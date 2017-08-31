package eagle.jfaster.org.mock;

import eagle.jfaster.org.callback.CalculateDao;
import eagle.jfaster.org.rpc.Mock;

import javax.annotation.Resource;

/**
 * Created by fangyanpeng on 2017/8/29.
 */
public class CalculateMock implements Mock<Integer> {

    @Resource(name = "calculateDao")
    private CalculateDao calculateDao;

    @Override
    public Integer getMockValue(String interfaceName, String methodName, Object[] parameters, Throwable e) {
        calculateDao.insert();
        if("add".equals(interfaceName)){
            return (int)parameters[0]+(int)parameters[1];
        }else if("sub".equals(interfaceName)){
            return (int)parameters[0]+(int)parameters[1];
        }else if("div".equals(interfaceName)){
            return Integer.MAX_VALUE;
        }
        return 0;
    }

}
