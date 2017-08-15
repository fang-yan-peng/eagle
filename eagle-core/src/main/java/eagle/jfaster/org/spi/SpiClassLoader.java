package eagle.jfaster.org.spi;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import static eagle.jfaster.org.constant.EagleConstants.*;
import eagle.jfaster.org.exception.EagleFrameException;
import eagle.jfaster.org.logging.InternalLogger;
import eagle.jfaster.org.logging.InternalLoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * spi类加载器，接口需要@Spi注解，实现类需要@SpiInfo注解
 *
 * Created by fangyanpeng1 on 2017/7/28.
 */
public class SpiClassLoader <T> {

    private final static InternalLogger logger = InternalLoggerFactory.getInstance(SpiClassLoader.class);


    private static Map<Class<?>,SpiClassLoader<?>> spiClassLoaders = Maps.newHashMap();

    private Map<String,T> singletonObjects = null;

    private Map<String,Class<T>> spiClasses = null;

    private Class<T> type;

    private static final String PREFIX = "META-INF/services/";

    private static ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    private AtomicBoolean init = new AtomicBoolean(false);

    private ClassLoader classLoader;

    public SpiClassLoader(Class<T> type){
        this(type,Thread.currentThread().getContextClassLoader());
    }

    public SpiClassLoader(Class<T> type,ClassLoader loader){
        this.type = type;
        this.classLoader = loader;
    }

    private void checkInit() {
        if (init.compareAndSet(false,true)) {
            loadExtensionClasses();
        }
    }

