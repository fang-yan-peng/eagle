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
package eagle.jfaster.org.service.impl;

import eagle.jfaster.org.config.annotation.Refer;
import eagle.jfaster.org.config.annotation.Service;
import eagle.jfaster.org.service.City;
import eagle.jfaster.org.service.Hello;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * Created by fangyanpeng on 2017/12/17.
 */
@Service(baseService = "baseService")
public class HelloImpl implements Hello{

    private static final Logger logger = LoggerFactory.getLogger(HelloImpl.class);

    @Refer(baseRefer = "baseRefer")
    private City city;

    @Override
    public String hello(int code) {
        String cityName = city.getCityName(code);
        logger.info("execute hello {}",cityName);
        return "hello " + cityName;
    }
}
