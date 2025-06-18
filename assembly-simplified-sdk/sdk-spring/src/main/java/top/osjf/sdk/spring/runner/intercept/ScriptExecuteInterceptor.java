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

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.util.Assert;
import top.osjf.sdk.core.util.internal.logging.InternalLogger;
import top.osjf.sdk.core.util.internal.logging.InternalLoggerFactory;
import top.osjf.sdk.spring.runner.SdkExpressRunner;
import top.osjf.sdk.spring.runner.SdkExpressRunnerException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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

    @Pointcut("@annotation(ScriptInterceptor)")
    public void pointcut () { }

    /**
     * Before advice to intercept with annotation {@link ScriptInterceptor}
     * @param jp the instance of {@link JoinPoint}.
     */
    @Before("pointcut()")
    public void doInterceptBefore(JoinPoint jp) {
         doInterceptBeforeInternal(jp);
    }

    /**
     * After advice to intercept with annotation {@link ScriptInterceptor}
     * @param jp the instance of {@link JoinPoint}.
     */
    @After("pointcut()")
    public void doInterceptAfter(JoinPoint jp) {
        release();
    }

    /**
     * AfterThrowing advice to intercept with annotation {@link ScriptInterceptor}
     * @param jp the instance of {@link JoinPoint}.
     */
    @AfterThrowing("pointcut()")
    public void intercept(JoinPoint jp) {
        release();
    }

    /**
     * Execute the expression for intercepting the cut plane, dynamically parameter {@link #getContexts}
     * according to the method of cutting in, obtain multiple {@link ScriptExecuteContext} instances,
     * Execute in the order of parsing and sorting parameters according to {@link AnnotationAwareOrderComparator},
     * and return the last result.
     * @param jp the instance of {@link JoinPoint}.
     */
    protected void doInterceptBeforeInternal(JoinPoint jp) {
        List<ScriptExecuteContext> contexts = getContexts(jp);
        List<Object> results = new ArrayList<>();
        for (ScriptExecuteContext sc : contexts) {
            results.add(doExecute(sc));
        }
        ScriptInterceptor si
                = ((MethodSignature) jp.getSignature())
                .getMethod().getAnnotation(ScriptInterceptor.class);
        if (si.exposeResults()) {
            LocalScriptExecuteContextManager.setScriptResults(results);
        }
    }

    /**
     * Release all local resources.
     */
    protected void release() {
        LocalScriptExecuteContextManager.removeAll();
    }

    /**
     * Extract the {@link ScriptExecuteContext} set by intercepting parameters from the cross-section
     * and local {@link ScriptExecuteContext} manager.
     * @param pjp the instance of {@link JoinPoint}.
     * @return The extracted {@link ScriptExecuteContext} set.
     */
    protected List<ScriptExecuteContext> getContexts(JoinPoint pjp) {
        List<ScriptExecuteContext> contexts = new ArrayList<>();
        for (Object arg : pjp.getArgs()) {
            if (arg instanceof ScriptExecuteContext) {
                contexts.add((ScriptExecuteContext) arg);
            }
        }
        contexts.addAll(getLocalContexts());
        Assert.notEmpty(contexts, "Missing context");
        AnnotationAwareOrderComparator.sort(contexts); // sorted ...
        return contexts;
    }

    /**
     * Return the locally dynamically set {@link ScriptExecuteContext} collection, which will
     * be used together with parameter extraction.
     * @return The local {@link ScriptExecuteContext} set.
     */
    protected List<ScriptExecuteContext> getLocalContexts() {
        if (LocalScriptExecuteContextManager.hasContext()) {
            return LocalScriptExecuteContextManager.getContexts();
        }
        return Collections.emptyList();
    }

    /**
     * Execute the expression through the executor, execute the corresponding method, and return the final result.
     * @param context The context object for executing parameters.
     * @return The result of executed by {@link #sdkExpressRunner}.
     * @throws SdkExpressRunnerException if execute by {@link #sdkExpressRunner} failed.
     */
    protected Object doExecute(ScriptExecuteContext context) {
        if (log.isDebugEnabled()) {
            log.debug("Execute script {}", context.getScript());
        }
        try {
            return sdkExpressRunner.execute(context.getScript(), context.getContext());
        }
        catch (SdkExpressRunnerException ex) {
            log.error("Failed to do script " + context.getScript() + " with context "
                    + Arrays.toString(context.getContext()), ex);
            throw ex;
        }
    }
}
