package eagle.jfaster.org.benchmark.impl;

import eagle.jfaster.org.benchmark.api.EagleBenchmarkService;
import eagle.jfaster.org.benchmark.pojo.Sex;
import eagle.jfaster.org.benchmark.pojo.User;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by fangyanpeng on 2017/9/8.
 */
public class EagleBenchmarkServiceImpl implements EagleBenchmarkService {

    private Map<Integer,User> users = new HashMap(){{
        User user1 = new User();
        user1.setId(1);
        user1.setName("fangyanpeng");
        user1.setAge(26);
        user1.setSex(Sex.MAN);
        put(user1.getId(),user1);

    }};

    @Override
    public String echo() {
        return "benchmark";
    }

    @Override
    public User getUserById(int id) {
        return users.get(id);
    }
}
