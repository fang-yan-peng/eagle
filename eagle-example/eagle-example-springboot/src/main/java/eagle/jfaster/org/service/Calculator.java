package eagle.jfaster.org.service;

import eagle.jfaster.org.config.annotation.Refer;
import org.springframework.stereotype.Service;

/**
 * Created by fangyanpeng on 2017/10/24.
 */
@Service
public class Calculator {

    @Refer(baseRefer = "baseRefer")
    public Calculate calculate;

}
