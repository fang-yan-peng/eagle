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

import eagle.jfaster.org.config.annotation.Service;
import eagle.jfaster.org.service.City;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by fangyanpeng on 2017/12/17.
 */
@Service(baseService = "baseService")
public class CityImpl implements City{

    private static final Logger logger = LoggerFactory.getLogger(CityImpl.class);


    private Map<Integer,String> cityNames = new HashMap(){
        {
            this.put(1,"北京");
            this.put(2,"上海");
            this.put(3,"广州");
            this.put(4,"深圳");
        }
    };

    @Override
    public String getCityName(int code) {
        logger.info("execute get city name by {}",code);
        return cityNames.get(code);
    }
}
