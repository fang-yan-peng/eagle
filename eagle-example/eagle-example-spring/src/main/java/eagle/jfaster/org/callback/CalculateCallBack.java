package eagle.jfaster.org.callback;

import eagle.jfaster.org.rpc.MethodInvokeCallBack;
import javax.annotation.Resource;

/**
 * Created by fangyanpeng1 on 2017/8/9.
 */
public class CalculateCallBack implements MethodInvokeCallBack<Integer> {

    @Resource
    CalculateDao calculateDao;

    public void onSuccess(Integer response) {
        calculateDao.insert();
        System.out.println("calculate res:"+response);
    }

    public void onFail(Exception e) {
        e.printStackTrace();
    }
}
