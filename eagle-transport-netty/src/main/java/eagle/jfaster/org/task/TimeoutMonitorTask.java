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

package eagle.jfaster.org.task;

import eagle.jfaster.org.client.NettyClient;
import eagle.jfaster.org.client.NettyResponseFuture;
import eagle.jfaster.org.exception.EagleFrameException;
import eagle.jfaster.org.exception.EagleTimeoutException;
import eagle.jfaster.org.logging.InternalLogger;
import eagle.jfaster.org.logging.InternalLoggerFactory;
import lombok.RequiredArgsConstructor;

import java.util.Iterator;
import java.util.Map;

/**
 * Created by fangyanpeng on 2017/8/22.
 */

@RequiredArgsConstructor
public class TimeoutMonitorTask implements Runnable {

    private final static InternalLogger logger = InternalLoggerFactory.getInstance(TimeoutMonitorTask.class);

    private final NettyClient client;

    @Override
    public void run() {
        Iterator<Map.Entry<Integer, NettyResponseFuture>> it = client.getCallbackMap().entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, NettyResponseFuture> next = it.next();
            NettyResponseFuture rep = next.getValue();
            if ((rep.getBeginTimestamp() + rep.getTimeoutMillis() + 300) <= System.currentTimeMillis()) {
                if (rep.getCallBack() == null) {
                    rep.onFail(new EagleTimeoutException("%s request timeout，requestid:%d,timeout:%d ms", client
                            .getConfig().getInterfaceName(), rep.getOpaque(), rep.getTimeoutMillis()));
                } else {
                    rep.setException(new EagleTimeoutException("%s request timeout，requestid:%d,timeout:%d ms",
                            client.getConfig().getInterfaceName(), rep.getOpaque(), rep.getTimeoutMillis()));
                    client.executeInvokeCallback(rep);
                }
                it.remove();
                logger.warn("remove timeout request, interfaceName: " + client.getConfig().getInterfaceName());
            }
        }
    }
}
