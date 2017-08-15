package eagle.jfaster.org.rpc;

import eagle.jfaster.org.cluster.ReferCluster;
import eagle.jfaster.org.cluster.cluster.ReferClusterManage;
import eagle.jfaster.org.config.common.MergeConfig;
import eagle.jfaster.org.spi.Scope;
import eagle.jfaster.org.spi.Spi;
import java.util.List;

/**
 * Created by fangyanpeng1 on 2017/8/6.
 */
@Spi(scope = Scope.SINGLETON)
public interface RpcHandler {

    <T> ReferClusterManage<T> buildClusterManage(Class<T> interfaceClass, MergeConfig refConfig,List<MergeConfig> registryConfigs);

    <T> T refer(Class<T> interfaceClass, List<ReferCluster<T>> clusters);

    <T> Exporter<T> export(Class<T> interfaceClass, T ref, MergeConfig serviceConfig,List<MergeConfig> registryConfigs);

    <T> void unexport(List<Exporter<T>> exporters, List<MergeConfig> registryConfigs);

    <T> void unRef(List<ReferClusterManage<T>> clusterManages);
}
