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


package top.osjf.sdk.spring.runner.intercept;

import java.lang.annotation.*;

/**
 * This annotation is attached to the method, indicating that the parameter acquisition
 * of the method requires the use of expressions to execute the final solution and proxy
 * execution to return the result.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.3
 *
 * @see ScriptExecuteContext
 * @see ScriptExecuteInterceptor
 * @see top.osjf.sdk.spring.runner.SdkExpressRunner
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface ScriptInterceptor {

    /**
     * Indicate that {@link ScriptExecuteInterceptor} should make the script execution
     * result public as {@code ThreadLocal} Used for retrieval through class
     * {@link LocalScriptExecuteContextManager#getResults()}. By default, it is turned off, which
     * does not guarantee that {@link LocalScriptExecuteContextManager#getResults()}
     * access will work properly.
     */
    boolean exposeResults() default false;
}
