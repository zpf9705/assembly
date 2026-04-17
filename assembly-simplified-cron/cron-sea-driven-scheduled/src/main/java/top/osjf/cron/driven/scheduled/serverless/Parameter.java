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


package top.osjf.cron.driven.scheduled.serverless;

/**
 * Annotation for marking fields as task execution parameters.
 * Supports custom parameter names and provides object-to-string serialization strategy
 * for non-String type fields.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.2
 */
public @interface Parameter {
    /**
     * Defines the name of the parameter.
     * <p>If not set (empty string by default), the framework will use the field name
     * as the parameter name.
     * @return custom parameter name (field name if empty)
     */
    String name() default "";

    /**
     * Specifies the strategy for serializing objects to Strings.
     * <p>This strategy takes effect ONLY when the field type is NOT {@code String}, used
     * to convert the field object into a String value. A default implementation
     * {@code SimpleObjectToStringSerializationStrategy} is provided. Users can customize
     * the strategy by implementing {@code ObjectToStringSerializationStrategy}.
     *
     * @return serialization strategy {@code ObjectToStringSerializationStrategy} class
     */
    Class<? extends ObjectToStringSerializationStrategy> serializationStrategy()
            default SimpleObjectToStringSerializationStrategy.class;
}
