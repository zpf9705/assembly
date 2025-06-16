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

import com.alibaba.qlexpress4.annotation.QLFunction;
import top.osjf.sdk.spring.runner.SdkExpressRunner;

/**
 * The parameter context retrieval interface for script execution calls.
 *
 * <p>According to the parsing requirements in {@link ScriptExecuteInterceptor#getContexts},
 * implement this interface and place appropriate parameters at the appropriate entry point
 * to achieve smooth expression execution.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.3
 * @see ScriptExecuteInterceptor
 * @see ScriptInterceptor
 * @see SdkExpressRunner
 */
public interface ScriptExecuteContext {

    /**
     * Return the name of the script executed by the expression.
     *
     * <p>The value of this name is used as a reference for expression
     * calls and for method interpretation. It can be customized using
     * annotation {@link QLFunction},with the default value referencing
     * {@link SdkExpressRunner#formatStandardizedClearFunctionName}, but
     * it must be identified by the developer.
     *
     * @return the name of the script executed by the expression.
     */
    String getScript();

    /**
     * Referring to API {@link SdkExpressRunner#execute(String, Object...)},
     * the return value of this method will be used as the execution parameter
     * for the final method call. This method is assembled and returned, and
     * can be used as a conditional method for dynamic component parameters.
     *
     * @return the method will be used as the execution parameter.
     */
    Object[] getContext();
}
