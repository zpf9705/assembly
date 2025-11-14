/*
 * Copyright 2025-? the original author or authors.
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


package top.osjf.cron.datasource.driven.scheduled.nacosconfig;

/**
 * Configuration format provider interface, used to provide the format type of configuration content.
 *
 * <p>Classes implementing this interface can return the corresponding {@link ConfigFormat}
 * via the {@link #getConfigFormat()} method, indicating the data format used by the provided
 * configuration content.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.2
 */
public interface ConfigFormatProvider {

    /**
     * Gets the configuration format of the current provider.
     * @return the configuration format enum value, must not be {@literal null}.
     */
    ConfigFormat getConfigFormat();
}
