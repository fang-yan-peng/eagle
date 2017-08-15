package eagle.jfaster.org.service;

import eagle.jfaster.org.Pong;

/**
 * Created by fangyanpeng1 on 2017/8/9.
 */
public interface Notify {

    Pong ping(String content);

    void invoke(String content);
}
