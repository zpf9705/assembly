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
 * The {@code StartupProperties} interface defines a set of methods
 * for managing and manipulating startup parameters.
 *
 * <p>These parameters are typically used to configure scheduled tasks
 * or other components initialized at application startup, and it allows
 * retrieving the current startup properties, adding new properties,
 * and initializing properties from various sources such as {@code Map},
 * {@code Properties} objects, or other {@code StartupProperties} instances.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.1
 */
public interface StartupProperties {

    /* --------------------------------------------------------------------------- */
    /* -------------------------- conventional method ---------------------------- */
    /* --------------------------------------------------------------------------- */

    /**
     * Retrieve the value of the specified attribute, and return the default value if the
     * attribute does not exist.
     *
     * @param propertyName name of the attribute to be obtained. This name should match the
     *                     property name defined in the object.
     * @param <T>          generic type, representing the type of attribute value and the type
     *                     of default value.
     * @return Returns the value of the specified property, and if the property does not exist,
     * returns the default value passed in.
     * @throws NullPointerException if input propertyName is null.
     * @since 1.0.3
     */
    <T> T getProperty(String propertyName, T def);

    /**
     * Adds a property to the property collection with a string key and string value.
     *
     * @param propertyKey   the key of the property, used to uniquely identify the property.
     *                      Must be of type String.
     * @param propertyValue the value associated with the key. Must be of type String.
     * @throws NullPointerException if input key or value is null.
     * @since 1.0.3
     */
    void addProperty(String propertyKey, String propertyValue);

    /**
     * Adds a property to the property collection with a string key and an object value.
     *
     * @param propertyKey   the key of the property, used to uniquely identify the property.
     *                      Must be of type String.
     * @param propertyValue the value associated with the key. Can be any type of object.
     * @throws NullPointerException if input key or value is null.
     * @since 1.0.3
     */
    void addProperty(String propertyKey, Object propertyValue);

    /**
     * Adds a property to the property collection where both the key and value can be any type
     * of object.
     *
     * @param propertyKey   the key of the property, used to uniquely identify the property.
     *                      Can be any type of object.
     * @param propertyValue the value associated with the key. Can be any type of object.
     * @throws NullPointerException if input key or value is null.
     * @since 1.0.3
     */
    void addProperty(Object propertyKey, Object propertyValue);

    /**
     * Add a set of startup parameters through a {@code Properties} object.
     *
     * @param properties the {@code Properties} object containing the startup
     *                   parameters to be added.
     * @throws NullPointerException if input properties is null.
     */
    void addProperties(Properties properties);

    /**
     * Add a set of startup parameters through a {@code Map}.
     *
     * @param map a {@code Map} containing the startup parameters to be added,
     *            where the key is of string type and the value is of object type.
     * @throws NullPointerException if input map is null.
     */
    void addProperties(Map<String, Object> map);

    /**
     * Copy the startup parameters from another {@code StartupProperties} object.
     *
     * @param startupProperties another {@code StartupProperties} instance to merge.
     * @throws NullPointerException if input startupProperties is null.
     */
    void addProperties(StartupProperties startupProperties);

    /**
     * Convert all properties in the current {@code StartupProperties} into a
     * {@code Properties} object.
     *
     * @return the {@code Properties} object that contains all the properties in
     * the current instance.
     */
    Properties asProperties();

    /* --------------------------------------------------------------------------- */
    /* -------------------------- static factory method -------------------------- */
    /* --------------------------------------------------------------------------- */

    /**
     * Creates a new {@code StartupProperties} without args.
     *
     * @return a without args {@code StartupProperties} instance.
     */
    static StartupProperties of() {
        return new DefaultStartupProperties();
    }

    /**
     * Creates a new {@code StartupProperties} instance initialized with the
     * properties from the provided Map.
     *
     * @param map a {@code Map} containing the initial properties, where the
     *            keys are Strings and the values can be any Object type.
     * @return a new {@code StartupProperties}  instance initialized with the {@code Map}.
     */
    static StartupProperties of(Map<String, Object> map) {
        return new DefaultStartupProperties(map);
    }

    /**
     * Creates a new {@code StartupProperties} instance initialized with the
     * properties from the provided {@code Properties} object.
     *
     * @param properties the initial properties, represented as a {@code Properties}
     *                   object.
     * @return a new {@code StartupProperties} instance initialized with the provided
     * {@code Properties}.
     */
    static StartupProperties of(Properties properties) {
        return new DefaultStartupProperties(properties);
    }

    /* --------------------------------------------------------------------------- */
    /* ------------------------------ default impl ------------------------------- */
    /* --------------------------------------------------------------------------- */

    /**
     * Default impl for {@link StartupProperties}.
     */
    class DefaultStartupProperties implements StartupProperties {

        private final Properties properties = new Properties();

        DefaultStartupProperties() {
        }

        DefaultStartupProperties(Map<String, Object> map) {
            properties.putAll(map);
        }

        DefaultStartupProperties(Properties properties) {
            this.properties.putAll(properties);
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T> T getProperty(String propertyName, T def) {
            Object o = properties.get(propertyName);
            if (o == null) {
                return def;
            }
            return (T) o;
        }

        @Override
        public void addProperty(String propertyKey, String propertyValue) {
            properties.setProperty(propertyKey, propertyValue);
        }

        @Override
        public void addProperty(String propertyKey, Object propertyValue) {
            properties.put(propertyKey, propertyValue);
        }

        @Override
        public void addProperty(Object propertyKey, Object propertyValue) {
            properties.put(propertyKey, propertyValue);
        }

        @Override
        public void addProperties(Properties properties) {
            this.properties.putAll(properties);
        }

        @Override
        public void addProperties(Map<String, Object> properties) {
            this.properties.putAll(properties);
        }

        @Override
        public void addProperties(StartupProperties properties) {
            this.properties.putAll(properties.asProperties());
        }

        @Override
        public Properties asProperties() {
            return properties;
        }
    }
}
