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

package eagle.jfaster.org.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import eagle.jfaster.org.interceptor.context.CurrentExecutionContext;

/**
 * Created by fangyanpeng1 on 2018/3/31.
 */
@Service("clientInterceptor")
public class ClientInterceptor implements ExecutionInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(ClientInterceptor.class);

    @Override
    public void onBefore(String interfaceName, String method, Object[] args) {
        logger.info("{}.{} start....", interfaceName, method);
        CurrentExecutionContext.setVariable("begin", System.nanoTime());
    }

    @Override
    public void onAfter(String interfaceName, String method, Object[] args) {
        logger.info("{}.{} end....", interfaceName, method);
        long starTime = (long) CurrentExecutionContext.getVariable("begin");
        logger.info("{}.{} spent {} ns", interfaceName, method, System.nanoTime() - starTime);
    }

    @Override
    public void onError(String interfaceName, String method, Object[] args, Throwable e) {
        logger.info("{}.{} error....", interfaceName, method, e);
        long starTime = (long) CurrentExecutionContext.getVariable("begin");
        logger.info("{}.{} spent {} ns", interfaceName, method, System.nanoTime() - starTime);
    }
}