    public static <T> SpiClassLoader<T> getClassLoader(Class<T> type){
        checkInterfaceType(type);
        readWriteLock.readLock().lock();
        try {
            SpiClassLoader<T> loader = (SpiClassLoader<T>) spiClassLoaders.get(type);
            if(loader == null){
                readWriteLock.readLock().unlock();
                try {
                    readWriteLock.writeLock().lock();
                    loader = new SpiClassLoader(type);
                    spiClassLoaders.put(type,loader);
                } finally {
                    readWriteLock.readLock().lock();
                    readWriteLock.writeLock().unlock();
                }
            }
            return loader;
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    public T getExtension(String name) {
        checkInit();
        if (name == null) {
            return null;
        }
        try {
            Spi spi = type.getAnnotation(Spi.class);
            if (spi.scope() == Scope.SINGLETON) {
                return getSingletonInstance(name);
            } else {
                Class<T> clz = spiClasses.get(name);
                if (clz == null) {
                    return null;
                }
                //解决依赖，但不能解决循环依赖。
                SpiInfo info = clz.getAnnotation(SpiInfo.class);
                if(!Strings.isNullOrEmpty(info.dependency()) && info.dependencyType()!= Spi.class){
                    Object depend = SpiClassLoader.getClassLoader(info.dependencyType()).getExtension(info.dependency());
                    if(depend != null){
                        Constructor cto = clz.getConstructor(depend.getClass());
                        if(cto != null){
                            return (T) cto.newInstance(depend);
                        }
                    }
                }
                return clz.newInstance();
            }
        } catch (Exception e) {
            throw new  EagleFrameException(e);
        }
    }

    private T getSingletonInstance(String name) throws InstantiationException, IllegalAccessException {
        T obj = singletonObjects.get(name);
        if (obj != null) {
            return obj;
        }
        Class<T> clz = spiClasses.get(name);
        if (clz == null) {
            return null;
        }
        synchronized (singletonObjects) {
            obj = singletonObjects.get(name);
            if (obj != null) {
                return obj;
            }
            obj = clz.newInstance();
            singletonObjects.put(name, obj);
        }
        return obj;
    }

    private static <T> void checkInterfaceType(Class<T> clz) {
        if (clz == null) {
            throw new EagleFrameException("Error type is null");
        }

        if (!clz.isInterface()) {
            throw new EagleFrameException("Error %s is not a interface",clz.getName());
        }

        if (!isSpiType(clz)) {
            throw new EagleFrameException("Error %s is not spi type",clz.getName());
        }
    }

    private void checkSpiType(Class<T> clz) {
        checkClassPublic(clz);

        checkConstructorPublic(clz);

        checkClassInherit(clz);
    }

    private void checkClassInherit(Class<T> clz) {
        if (!type.isAssignableFrom(clz)) {
            throw new EagleFrameException("Error %s is not instanceof %s ",clz.getName(),type.getName());
        }
    }

    private void checkClassPublic(Class<T> clz) {
        if (!Modifier.isPublic(clz.getModifiers())) {
            throw new EagleFrameException("Error %s is not a public class ",clz.getName());
        }
    }

    private void checkConstructorPublic(Class<T> clz) {
        Constructor<?>[] constructors = clz.getConstructors();

        if (constructors == null || constructors.length == 0) {
            throw new EagleFrameException("Error %s has no public no-args constructor",clz.getName());
        }
    }

    private static <T> boolean isSpiType(Class<T> clz) {
        return clz.isAnnotationPresent(Spi.class);
    }

    public void addExtensionClass(Class<T> clz) {
        if (clz == null) {
            return;
        }

        checkInit();

        checkSpiType(clz);

        String spiName = getSpiName(clz);

        synchronized (spiClasses) {
            if (spiClasses.containsKey(spiName)) {
                throw new EagleFrameException("%s:Error spiName:%s already exist " , clz.getName(), spiName);
            } else {
                spiClasses.put(spiName, clz);
            }
        }
    }

    public Class<T> getExtensionClass(String spiName) {
        if (Strings.isNullOrEmpty(spiName)) {
            return null;
        }
        checkInit();
        return spiClasses.get(spiName);
    }

    private void loadExtensionClasses() {
        spiClasses = loadExtensionClasses(PREFIX);
        singletonObjects = Maps.newConcurrentMap();

    }

    private Map<String, Class<T>> loadExtensionClasses(String prefix) {
        String fullName = prefix + type.getName();
        List<String> classNames = new ArrayList<String>();

        try {
            Enumeration<URL> urls;
            if (classLoader == null) {
                urls = ClassLoader.getSystemResources(fullName);
            } else {
                urls = classLoader.getResources(fullName);
            }

            if (urls == null || !urls.hasMoreElements()) {
                return new ConcurrentHashMap();
            }

            while (urls.hasMoreElements()) {
                URL url = urls.nextElement();

                parseUrl(type, url, classNames);
            }
        } catch (Exception e) {
            throw new EagleFrameException(e);
        }

        return loadClass(classNames);
    }

    private Map<String, Class<T>> loadClass(List<String> classNames) {
        ConcurrentMap<String, Class<T>> map = new ConcurrentHashMap<String, Class<T>>();

        for (String className : classNames) {
            try {
                Class<T> clz;
                if (classLoader == null) {
                    clz = (Class<T>) Class.forName(className);
                } else {
                    clz = (Class<T>) Class.forName(className, true, classLoader);
                }

                checkSpiType(clz);

                String spiName = getSpiName(clz);

                if (map.containsKey(spiName)) {
                    throw new  EagleFrameException("%s:Error spiName already exist %s",clz.getName(),spiName);
                } else {
                    map.put(spiName, clz);
                }
            } catch (Exception e) {
                logger.error("Error load spi class", e);
            }
        }
        return map;
    }


    private String getSpiName(Class<?> clz) {
        SpiInfo spiMeta = clz.getAnnotation(SpiInfo.class);

        String name = (spiMeta != null && !"".equals(spiMeta.name())) ? spiMeta.name() : clz.getSimpleName();

        return name;
    }

    private void parseUrl(Class<T> type, URL url, List<String> classNames) throws ServiceConfigurationError {
        InputStream inputStream = null;
        BufferedReader reader = null;
        try {
            inputStream = url.openStream();
            reader = new BufferedReader(new InputStreamReader(inputStream, DEFAULT_CHARACTER));
            String line;
            int indexNumber = 0;
            while ((line = reader.readLine()) != null) {
                indexNumber++;
                parseLine(type, url, line, indexNumber, classNames);
            }
        } catch (Exception x) {
            logger.error("Error reading spi configuration file", x);
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException y) {
                logger.error("Error closing spi configuration file", y);
            }
        }
    }

    private void parseLine(Class<T> type, URL url, String line, int lineNumber, List<String> names) throws IOException,
            ServiceConfigurationError {
        int ci = line.indexOf('#');

        if (ci >= 0) {
            line = line.substring(0, ci);
        }

        line = line.trim();

        if (line.length() <= 0) {
            return;
        }

        if ((line.indexOf(' ') >= 0) || (line.indexOf('\t') >= 0)) {
            throw new EagleFrameException( "Illegal spi:%s configuration-file syntax lineNmber:%d url:%s",type.getName(), lineNumber,url.getPath());
        }

        int cp = line.codePointAt(0);
        if (!Character.isJavaIdentifierStart(cp)) {
            throw new EagleFrameException("Illegal spi provider-class name:%s,url:%s,lineNumber:%d,line:%s" ,type.getName(), url.getPath(), lineNumber,line);
        }

        for (int i = Character.charCount(cp); i < line.length(); i += Character.charCount(cp)) {
            cp = line.codePointAt(i);
            if (!Character.isJavaIdentifierPart(cp) && (cp != '.')) {
                throw new EagleFrameException("Illegal spi provider-class name:%s,url:%s,lineNumber:%d,line:%s " ,type.getName(), url.getPath(), lineNumber, line);
            }
        }

        if (!names.contains(line)) {
            names.add(line);
        }
    }
}
