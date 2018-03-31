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

package eagle.jfaster.org.codec;

import eagle.jfaster.org.spi.Scope;
import eagle.jfaster.org.spi.Spi;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by fangyanpeng1 on 2017/7/28.
 */
@Spi(scope = Scope.PROTOTYPE)
public interface Codec {

    ByteBuffer encode(Object message, Serialization serialization) throws IOException;

    Object decode(ByteBuffer buffer, Serialization serialization, int opaque, short magicCode) throws IOException;
}
