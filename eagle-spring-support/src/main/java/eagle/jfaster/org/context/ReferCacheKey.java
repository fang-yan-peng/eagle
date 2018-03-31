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
package eagle.jfaster.org.context;

import eagle.jfaster.org.config.annotation.Refer;

import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

/**
 * Created by fangyanpeng on 2017/12/17.
 */
public class ReferCacheKey {

    private final Class<?> targetClass;

    private final Refer refer;

    public ReferCacheKey(Class<?> targetClass, Refer refer) {
        Assert.notNull(refer, "refer must be set.");
        this.targetClass = targetClass;
        this.refer = refer;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof ReferCacheKey)) {
            return false;
        }
        ReferCacheKey otherKey = (ReferCacheKey) other;
        return (this.refer.equals(otherKey.refer) && ObjectUtils.nullSafeEquals(this.targetClass,
                otherKey.targetClass));
    }

    @Override
    public int hashCode() {
        return this.refer.hashCode() * 29 + (this.targetClass != null ? this.targetClass.hashCode() : 0);
    }
}
