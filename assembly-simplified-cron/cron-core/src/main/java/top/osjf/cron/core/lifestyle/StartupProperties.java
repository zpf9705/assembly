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

package top.osjf.cron.core.lifestyle;

import java.util.Map;
import java.util.Properties;

/**
 * StartupProperties interface provides methods for managing startup properties.
 * <p>
 * It allows retrieving the current startup properties, adding new properties, and
 * initializing properties from various sources such as Maps, Properties objects,
 * or other StartupProperties instances.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.1
 */
public interface StartupProperties {

    /**
     * Retrieves the current startup properties.
     *
     * @return The current startup properties as a {@link Properties} object.
     */
    Properties getStartUpProperties();

    /**
     * Adds a set of new properties to the startup properties.
     *
     * @param properties The new properties to add, represented as a {@link Properties} object.
     */
    void addStartupProperties(Properties properties);

    /**
     * Adds a set of new properties to the startup properties, sourced from a Map.
     *
     * @param properties A Map containing the new properties to add, where the keys
     *                   are Strings and the values can be any Object type.
     */
    void addStartupProperties(Map<String, Object> properties);

    /**
     * Merges the properties from another StartupProperties instance into this instance.
     *
     * @param properties Another StartupProperties instance whose properties will be
     *                   merged into this instance.
     */
    void addStartupProperties(StartupProperties properties);

    /**
     * Creates a new, empty StartupProperties instance.
     *
     * @return A new, empty StartupProperties instance.
     */
    static StartupProperties of() {
        return new DefaultStartupProperties();
    }

    /**
     * Creates a new StartupProperties instance initialized with the properties from the provided Map.
     *
     * @param properties A Map containing the initial properties, where the keys are Strings and
     *                   the values can be any Object type.
     * @return A new StartupProperties instance initialized with the provided properties.
     */
    static StartupProperties of(Map<String, Object> properties) {
        return new DefaultStartupProperties(properties);
    }

    /**
     * Creates a new StartupProperties instance initialized with the properties from
     * the provided {@link Properties} object.
     *
     * @param properties The initial properties, represented as a {@link Properties} object.
     * @return A new StartupProperties instance initialized with the provided properties.
     */
    static StartupProperties of(Properties properties) {
        return new DefaultStartupProperties(properties);
    }

    /**
     * Default impl for {@link StartupProperties}.
     */
    class DefaultStartupProperties implements StartupProperties {

        private final Properties properties = new Properties();

        public DefaultStartupProperties() {
        }

        public DefaultStartupProperties(Map<String, Object> properties) {
            addStartupProperties(properties);
        }

        public DefaultStartupProperties(Properties properties) {
            addStartupProperties(properties);
        }

        @Override
        public Properties getStartUpProperties() {
            return properties;
        }

        @Override
        public void addStartupProperties(Properties properties) {
            this.properties.putAll(properties);
        }

        @Override
        public void addStartupProperties(Map<String, Object> properties) {
            this.properties.putAll(properties);
        }

        @Override
        public void addStartupProperties(StartupProperties properties) {
            addStartupProperties(properties.getStartUpProperties());
        }
    }
}
