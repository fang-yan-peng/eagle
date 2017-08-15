package eagle.jfaster.org.service.impl;

import eagle.jfaster.org.service.HelloWorld;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fangyanpeng1 on 2017/8/9.
 */

@Service("hello")
public class HelloWorldImpl implements HelloWorld {

    public String hello() {
        return "Hello World";
    }

    public List<String> hellos() {
        List<String> echo = new ArrayList<String>();
        echo.add("Hello world");
        echo.add("Hello Beijing");
        return echo;
    }
}
