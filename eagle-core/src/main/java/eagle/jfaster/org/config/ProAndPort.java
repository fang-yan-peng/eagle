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

package eagle.jfaster.org.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * 协议与端口信息
 *
 * Created by fangyanpeng1 on 2017/8/8.
 */
@RequiredArgsConstructor
public class ProAndPort {

    @Setter
    @Getter
    private final String protocolId;

    @Setter
    @Getter
    private final int port;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        ProAndPort that = (ProAndPort) o;

        if (port != that.port)
            return false;
        return protocolId.equals(that.protocolId);

    }

    @Override
    public int hashCode() {
        int result = protocolId.hashCode();
        result = 31 * result + port;
        return result;
    }
}
