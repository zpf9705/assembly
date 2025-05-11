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

import com.alibaba.qlexpress4.Express4Runner;
import com.alibaba.qlexpress4.InitOptions;
import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.QLResult;
import com.alibaba.qlexpress4.exception.QLException;
import com.alibaba.qlexpress4.runtime.context.ExpressContext;
import com.alibaba.qlexpress4.runtime.function.QMethodFunction;
import com.alibaba.qlexpress4.utils.BasicUtil;
import com.alibaba.qlexpress4.utils.QLFunctionUtil;
import top.osjf.sdk.core.lang.Nullable;
import top.osjf.sdk.spring.annotation.Sdk;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static java.util.Objects.requireNonNull;

/**
 * The {@code SdkExpressRunner} class extends the {@link Express4Runner} class and implements a
 * rule engine for SDK calls.
 *
 * <p>This class provides support for extending functional methods and uses the annotation
 * {@link com.alibaba.qlexpress4.annotation.QLFunction} to mark functions. Additionally,
 * it overrides the execute methods of the parent class to support script formatting and
 * execution.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.3
 */
public class SdkExpressRunner extends Express4Runner {

    /**
     * Constructs a {@code SdkExpressRunner} with {@link InitOptions}.
     *
     * @param initOptions Initialization options
     */
    public SdkExpressRunner(InitOptions initOptions) {
        super(initOptions);
    }

    /**
     * Methods for expanding new features method with annotation
     * {@link com.alibaba.qlexpress4.annotation.QLFunction} as function
     * <p>
     * The type of the input parameter can be the current type {@link Class#getClass()}
     * of the object or the actual type of the proxy class.
     *
     * @param clazz  Enter the type of the parameter or the actual type of the proxy object.
     * @param object Actual type instance object or regular object.
     */
    public void addFunction(@Nullable Class<?> clazz, Object object) {
        requireNonNull(object, "object");
        if (clazz == null) {
            super.addObjFunction(object);
            return;
        }
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            if (!BasicUtil.isPublic(method)) {
                continue;
            }
            List<String> functionNames = new ArrayList<>();
            functionNames.add((clazz.getName() + method.getName())
                    .replaceAll("[^a-zA-Z0-9]", ""));
            if (QLFunctionUtil.containsQLFunctionForMethod(method)) {
                functionNames.addAll(Arrays.asList(QLFunctionUtil.getQLFunctionValue(method))) ;
            }
            for (String functionName : functionNames) {
                addFunction(functionName, new QMethodFunction(object, method));
            }
        }
    }

    /**
     * Creates a new instance of {@code ScriptBuilder} associated with this
     * {@code SdkExpressRunner}.
     *
     * <p>Using template {@link ScriptBuilder}, you can directly specify the
     * real SDK proxy object type and its method name, making engine calls
     * more clear and concise. You can refer to the following example:
     * <pre>
     *    SdkExpressRunner exampleRunner = new SdkExpressRunner();
     *    ScriptBuilder exampleBuilder = exampleRunner.newScript();
     *    exampleBuilder.type(ExampleSdkInterface.class).methodName("say").build().execute(...)
     * </pre>
     *
     * @return a new {@code ScriptBuilder} instance initialized with this
     * {@code SdkExpressRunner}.
     */
    public ScriptBuilder newScript() {
        return new ScriptBuilder(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public QLResult execute(String script, Map<String, Object> context, QLOptions qlOptions) throws QLException {
        return super.execute(formatScript(script), context, qlOptions);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public QLResult execute(String script, Object context, QLOptions qlOptions) throws QLException {
        return super.execute(formatScript(script), context, qlOptions);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public QLResult executeWithAliasObjects(String script, QLOptions qlOptions, Object... objects) {
        return super.executeWithAliasObjects(formatScript(script), qlOptions, objects);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public QLResult execute(String script, ExpressContext context, QLOptions qlOptions) {
        return super.execute(formatScript(script), context, qlOptions);
    }

    private static String formatScript(String script) {
        script = script.replaceAll("[^a-zA-Z0-9\\(\\)]", "");
        if (!script.endsWith("()")) {
            script = script.concat("()");
        }
        return script;
    }

    public static class ScriptBuilder {

        private final SdkExpressRunner expressRunner;

        private Class<?> type;

        private String methodName;

        private String script;

        /**
         * Constructs a new {@code ScriptBuilder} instance with the given {@code SdkExpressRunner}.
         *
         * @param expressRunner the {@code SdkExpressRunner} instance to be used for script execution.
         */
        public ScriptBuilder(SdkExpressRunner expressRunner) {
            this.expressRunner = expressRunner;
        }

        /**
         * Sets the type of the class to be used in the script.
         *
         * @param type the class type to be annotated with {@link Sdk}
         * @return the {@code ScriptBuilder} instance for method chaining.
         */
        public ScriptBuilder type(Class<?> type) {
            this.type = type;
            return this;
        }

        /**
         * Sets the method name to be invoked in the script.
         *
         * @param methodName the name of the method to be invoked
         * @return the {@code ScriptBuilder} instance for method chaining.
         */
        public ScriptBuilder methodName(String methodName) {
            this.methodName = methodName;
            return this;
        }

        /**
         * Builds the script string by combining the type name and method name.
         * Throws an {@code IllegalArgumentException} if the type is not annotated with {@link Sdk}.
         *
         * @return the {@code ScriptBuilder} instance for method chaining.
         * @throws IllegalArgumentException if the type is not annotated with {@link Sdk}.
         */
        public ScriptBuilder build() {
            if (!type.isAnnotationPresent(Sdk.class)) {
                throw new IllegalArgumentException("Type must be annotated with @" + Sdk.class.getSimpleName());
            }
            script = type.getName() + "@" + methodName;
            return this;
        }

        /**
         * Retrieves the constructed script string.
         *
         * @return the constructed script string
         * @throws NullPointerException if the script string has not been built yet.
         */
        public String getScript() {
            requireNonNull(script, "script");
            return script;
        }

        /*
         * (non-javadoc)
         * @see SdkExpressRunner#execute(String, Map, QLOptions)
         */
        public QLResult execute(Map<String, Object> context, QLOptions qlOptions) throws QLException {
            return expressRunner.execute(getScript(), context, qlOptions);
        }

        /*
         * (non-javadoc)
         * @see SdkExpressRunner#execute(String, Object, QLOptions)
         */
        public QLResult execute(Object context, QLOptions qlOptions) throws QLException {
            return expressRunner.execute(getScript(), context, qlOptions);
        }

        /*
         * (non-javadoc)
         * @see SdkExpressRunner#executeWithAliasObjects(String, QLOptions, Object...)
         */
        public QLResult executeWithAliasObjects(QLOptions qlOptions, Object... objects) {
            return expressRunner.executeWithAliasObjects(getScript(), qlOptions, objects);
        }

        /*
         * (non-javadoc)
         * @see SdkExpressRunner#execute(String, ExpressContext, QLOptions)
         */
        public QLResult execute(ExpressContext context, QLOptions qlOptions) {
            return expressRunner.execute(getScript(), context, qlOptions);
        }
    }
}
