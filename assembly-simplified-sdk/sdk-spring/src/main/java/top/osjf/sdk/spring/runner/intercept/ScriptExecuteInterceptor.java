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

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.util.Assert;
import top.osjf.sdk.core.util.internal.logging.InternalLogger;
import top.osjf.sdk.core.util.internal.logging.InternalLoggerFactory;
import top.osjf.sdk.spring.runner.SdkExpressRunner;
import top.osjf.sdk.spring.runner.SdkExpressRunnerException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The {@code ScriptExecuteInterceptor} class is a slicing class used to intercept methods
 * containing specific annotations {@link ScriptInterceptor} and instead parse their parameters
 * {@link ScriptExecuteContext}.
 *
 * <p>Support chain execution with multiple parameter contexts, using the return result of
 * the last execution expression as the final execution result of the slicing method.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.3
 */
@Aspect
public class ScriptExecuteInterceptor {

    private static final InternalLogger log = InternalLoggerFactory.getInstance(ScriptExecuteInterceptor.class);

    private final SdkExpressRunner sdkExpressRunner;

    /**
     * Create a {@code ScriptExecuteInterceptor} slice instance, given an
     * expression executor {@link SdkExpressRunner}.
     *
     * @param sdkExpressRunner A {@link SdkExpressRunner} instance of a given collection
     *                         SDK expression call rule.
     */
    public ScriptExecuteInterceptor(SdkExpressRunner sdkExpressRunner) {
        this.sdkExpressRunner = sdkExpressRunner;
    }

    @Around("@annotation(ScriptInterceptor)")
    public Object intercept(ProceedingJoinPoint pjp) {
        return doIntercept(pjp);
    }

    /**
     * Execute the expression for intercepting the cut plane, dynamically parameter {@link #getContexts}
     * according to the method of cutting in, obtain multiple {@link ScriptExecuteContext} instances,
     * execute in the order of parameters, and return the last result.
     * @param pjp the instance of {@link ProceedingJoinPoint}.
     * @return The execution result of the last bit of multiple context execution chains.
     */
    protected Object doIntercept(ProceedingJoinPoint pjp) {
        List<ScriptExecuteContext> contexts = getContexts(pjp);
        Object lastResult = null;
        for (ScriptExecuteContext sc : contexts) {
            lastResult = doExecute(sc);
        }
        return lastResult;
    }

    /**
     * Extract the {@link ScriptExecuteContext} set by intercepting parameters from the cross-section.
     * @param pjp the instance of {@link ProceedingJoinPoint}.
     * @return The extracted {@link ScriptExecuteContext} set.
     */
    protected List<ScriptExecuteContext> getContexts(ProceedingJoinPoint pjp) {
        List<ScriptExecuteContext> contexts = new ArrayList<>();
        for (Object arg : pjp.getArgs()) {
            if (arg instanceof ScriptExecuteContext) {
                contexts.add((ScriptExecuteContext) arg);
            }
        }
        Assert.notEmpty(contexts, "Missing context");
        return contexts;
    }

    /**
     * Execute the expression through the executor, execute the corresponding method, and return the final result.
     * @param context The context object for executing parameters.
     * @return The result of executed by {@link #sdkExpressRunner}.
     * @throws SdkExpressRunnerException if execute by {@link #sdkExpressRunner} failed.
     */
    protected Object doExecute(ScriptExecuteContext context) {
        try {
            return sdkExpressRunner.execute(context.getScript(), context.getContext());
        } catch (SdkExpressRunnerException ex) {
            log.error("Failed to do script " + context.getScript() + " with context "
                    + Arrays.toString(context.getContext()), ex);
            throw ex;
        }
    }
}
