/*
 * Copyright 2024-? the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package top.osjf.cron.spring;

import top.osjf.commons.lang.Nullable;
import top.osjf.cron.core.lifecycle.InitializeProperties;

/**
 * Simple {@link InitializeProperties} related tool classes.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.2
 */
public abstract class PropertiesUtils {

    @Nullable
    public static InitializeProperties compositeSuperiorProperties(InitializeProperties s1, InitializeProperties s2) {
        if (s1 == null && s2 == null) {
            return null;
        } else if (s1 == null) {
            return s2;
        } else if (s2 == null) {
            return s1;
        }
        s1.mergeFrom(s2);
        return s1;
    }
}
