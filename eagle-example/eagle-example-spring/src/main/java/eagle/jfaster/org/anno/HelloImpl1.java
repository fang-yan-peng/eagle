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

package eagle.jfaster.org.anno;

import eagle.jfaster.org.config.annotation.Service;
import eagle.jfaster.org.service.Hello;

/**
 * Created by fangyanpeng on 2017/8/18.
 */
@Service(baseService = "baseService",export = "proto:28001",version = "1.1")
public class HelloImpl1 implements Hello {

    public String hello() {
        return "hello eagle 1.1";
    }
}
