package eagle.jfaster.org.listener;

import com.google.common.base.Charsets;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;

/**
 * Created by fangyanpeng1 on 2017/8/4.
 */
public abstract class AbstractChildrenDataListener implements PathChildrenCacheListener {
    @Override
    public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
        ChildData childData = event.getData();
        if (null == childData) {
            return;
        }
        String path = childData.getPath();
        if (path.isEmpty()) {
            return;
        }
        dataChanged(path, event.getType(), null == childData.getData() ? "" : new String(childData.getData(), Charsets.UTF_8));
    }

    protected abstract void dataChanged(final String path, final PathChildrenCacheEvent.Type eventType, final String data);
}
