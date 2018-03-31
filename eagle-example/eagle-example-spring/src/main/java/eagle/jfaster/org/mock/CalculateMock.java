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
        if ("add".equals(interfaceName)) {
            return (int) parameters[0] + (int) parameters[1];
        } else if ("sub".equals(interfaceName)) {
            return (int) parameters[0] + (int) parameters[1];
        } else if ("div".equals(interfaceName)) {
            return Integer.MAX_VALUE;
        }
        return 0;
    }

}
