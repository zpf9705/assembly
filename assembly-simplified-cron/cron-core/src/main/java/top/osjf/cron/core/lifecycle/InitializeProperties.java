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


package top.osjf.cron.core.lifecycle;

import top.osjf.cron.core.lang.Nullable;

import java.io.InputStream;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import static java.util.Objects.requireNonNull;

/**
 * Initial dedicated configuration loading container, inherit JDK native {@link Properties},
 * only used for <strong>service startup initialization phase</strong> to load configuration files,
 * merge startup parameters, and obtain text configuration items.
 *
 * <p>Restrictions：
 * <ul>
 *     <li>Only store String key and String value, strictly follow the native contract of Properties;</li>
 *     <li>It is only used during initialization, and will not be dynamically added/modified during runtime;</li>
 *     <li>Does not support storing custom object instances for framework runtime.</li>
 * </ul>
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.2
 */
public class InitializeProperties extends Properties {

    private static final long serialVersionUID = -1935772825783648031L;

    // ====================== Construction & Factory Methods ======================

    /**
     * Create empty initialization configuration container.
     */
    public InitializeProperties() {
        super();
    }

    /**
     * Create and initialize with existing {@code Properties} source.
     * @param source original properties source
     * @throws NullPointerException if source is null
     */
    public InitializeProperties(Properties source) {
        super();
        requireNonNull(source, "init source Properties cannot be null");
        this.putAll(source);
    }

    /**
     * Static factory method: create empty instance.
     * @return empty {@code InitializeProperties}
     */
    public static InitializeProperties empty() {
        return new InitializeProperties();
    }

    /**
     * Static factory method: copy from {@link System#getProperties()}.
     * @return new filled {@code InitializeProperties}.
     */
    public static InitializeProperties systemProperties() {
        return copyOf(System.getProperties());
    }

    /**
     * Static factory method: copy from {@code Properties}.
     * @param source origin properties
     * @return new filled {@code InitializeProperties}
     */
    public static InitializeProperties copyOf(Properties source) {
        return new InitializeProperties(source);
    }

    /**
     * Static factory method: copy from string-key string-value map.
     * @param source origin Map  {@link String}, {@link String} source
     * @return new filled {@code InitializeProperties}
     * @throws NullPointerException if source is null
     */
    public static InitializeProperties copyOf(Map<String, String> source) {
        requireNonNull(source, "init source Map cannot be null");
        InitializeProperties props = empty();
        source.forEach(props::setProperty);
        return props;
    }

    /**
     * Static factory method: copy from Map  {@link String}, {@link Object} , value auto convert to {@link String}.
     * @param source {@link Map} with String key and arbitrary object value
     * @return new filled {@code InitializeProperties} instance
     * @throws NullPointerException if source is null
     */
    public static InitializeProperties copyOfStringKeys(Map<String, Object> source) {
        requireNonNull(source, "init source Map cannot be null");
        InitializeProperties props = empty();
        source.forEach((k, v) -> props.setProperty(k, asString(v)));
        return props;
    }

    /**
     * Static factory method : copy from Map {@link Object}, {@link Object} , both key and value auto
     * convert to {@link String}.
     * @param source {@link Map} with arbitrary object key and arbitrary object value
     * @return new filled {@code InitializeProperties} instance
     * @throws NullPointerException if source is null
     */
    public static InitializeProperties copyOfObjectKeys(Map<Object, Object> source) {
        requireNonNull(source, "init source Map cannot be null");
        InitializeProperties props = empty();
        source.forEach((k, v) -> props.setProperty(asString(k), asString(v)));
        return props;
    }

    private static String asString(@Nullable Object property) {
        return property == null ? "" : property.toString();
    }

    // ====================== Multi-source Merge API ======================
    /**
     * Merge another Properties into current container, same key will be overwritten by source value.
     * @param source external properties to merge
     * @throws NullPointerException if source is null
     */
    public void mergeFrom(Properties source) {
        requireNonNull(source, "merge source cannot be null");
        this.putAll(source);
    }

    /**
     * Merge string-key map into current container, object values will convert to string.
     * @param source string-key map
     * @throws NullPointerException if source is null
     */
    public void mergeFrom(Map<String, Object> source) {
        requireNonNull(source, "merge source cannot be null");
        source.forEach((k, v) -> setProperty(k, v == null ? "" : v.toString()));
    }

    // ====================== Type-safe Getter API ======================
    /**
     * Get raw {@link String} config value, null if key not exists.
     * @param key config unique key
     * @return matched string value or null
     * @throws NullPointerException if key is null
     */
    @Nullable
    public String getString(String key) {
        requireNonNull(key);
        return getProperty(key);
    }

