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

import top.osjf.cron.core.lang.Nullable;
import top.osjf.cron.core.lifecycle.SuperiorProperties;

/**
 * Simple {@link SuperiorProperties} related tool classes.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.3
 */
public abstract class SuperiorPropertiesUtils {

    @Nullable
    public static SuperiorProperties compositeSuperiorProperties(SuperiorProperties s1, SuperiorProperties s2) {
        if (s1 == null && s2 == null) {
            return null;
        } else if (s1 == null) {
            return s2;
        } else if (s2 == null) {
            return s1;
        }
        s1.addProperties(s2);
        return s1;
    }
}
