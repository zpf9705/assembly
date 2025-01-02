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


package top.osjf.cron.core.lifecycle;

import java.util.Map;
import java.util.Properties;

/**
 * The {@code DefaultSuperiorProperties} class serves as the default implementation of the
 * {@code SuperiorProperties} interface.
 * By encapsulating Java's Properties class, this class offers a flexible and type-safe means
 * of managing properties.
 *
 * <p>It supports multiple constructors, allowing initialization of the property collection via
 * a Map, Properties object, or direct parameters. A range of methods are provided for retrieving
 * and adding properties, ensuring that type-safe property retrieval is achieved through the generic
 * method {@link #getProperty},which allows for specifying a default value to be returned if the
 * property does not exist, preventing null pointer exceptions. Additionally, adding properties supports
 * both string and object forms.
 *
 * <p>This class leverages a generic method, getProperty, to implement type-safe property retrieval,
 * enabling the specification of a default value to be returned when a property is not found.
 *
 * <p>It can be utilized in scenarios such as configuration management and property file loading, serving
 * as a powerful tool for managing key-value pair properties in Java applications.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.3
 */
class DefaultSuperiorProperties implements SuperiorProperties {

    private final Properties properties = new Properties();

    public DefaultSuperiorProperties() {
    }

    public DefaultSuperiorProperties(Map<String, Object> map) {
        properties.putAll(map);
    }

    public DefaultSuperiorProperties(Properties properties) {
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
    public void addProperties(SuperiorProperties properties) {
        this.properties.putAll(properties.asProperties());
    }

    @Override
    public Properties asProperties() {
        return properties;
    }
}