    /**
     * Get {@link String} value with default fallback.
     * @param key config unique key
     * @param defaultVal fallback value when key missing
     * @return config value or defaultVal
     * @throws NullPointerException if key is null
     */
    public String getString(String key, String defaultVal) {
        String val = getString(key);
        return val == null ? defaultVal : val;
    }

    /**
     * Parse config value to {@link Integer}, return null if missing or parse failed.
     * @param key config unique key
     * @return parsed Integer or null
     * @throws NullPointerException if key is null
     */
    @Nullable
    public Integer getInteger(String key) {
        String str = getString(key);
        if (str == null) return null;
        try {
            return Integer.valueOf(str.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Get {@link Integer} with default value.
     * @param key config unique key
     * @param defaultVal fallback number
     * @return parsed number or defaultVal
     * @throws NullPointerException if key is null
     */
    public int getInteger(String key, int defaultVal) {
        Integer val = getInteger(key);
        return val == null ? defaultVal : val;
    }

    /**
     * Parse config value to {@link Long}, return null if missing or parse failed.
     * @param key config unique key
     * @return parsed Long or null
     * @throws NullPointerException if key is null
     */
    @Nullable
    public Long getLong(String key) {
        String str = getString(key);
        if (str == null) return null;
        try {
            return Long.valueOf(str.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Get {@link Long} with default value.
     * @param key config unique key
     * @param defaultVal fallback number
     * @return parsed number or defaultVal
     * @throws NullPointerException if key is null
     */
    public long getLong(String key, long defaultVal) {
        Long val = getLong(key);
        return val == null ? defaultVal : val;
    }

    /**
     * Parse config to {@link Boolean}, support
     * <ul>
     *     <li>true/1/ye</li>
     *     <li>false/0/no</li>
     * </ul>
     * @param key config unique key
     * @return parsed Boolean or null
     * @throws NullPointerException if key is null
     */
    @Nullable
    public Boolean getBoolean(String key) {
        String str = getString(key);
        if (str == null) return null;
        String lower = str.trim().toLowerCase(Locale.ROOT);
        switch (lower) {
            case "true":
            case "1":
            case "yes": return Boolean.TRUE;
            case "false":
            case "0":
            case "no": return Boolean.FALSE;
            default: return null;
        }
    }

    /**
     * Get {@link Boolean} with default fallback.
     * @param key config unique key
     * @param defaultVal fallback boolean
     * @return parsed boolean or defaultVal
     * @throws NullPointerException if key is null
     */
    public boolean getBoolean(String key, boolean defaultVal) {
        Boolean val = getBoolean(key);
        return val == null ? defaultVal : val;
    }

    /**
     * Parse configuration string to target {@link Enum}, return {@code null} if key missing,
     * blank or enum name mismatch.
     * @param key       config string key
     * @param enumClazz target enum class type
     * @param <T>       generic enum type
     * @return matched enum instance, null when parse failed or key not found
     * @throws NullPointerException if key or enumClazz is null
     */
    @Nullable
    public <T extends Enum<T>> T getEnum(String key, Class<T> enumClazz) {
        requireNonNull(enumClazz);
        String val = getString(key);
        if (val == null) return null;
        try {
            return Enum.valueOf(enumClazz, val.trim());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * Parse configuration string to target {@link Enum}, return default enum when missing or parse error.
     *
     * @param key         config string key
     * @param enumClazz   target enum class type
     * @param defaultEnum fallback enum returned if key missing/parse fail
     * @param <T>         generic enum type
     * @return matched enum or defaultEnum
     * @throws NullPointerException if key or enumClazz or defaultEnum is null
     */
    public <T extends Enum<T>> T getEnum(String key, Class<T> enumClazz, T defaultEnum) {
        requireNonNull(defaultEnum);
        T enumVal = getEnum(key, enumClazz);
        return enumVal == null ? defaultEnum : enumVal;
    }

    // ====================== File Load Helper ======================
    /**
     * Load config from input stream (properties file).
     * @param inputStream file input stream
     * @throws Exception load IO exception
     * @throws NullPointerException if inputStream is null
     */
    public void loadConfig(InputStream inputStream) throws Exception {
        requireNonNull(inputStream);
        super.load(inputStream);
    }

    // ======================  Setter API ======================

    /**
     * Add or overwrite configuration, automatically convert any object value to string.
     * Suitable for quick writing of numbers, booleans, enums and other convertible types.
     *
     * @param key   Non-null config unique identifier
     * @param value Non-null object to convert to string storage
     * @throws NullPointerException if key or value is null
     */
    public void setProperty(String key, Object value) {
        requireNonNull(key, "config key cannot be null");
        requireNonNull(value, "config value cannot be null");
        setProperty(key, value.toString());
    }
}
