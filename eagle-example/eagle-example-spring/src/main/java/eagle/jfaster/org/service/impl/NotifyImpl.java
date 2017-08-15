package eagle.jfaster.org.service.impl;

import eagle.jfaster.org.Pong;
import eagle.jfaster.org.service.Notify;
import org.springframework.stereotype.Service;

/**
 * Created by fangyanpeng1 on 2017/8/9.
 */
@Service("notify")
public class NotifyImpl implements Notify {


    public Pong ping(String content) {
        Pong pong = new Pong();
        pong.content = content;
        pong.time = System.currentTimeMillis();
        return pong;
    }

    public void invoke(String content) {
        System.out.println(content);
    }
}
