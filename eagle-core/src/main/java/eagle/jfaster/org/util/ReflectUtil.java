/*
 * Copyright 2017 eagle.jfaster.org.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * </p>
 */

package eagle.jfaster.org.util;

import com.google.common.base.Strings;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 反射处理类
 *
 * Created by fangyanpeng1 on 2017/7/28.
 */
public class ReflectUtil {

    public static final String PARAM_CLASS_SPLIT = ",";

    public static final String METHOD_DESC_VOID = "%s()";

    public static final String METHOD_DESC_PARAM = "%s(%s)";

    private static final Class<?>[] EMPTY_CLASS_ARRAY = new Class<?>[0];

    private static final ConcurrentMap<String, Class<?>> name2ClassCache = new ConcurrentHashMap<String, Class<?>>();

    private static final ConcurrentMap<Class<?>, String> class2NameCache = new ConcurrentHashMap<Class<?>, String>();

    private static final String[] PRIMITIVE_NAMES = new String[]{"boolean", "byte", "char", "double", "float", "int",
            "long", "short",
            "void"};

    private static final Class<?>[] PRIMITIVE_CLASSES = new Class[]{boolean.class, byte.class, char.class,
            double.class, float.class,
            int.class, long.class, short.class, Void.TYPE};

    private static final int PRIMITIVE_CLASS_NAME_MAX_LENGTH = 7;

    /**
     * 获取method方式的接口参数，以逗号分割，拼接clz列表。 如果没有参数，那么void表示
     *
     * @param method
     * @return
     */
    public static String getMethodParamDesc(Method method) {
        if (method.getParameterTypes() == null || method.getParameterTypes().length == 0) {
            return null;
        }

        StringBuilder builder = new StringBuilder();

        Class<?>[] clzs = method.getParameterTypes();

        for (Class<?> clz : clzs) {
            String className = getName(clz);
            builder.append(className).append(PARAM_CLASS_SPLIT);
        }

        return builder.substring(0, builder.length() - 1);
    }

    /**
     * 获取方法的标示 : method_name + "(" + paramDesc + ")"
     *
     * @param method
     * @return
     */
    public static String getMethodDesc(Method method) {
        String methodParamDesc = getMethodParamDesc(method);
        return getMethodDesc(method.getName(), methodParamDesc);
    }

    /**
     * 获取方法的标示 : method_name + "(" + paramDesc + ")"
     *
     * @param
     * @return
     */
    public static String getMethodDesc(String methodName, String paramDesc) {
        if (paramDesc == null) {
            return String.format(METHOD_DESC_VOID, methodName);
        } else {
            return String.format(METHOD_DESC_PARAM, methodName, paramDesc);
        }
    }

    public static Class<?>[] forNames(String classList) throws ClassNotFoundException {
        if (Strings.isNullOrEmpty(classList)) {
            return EMPTY_CLASS_ARRAY;
        }

        String[] classNames = classList.split(PARAM_CLASS_SPLIT);
        Class<?>[] classTypes = new Class<?>[classNames.length];
        for (int i = 0; i < classNames.length; i++) {
            String className = classNames[i];
            classTypes[i] = forName(className);
        }

        return classTypes;
    }

    public static Class<?> forName(String className) throws ClassNotFoundException {
        if (null == className || "".equals(className)) {
            return null;
        }

        Class<?> clz = name2ClassCache.get(className);

        if (clz != null) {
            return clz;
        }

        clz = forNameWithoutCache(className);

        name2ClassCache.putIfAbsent(className, clz);

        return clz;
    }

    private static Class<?> forNameWithoutCache(String className) throws ClassNotFoundException {
        if (!className.endsWith("[]")) { // not array
            Class<?> clz = getPrimitiveClass(className);

            clz = (clz != null) ? clz : Class.forName(className, true, Thread.currentThread().getContextClassLoader());
            return clz;
        }

        int dimensionSiz = 0;

        while (className.endsWith("[]")) {
            dimensionSiz++;

            className = className.substring(0, className.length() - 2);
        }

        int[] dimensions = new int[dimensionSiz];

        Class<?> clz = getPrimitiveClass(className);

        if (clz == null) {
            clz = Class.forName(className, true, Thread.currentThread().getContextClassLoader());
        }

        return Array.newInstance(clz, dimensions).getClass();
    }

    /**
     * 需要支持一维数组、二维数组等
     *
     * @param
     * @return
     */
    public static String getName(Class<?> clz) {
        if (clz == null) {
            return null;
        }

        String className = class2NameCache.get(clz);

        if (className != null) {
            return className;
        }

        className = getNameWithoutCache(clz);

        // 与name2ClassCache同样道理，如果没有恶心的代码，这块内存大小应该可控
        class2NameCache.putIfAbsent(clz, className);

        return className;
    }

    private static String getNameWithoutCache(Class<?> clz) {
        if (!clz.isArray()) {
            return clz.getName();
        }

        StringBuilder sb = new StringBuilder();
        while (clz.isArray()) {
            sb.append("[]");
            clz = clz.getComponentType();
        }

        return clz.getName() + sb.toString();
    }

    public static Class<?> getPrimitiveClass(String name) {
        // check if is primitive class
        if (name.length() <= PRIMITIVE_CLASS_NAME_MAX_LENGTH) {
            int index = Arrays.binarySearch(PRIMITIVE_NAMES, name);
            if (index >= 0) {
                return PRIMITIVE_CLASSES[index];
            }
        }
        return null;
    }

    /**
     * 获取clz public method
     * <p>
     * <pre>
     *      1）不包含构造函数
     *      2）不包含Object.class
     *      3）包含该clz的父类的所有public方法
     * </pre>
     *
     * @param clz
     * @return
     */
    public static List<Method> getPublicMethod(Class<?> clz) {
        Method[] methods = clz.getMethods();
        List<Method> ret = new ArrayList<Method>();

        for (Method method : methods) {

            boolean isPublic = Modifier.isPublic(method.getModifiers());
            boolean isNotObjectClass = method.getDeclaringClass() != Object.class;

            if (isPublic && isNotObjectClass) {
                ret.add(method);
            }
        }

        return ret;
    }

    public static Object[] getParameterDefaultVals(Method method) {
        Class[] clzs = method.getParameterTypes();
        if (clzs == null || clzs.length == 0) {
            return new Object[0];
        }
        Object[] vals = new Object[clzs.length];
        for (int i = 0; i < clzs.length; ++i) {
            vals[i] = PrimitiveDefault.getDefaultValue(clzs[i]);
        }
        return vals;

    }

    public static Object getDefaultReturnValue(Class<?> returnType) {
        return PrimitiveDefault.getDefaultValue(returnType);
    }

    private static class PrimitiveDefault {
        private static boolean defaultBoolean;
        private static char defaultChar;
        private static byte defaultByte;
        private static short defaultShort;
        private static int defaultInt;
        private static long defaultLong;
        private static float defaultFloat;
        private static double defaultDouble;

        private static Map<Class<?>, Object> primitiveValues = new HashMap<Class<?>, Object>();

        static {
            primitiveValues.put(boolean.class, defaultBoolean);
            primitiveValues.put(char.class, defaultChar);
            primitiveValues.put(byte.class, defaultByte);
            primitiveValues.put(short.class, defaultShort);
            primitiveValues.put(int.class, defaultInt);
            primitiveValues.put(long.class, defaultLong);
            primitiveValues.put(float.class, defaultFloat);
            primitiveValues.put(double.class, defaultDouble);
        }

        public static Object getDefaultValue(Class<?> valType) {
            return primitiveValues.get(valType);
        }

    }

}