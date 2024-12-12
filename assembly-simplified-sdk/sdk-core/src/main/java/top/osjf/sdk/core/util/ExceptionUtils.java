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

package top.osjf.sdk.core.util;

/**
 * Java exception error related service utility class.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.2
 */
public abstract class ExceptionUtils {

    /**
     * Extract error information from an abnormal object.
     * <p>
     * If the exception object itself or any exception in its cause chain
     * has nonEmpty error information, return that information.
     * <p>
     * If no nonEmpty error message is found, return {@literal UnKnow message}.
     *
     * @param e Abnormal objects from which to extract error information.
     * @return Abnormal error message or 'unknown message' (if the error
     * message cannot be determined).
     */
    public static String getMessage(Throwable e) {
        if (e == null) return "unKnow message";
        String message = e.getMessage();
        if (StringUtils.isNotBlank(message)) return message;
        Throwable cause = e.getCause();
        while (true) {
            if (cause != null) {
                message = cause.getMessage();
                if (StringUtils.isNotBlank(message)) {
                    break;
                } else {
                    cause = cause.getCause();
                }
            } else {
                message = "unKnow message";
                break;
            }
        }
        return message;
    }
}
