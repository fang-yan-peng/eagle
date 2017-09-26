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

package eagle.jfaster.org.protocol;

import eagle.jfaster.org.rpc.Exporter;
import eagle.jfaster.org.rpc.RemoteInvoke;
import eagle.jfaster.org.transport.Server;
import lombok.RequiredArgsConstructor;

/**
 * Created by fangyanpeng1 on 2017/7/31.
 */
@RequiredArgsConstructor
public class NettyRpcExporter<T> implements Exporter<T> {

    private final RemoteInvoke<T> invoker;

    private final Server server;

    @Override
    public RemoteInvoke<T> getInvoker() {
        return invoker;
    }

    @Override
    public void init() {

    }

    @Override
    public void close() {
        server.shutdown();
    }
}
