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


package top.osjf.spring.autoconfigure.filewatch.config.refresh;

import org.springframework.boot.env.PropertiesPropertySourceLoader;
import org.springframework.boot.env.PropertySourceLoader;
import org.springframework.boot.env.YamlPropertySourceLoader;

import java.util.regex.Pattern;

import static java.util.Objects.requireNonNull;

/**
 * Config refreshed utils.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.1
 *
 * @see PropertySourceLoader
 * @see org.springframework.boot.env.YamlPropertySourceLoader
 * @see org.springframework.boot.env.PropertiesPropertySourceLoader
 */
public final class ConfigRefreshedUtils {

    /** Yaml config resolver {@code PropertySourceLoader}.*/
    private static final PropertySourceLoader YAML_SOURCE_LOADER = new YamlPropertySourceLoader();

    /** Properties config resolver {@code PropertySourceLoader}.*/
    private static final PropertySourceLoader PROPERTIES_SOURCE_LOADER = new PropertiesPropertySourceLoader();

    /** Check if the specified configuration value is a precompiled {@link Pattern} instance of a placeholder. */
    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("^\\$\\{[^\\s]+\\}$");

    /**
     * Return the {@link PropertySourceLoader} static instance that is suitable for parsing based on
     * the file name type (determined by the suffix).
     * @param fileName the given {@code fileName}.
     * @return Specify the file type to support parsing of {@link PropertySourceLoader} static instances.
     * @see #isYamlFile(String)
     * @see #isPropertiesFile(String)
     */
    public static PropertySourceLoader getPropertySourceLoader(String fileName) {
        if (isYamlFile(fileName)) {
            return YAML_SOURCE_LOADER;
        }
        else if (isPropertiesFile(fileName)) {
            return PROPERTIES_SOURCE_LOADER;
        }
        throw new UnsupportedOperationException("Unsupported file type");
    }

    /**
     * Check if the given config is a placeholder.
     * <ul>
     * <li>${xxx.xxx} - true</li>
     * <li>${} - false</li>
     * <li>${ } - false</li>
     * <li>${xxx.xxx - false</li>
     * <li>xxx.xxx} - false</li>
     * <li>xxx.xxx - false</li>
     * </ul>
     * @param config the given config.
     * @return {@code true} is placeholder config,{@code false} otherwise.
     */
    public static boolean isPlaceholderConfig(String config) {
        if (config == null) {
            return false;
        }

        return PLACEHOLDER_PATTERN.matcher(config).matches();
    }

    /**
     * Check if the config file is support resolve.
     * @param fileName the given {@code fileName}.
     * @return {@code true} is supported config file,{@code false} otherwise.
     */
    public static boolean isConfigFile(String fileName) {
        return isYamlFile(fileName) || isPropertiesFile(fileName);
    }

    /**
     * Check if the given filename is yaml file.
     * @param fileName the given {@code fileName}.
     * @return {@code true} is yaml file,{@code false} otherwise.
     */
    public static boolean isYamlFile(String fileName) {
        requireNonNull(fileName, "fileName");
        return fileName.endsWith(".yml") || fileName.endsWith(".yaml");
    }

    /**
     * Check if the given filename is properties file.
     * @param fileName the given {@code fileName}.
     * @return {@code true} is properties file,{@code false} otherwise.
     */
    public static boolean isPropertiesFile(String fileName) {
        requireNonNull(fileName, "fileName");
        return fileName.endsWith(".properties");
    }
}
