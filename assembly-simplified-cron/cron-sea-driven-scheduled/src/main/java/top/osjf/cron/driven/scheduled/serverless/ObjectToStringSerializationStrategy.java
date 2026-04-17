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
 * Strategy interface for object-to-string serialization.
 *
 * <p>Defines a standard contract for converting any Java object into a string representation.
 * Used to uniformly convert non-String data into string format during parameter passing,
 * data storage, or network transmission.
 *
 * <p>Implementations of this interface should provide specific conversion logic, such as:
 * <ul>
 *     <li>Simple conversion using {@link Object#toString()}</li>
 *     <li>JSON serialization</li>
 *     <li>XML serialization</li>
 *     <li>Custom encoding format</li>
 * </ul>
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.2
 */
public interface ObjectToStringSerializationStrategy {

    /**
     * Serializes the given object into a string.
     *
     * <p>Implementations should convert the given object into its string representation
     * according to their own strategy. If the input object is {@literal null}, implementations
     * can decide to return empty string or throw an exception.
     *
     * @param obj object to be serialized, may be {@literal null}.
     * @return serialized string result
     */
    String serializeToString(Object obj);
}
