/*
 * Copyright 2026-? the original author or authors.
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


package top.osjf.cron.core.micrometer;

import top.osjf.commons.lang.Nullable;
import top.osjf.commons.util.ClassUtils;

/**
 * Detects whether micrometer MeterRegistry exists in classpath.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.2
 */
public abstract class MeterRegistryDetector {

    /**
     * Check whether micrometer {@code MeterRegistry} is present on classpath.
     *
     * @param classLoader the target classloader to detect, may be {@code null}.
     * @return {@code true} if MeterRegistry class exists, otherwise {@code false}.
     */
    public static boolean isPresent(@Nullable ClassLoader classLoader) {
        return ClassUtils.isPresent("io.micrometer.core.instrument.MeterRegistry", classLoader);
    }
}
