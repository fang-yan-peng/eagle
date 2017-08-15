package eagle.jfaster.org.rpc;

/**
 * Created by fangyanpeng1 on 2017/7/31.
 */
public interface Exporter<T> {
    RemoteInvoke<T> getInvoker();
    void init();
    void close();
}
