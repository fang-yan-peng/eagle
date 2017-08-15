package eagle.jfaster.org.rpc;

import eagle.jfaster.org.config.common.MergeConfig;
/**
 *
 * Created by fangyanpeng1 on 2017/7/29.
 */
public interface RemoteInvoke <T> {

    Response invoke(Request request);

    MergeConfig getConfig();

    Class<T> getInterface();


}
