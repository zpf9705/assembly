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

package top.osjf.sdk.http.annotation.resolver;

import javax.tools.Diagnostic;

/**
 * Regarding the logger wrapper interface for {@link javax.annotation.processing.Messager},
 * including its input types for enumerating {@link Diagnostic.Kind}.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.2
 */
public interface Logger {

    /**
     * Log a message at the ERROR level.
     *
     * @param msg the message string to be logged
     * @see Diagnostic.Kind#ERROR
     */
    void error(String msg);

    /**
     * Log a message at the WARNING level.
     *
     * @param msg the message string to be logged
     * @see Diagnostic.Kind#WARNING
     */
    void warning(String msg);

    /**
     * Log a message at the MANDATORY_WARNING level.
     *
     * @param msg the message string to be logged
     * @see Diagnostic.Kind#MANDATORY_WARNING
     */
    void mandatoryWaring(String msg);

    /**
     * Log a message at the NOTE level.
     *
     * @param msg the message string to be logged
     * @see Diagnostic.Kind#NOTE
     */
    void note(String msg);

    /**
     * Log a message at the OTHER level.
     *
     * @param msg the message string to be logged
     * @see Diagnostic.Kind#OTHER
     */
    void other(String msg);
}
