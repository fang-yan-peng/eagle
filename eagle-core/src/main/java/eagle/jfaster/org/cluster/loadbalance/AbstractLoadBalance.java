package eagle.jfaster.org.cluster.loadbalance;

import eagle.jfaster.org.cluster.LoadBalance;
import eagle.jfaster.org.exception.EagleFrameException;
import eagle.jfaster.org.rpc.Refer;
import eagle.jfaster.org.rpc.Request;

import java.util.List;

/**
 * Created by fangyanpeng1 on 2017/8/4.
 */
public abstract class AbstractLoadBalance<T> implements LoadBalance<T> {

    public static final int MAX_REFER_COUNT = 10;

    protected List<Refer<T>> refers;

    @Override
    public void refresh(List<Refer<T>> refers) {
        this.refers = refers;
    }

    @Override
    public Refer<T> select(Request request) {
        List<Refer<T>> refers = this.refers;
        if(refers == null){
            throw new EagleFrameException("No alive refers to request,interfaceName:%s",request.getInterfaceName());
        }
        Refer<T> refer = null;
        if(refers.size() > 1){
            refer = doSelect(request);
        }else if(refers.size() == 1 && refers.get(0).isAlive()){
            refer =  refers.get(0);
        }
        if(refer != null){
            return  refer;
        }
        throw new EagleFrameException("No alive refers to request,interfaceName:%s",request.getInterfaceName());
    }

    public abstract Refer<T> doSelect(Request request);


}
