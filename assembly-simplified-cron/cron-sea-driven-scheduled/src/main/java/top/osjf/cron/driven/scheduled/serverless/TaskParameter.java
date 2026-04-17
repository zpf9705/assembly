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
 * Marker interface for task parameters.
 *
 * <p>This is a <strong>marker interface</strong> with no methods defined.
 * It is used to mark a class as a <strong>task execution parameter class</strong>.
 * When a custom parameter class implements this interface, the framework will automatically
 * recognize it, scan fields, and assemble task parameters.
 *
 * <p>The original design intention of this interface:
 * <ul>
 *     <li>Scan fields annotated with {@link Parameter}</li>
 *     <li>Obtain parameter name, field value, and serialization strategy from the annotation</li>
 *     <li>Automatically convert non-String types to String using the specified strategy</li>
 *     <li>Assemble all parameters into the complete input for task execution</li>
 * </ul>
 *
 * <p>Usage example:
 * <pre>
 * // Implement this marker interface to mark as a task parameter class
 * public class MyTaskParam implements TaskParameter {
 *
 *     // Specify parameter name with annotation
 *     &#64;Parameter(name = "query")
 *     private String query;
 *
 *     // Specify serialization strategy (object to JSON string)
 *     &#64;Parameter(serializationStrategy = JsonToStringStrategy.class)
 *     private UserInfo userInfo;
 * }
 * </pre>
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.2
 */
public interface TaskParameter {
}
