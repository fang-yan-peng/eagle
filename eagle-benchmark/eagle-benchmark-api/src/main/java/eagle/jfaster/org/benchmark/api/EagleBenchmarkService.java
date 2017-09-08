package eagle.jfaster.org.benchmark.api;

import eagle.jfaster.org.benchmark.pojo.User;

/**
 *
 * Created by fangyanpeng on 2017/9/8.
 */
public interface EagleBenchmarkService {

    String echo();

    User getUserById(int id);

}
