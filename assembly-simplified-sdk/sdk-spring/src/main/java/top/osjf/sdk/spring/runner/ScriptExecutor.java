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


package top.osjf.sdk.spring.runner;

import com.alibaba.qlexpress4.exception.QLException;
import top.osjf.sdk.core.lang.Nullable;

/**
 * Script executor interface.
 *
 * <p>This interface defines a method for executing scripts. Classes implementing this interface
 * must provide the specific logic for script execution.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.3
 * @see ScriptExecutorBuilder
 */
@FunctionalInterface
public interface ScriptExecutor {
    /**
     * Method to execute a script.
     * <p>Implementing classes must define the specific logic for script execution and return the result.
     *
     * @return The result of the script execution, as an Object.
     * @throws QLException If an error occurs during script execution, this exception is thrown.
     */
    @Nullable
    Object execute() throws QLException;
}
