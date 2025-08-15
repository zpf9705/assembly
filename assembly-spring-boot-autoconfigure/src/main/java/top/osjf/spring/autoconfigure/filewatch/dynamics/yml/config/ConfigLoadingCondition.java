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


package top.osjf.spring.autoconfigure.filewatch.dynamics.yml.config;

import top.osjf.filewatch.BindingConfiguration;

import java.util.Objects;

/**
 * The loading condition entity class configured in the YAML file.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.1
 */

public class ConfigLoadingCondition extends BindingConfiguration {

    private static final long serialVersionUID = 1358161329578896179L;

    /**
     * @return The file name end with.
     */
    @Override
    public String getPathContext() {
        throw new UnsupportedOperationException("No fixed one");
    }

    /**
     * Check is yaml file.
     * @param fileName the given {@code fileName}.
     * @return {@code true} is yaml file,{@code false} otherwise.
     */
    public boolean isYamlConfig(String fileName) {
        return isYamlFile(fileName);
    }

    /**
     * Check is yaml file.
     * @param fileName the given {@code fileName}.
     * @return {@code true} is yaml file,{@code false} otherwise.
     */
    public static boolean isYamlFile(String fileName) {
        Objects.requireNonNull(fileName, "fileName");
        return fileName.endsWith(".yml") || fileName.endsWith(".yaml");
    }
}
