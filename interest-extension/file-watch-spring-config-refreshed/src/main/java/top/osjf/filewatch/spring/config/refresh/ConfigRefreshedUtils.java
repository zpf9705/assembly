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


package top.osjf.filewatch.spring.config.refresh;

import org.springframework.boot.env.PropertySourceLoader;
import org.springframework.core.io.support.SpringFactoriesLoader;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
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
public abstract class ConfigRefreshedUtils {

    /** Load the collection of {@link PropertySourceLoader} instances located under
     * {@link SpringFactoriesLoader#FACTORIES_RESOURCE_LOCATION}.*/
    private static final List<PropertySourceLoader> PROPERTY_SOURCE_LOADERS;

    /** Check if the specified configuration value is a precompiled {@link Pattern} instance of a placeholder. */
    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("^\\$\\{[^\\s]+\\}$");

    static {
        PROPERTY_SOURCE_LOADERS
                = SpringFactoriesLoader.loadFactories(PropertySourceLoader.class, ConfigRefreshedUtils.class.getClassLoader());
    }

    /**
     * Return the {@link PropertySourceLoader} static instance that is suitable for parsing based on
     * the file name type (determined by the suffix).
     * @param fileName the given {@code fileName}.
     * @return Specify the file type to support parsing of {@link PropertySourceLoader} static instances.
     */
    public static PropertySourceLoader getPropertySourceLoader(String fileName) {
        requireNonNull(fileName, "fileName");
        for (PropertySourceLoader loader : PROPERTY_SOURCE_LOADERS) {
            if (canLoadFileExtension(loader, fileName)) {
                return loader;
            }
        }
        throw new IllegalStateException("File extension of config file location '" + fileName
                + "' is not known to any PropertySourceLoader. If the location is meant to reference "
                + "a directory, it must end in '/'");
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
        for (PropertySourceLoader loader : PROPERTY_SOURCE_LOADERS) {
            if (canLoadFileExtension(loader, fileName)) {
                return true;
            }
        }
        return false;
    }

    private static boolean canLoadFileExtension(PropertySourceLoader loader, String name) {
        return Arrays.stream(loader.getFileExtensions())
                .anyMatch((fileExtension) -> StringUtils.endsWithIgnoreCase(name, fileExtension));
    }
}
