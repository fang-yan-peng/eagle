package eagle.jfaster.org.repository.impl;

import eagle.jfaster.org.exception.ServiceConsoleException;
import eagle.jfaster.org.repository.XmlRepository;
import eagle.jfaster.org.util.HomeFolderUtils;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.File;

/**
 * 基于XML的数据访问器实现类.
 * 
 * @param <E> 数据类型
 * @author fangyanpeng
 */
public abstract class AbstractXmlRepositoryImpl<E> implements XmlRepository<E> {
    
    private final File file;
    
    private final Class<E> clazz;
    
    private JAXBContext jaxbContext;
    
    protected AbstractXmlRepositoryImpl(final String fileName, final Class<E> clazz) {
        file = new File(HomeFolderUtils.getFilePathInHomeFolder(fileName));
        this.clazz = clazz;
        HomeFolderUtils.createHomeFolderIfNotExisted();
        try {
            jaxbContext = JAXBContext.newInstance(clazz);
        } catch (final JAXBException ex) {
            throw new ServiceConsoleException(ex);
        }
    }
    
    @Override
    public synchronized E load() {
        if (!file.exists()) {
            try {
                return clazz.newInstance();
            } catch (final InstantiationException | IllegalAccessException ex) {
                throw new ServiceConsoleException(ex);
            }
        }
        try {
            @SuppressWarnings("unchecked")
            E result = (E) jaxbContext.createUnmarshaller().unmarshal(file);
            return result;
        } catch (final JAXBException ex) {
            throw new ServiceConsoleException(ex);
        }
    }
    
    @Override
    public synchronized void save(final E entity) {
        try {
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(entity, file);
        } catch (final JAXBException ex) {
            throw new ServiceConsoleException(ex);
        }
    }
}
