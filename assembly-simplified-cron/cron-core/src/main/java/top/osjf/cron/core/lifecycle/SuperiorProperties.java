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

import top.osjf.cron.core.lang.Nullable;

import java.util.Map;
import java.util.Properties;

/**
 * The {@code SuperiorProperties} interface defines a set of methods for managing and manipulating
 * properties,these properties can be key-value pairs of any type and support initialization and
 * merging through different data structures, such as Maps and {@code Properties} objects.
 *
 * <p>The interface provides functionalities to retrieve property values (returning a default value
 * if the property does not exist), add properties (supporting string keys with string values, string
 * keys with object values, and object keys with object values), add a set of startup parameters through
 * a {@code Properties} object or a Map, copy startup parameters from another SuperiorProperties object,
 * and convert all current properties into a Properties object.
 *
 * <p>Static factory methods are provided to create an empty {@code SuperiorProperties} instance or an
 * instance initialized with a provided Map or Properties object.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.3
 */
public interface SuperiorProperties {

    /* --------------------------------------------------------------------------- */
    /* -------------------------- conventional method ---------------------------- */
    /* --------------------------------------------------------------------------- */

    /**
     * Retrieve the value of the specified property name,if there is no return {@literal null}.
     *
     * @param propertyName name of the attribute to be obtained. This name should match the
     *                     property name defined in the object.
     * @return Returns the value of the specified property name,if not exist return null.
     * @throws NullPointerException if input propertyName is null.
     * @since 1.0.3
     */
    @Nullable
    Object getProperty(String propertyName);

    /**
     * Retrieve the value of the specified attribute, and return the default value if the
     * attribute does not exist.
     *
     * @param propertyName name of the attribute to be obtained. This name should match the
     *                     property name defined in the object.
     * @param <T>          generic type, representing the type of attribute value and the type
     *                     of default value.
     * @param def          the default value when the attribute does not exist.
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
     * Copy the startup parameters from another {@code SuperiorProperties} object.
     *
     * @param superiorProperties another {@code SuperiorProperties} instance to merge.
     * @throws NullPointerException if input SuperiorProperties is null.
     */
    void addProperties(SuperiorProperties superiorProperties);

    /**
     * Returns <tt>true</tt> if this {@code SuperiorProperties} contains no key-value mappings.
     *
     * @return <tt>true</tt> if this {@code SuperiorProperties} contains no key-value mappings.
     */
    boolean isEmpty();

    /**
     * Convert all properties in the current {@code SuperiorProperties} into a
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
     * Creates a new {@code SuperiorProperties} without args.
     *
     * @return a without args {@code SuperiorProperties} instance.
     */
    static SuperiorProperties of() {
        return new DefaultSuperiorProperties();
    }

    /**
     * Creates a new {@code SuperiorProperties} instance initialized with the
     * properties from the provided Map.
     *
     * @param map a {@code Map} containing the initial properties, where the
     *            keys are Strings and the values can be any Object type.
     * @return a new {@code SuperiorProperties}  instance initialized with the {@code Map}.
     */
    static SuperiorProperties of(Map<String, Object> map) {
        return new DefaultSuperiorProperties(map);
    }

    /**
     * Creates a new {@code SuperiorProperties} instance initialized with the
     * properties from the provided {@code Properties} object.
     *
     * @param properties the initial properties, represented as a {@code Properties}
     *                   object.
     * @return a new {@code SuperiorProperties} instance initialized with the provided
     * {@code Properties}.
     */
    static SuperiorProperties of(Properties properties) {
        return new DefaultSuperiorProperties(properties);
    }
}
