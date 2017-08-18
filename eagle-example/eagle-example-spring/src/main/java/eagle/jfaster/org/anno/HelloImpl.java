package eagle.jfaster.org.anno;

import eagle.jfaster.org.config.annotation.Service;
import eagle.jfaster.org.service.Hello;

/**
 * Created by fangyanpeng on 2017/8/18.
 */
@Service(baseService = "baseService",export = "proto:28000")
public class HelloImpl implements Hello {

    public String hello() {
        return "hello eagle";
    }
}
