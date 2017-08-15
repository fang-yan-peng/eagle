package eagle.jfaster.org.service.impl;

import eagle.jfaster.org.service.Calculate;
import org.springframework.stereotype.Service;

/**
 * Created by fangyanpeng1 on 2017/8/9.
 */
@Service("calculate")
public class CalculateImpl implements Calculate {

    public int add(int a, int b) {
        return a+b;
    }

    public int sub(int a, int b) {
        return a-b;
    }
}
