package eagle.jfaster.org.service;

import eagle.jfaster.org.config.annotation.Refer;

/**
 * Created by fangyanpeng on 2017/8/18.
 */
@Refer(id = "helloAnno",baseRefer = "baseRefer")
public interface Hello {
    String hello();
}
