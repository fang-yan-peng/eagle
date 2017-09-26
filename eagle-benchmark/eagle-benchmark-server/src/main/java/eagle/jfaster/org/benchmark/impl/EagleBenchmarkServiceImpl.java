/*
 * Copyright 2017 eagle.jfaster.org.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * </p>
 */

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

    public String echo() {
        return "benchmark";
    }

    public User getUserById(int id) {
        return users.get(id);
    }
}
