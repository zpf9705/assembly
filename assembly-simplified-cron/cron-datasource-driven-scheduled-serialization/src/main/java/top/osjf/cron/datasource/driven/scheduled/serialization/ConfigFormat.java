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


package top.osjf.cron.datasource.driven.scheduled.serialization;

/**
 * Enumeration of configuration formats, representing the types of data serialization formats supported.
 *
 * <p>This enum defines various serialization formats commonly used in data sources to store scheduled task
 * configuration or other structured metadata. Each format corresponds to a specific data structure
 * and parsing mechanism, suitable for different storage and transmission scenarios.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.2
 */
public enum ConfigFormat {

    /**
     * Plain text format, stores content as a simple string.
     */
    TEXT,

    /**
     * JSON format, uses JavaScript Object Notation for structured data representation.
     */
    JSON,

    /**
     * XML format, uses Extensible Markup Language for data definition.
     */
    XML,

    /**
     * YAML format, uses YAML Ain't Markup Language for data serialization.
     */
    YAML,

    /**
     * HTML format, uses HyperText Markup Language for document structure.
     */
    HTML,

    /**
     * Properties format, Java properties file format, a key-value pair based text format.
     */
    PROPERTIES
}
