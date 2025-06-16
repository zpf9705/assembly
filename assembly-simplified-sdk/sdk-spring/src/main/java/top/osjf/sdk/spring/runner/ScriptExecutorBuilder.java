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

import com.alibaba.qlexpress4.QLOptions;
import top.osjf.sdk.core.util.MapUtils;
import top.osjf.sdk.spring.annotation.Sdk;

import java.util.Collections;
import java.util.Map;

import static java.util.Objects.requireNonNull;

/**
 * Script executor builder class.
 *
 * <p>This class is used to build a script executor ({@link ScriptExecutor}) by configuring script execution parameters
 * through method chaining.</p>
 *
 * <p>The builder pattern allows setting various parameters for script execution step by step and finally generating
 * the script executor via the {@link #build()} method.</p>
 *
 * <p>Example usage:
 * <pre>{@code
 * ScriptExecutor executor = new ScriptExecutorBuilder(expressRunner)
 *     .type(MyClass.class)
 *     .methodName("myMethod")
 *     .context(contextMap)
 *     .options(qlOptions)
 *     .build();
 * }</pre>
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @see ScriptExecutor
 * @since 1.0.3
 */
public final class ScriptExecutorBuilder {

    private final SdkExpressRunner expressRunner;

    private Class<?> type;

    private String methodName;

    private Map<String, Object> context = Collections.emptyMap();

    private QLOptions qlOptions = QLOptions.DEFAULT_OPTIONS;

    private String script;

    /**
     * Constructs a new {@code ScriptExecutorBuilder} instance with the given {@code SdkExpressRunner}.
     *
     * @param expressRunner the {@code SdkExpressRunner} instance to be used for script execution.
     */
    public ScriptExecutorBuilder(SdkExpressRunner expressRunner) {
        this.expressRunner = expressRunner;
    }

    /**
     * Sets the type of the class to be used in the script.
     *
     * @param type the class type to be annotated with {@link Sdk}
     * @return the {@code ScriptBuilder} instance for method chaining.
     */
    public ScriptExecutorBuilder type(Class<?> type) {
        requireNonNull(type, "type");
        this.type = type;
        return this;
    }

    /**
     * Sets the method name to be invoked in the script.
     *
     * @param methodName the name of the method to be invoked
     * @return the {@code ScriptBuilder} instance for method chaining.
     */
    public ScriptExecutorBuilder methodName(String methodName) {
        requireNonNull(methodName, "methodName");
        this.methodName = methodName;
        return this;
    }

    /**
     * Sets the context parameter information for script execution.
     *
     * @param context the context parameter information.
     * @return the {@code ScriptBuilder} instance for method chaining.
     */
    public ScriptExecutorBuilder context(Map<String, Object> context) {
        if (MapUtils.isNotEmpty(context)) {
            this.context = context;
        }
        return this;
    }

    /**
     * Sets configurable option instances for script execution.
     *
     * @param qlOptions the configurable option instances.
     * @return the {@code ScriptBuilder} instance for method chaining.
     */
    public ScriptExecutorBuilder options(QLOptions qlOptions) {
        if (qlOptions != null) {
            this.qlOptions = qlOptions;
        }
        return this;
    }

    /**
     * Use the given chain assignment information component to create a script executor instance.
     *
     * @return the Script executor function instance.
     * @throws IllegalArgumentException      if the type is not annotated with {@link Sdk}.
     * @throws UnsupportedOperationException If expression call support is not enabled.
     * @throws IllegalStateException         if script not registered in {@link SdkExpressRunner#getStandardizedScriptCorrespond()}
     */
    public ScriptExecutor build() {
        Sdk sdk = type.getAnnotation(Sdk.class);
        if (sdk == null) {
            throw new IllegalArgumentException("Type must be annotated with @" + Sdk.class.getSimpleName());
        }
        if (!sdk.enableExpressionCall()) {
            throw new UnsupportedOperationException("Expression call support is not enabled.");
        }
        script = SdkExpressRunner.formatStandardizedClearFunctionName(type.getName(), methodName);
        if (!expressRunner.getStandardizedScriptCorrespond().containsKey(script)) {
            throw new IllegalStateException("Unregistered script information.");
        }
        return () -> expressRunner.execute(script, context, qlOptions);
    }
}
