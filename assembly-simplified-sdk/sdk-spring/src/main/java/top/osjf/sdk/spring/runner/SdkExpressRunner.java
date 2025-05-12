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
import com.alibaba.qlexpress4.annotation.QLFunction;
import com.alibaba.qlexpress4.exception.QLException;
import com.alibaba.qlexpress4.runtime.function.CustomFunction;
import com.alibaba.qlexpress4.runtime.function.QMethodFunction;
import com.alibaba.qlexpress4.utils.BasicUtil;
import com.alibaba.qlexpress4.utils.QLFunctionUtil;
import top.osjf.sdk.core.lang.Nullable;
import top.osjf.sdk.core.util.ArrayUtils;
import top.osjf.sdk.spring.annotation.Sdk;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

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
public class SdkExpressRunner {

    private final Express4Runner express4Runner;

    private final Map<String, String> standardizedScriptCorrespond = new ConcurrentHashMap<>();

    public SdkExpressRunner(InitOptions initOptions) {
        express4Runner = new Express4Runner(initOptions);
    }

    /**
     * Add expression functionality for SDK proxy type methods using the
     * {@code com.alibaba.qlexpress4} specification.
     *
     * <p>Customized a set of instruction specifications and support annotations
     * {@link com.alibaba.qlexpress4.annotation.QLFunction} for this framework,
     * making it easy to call the SDK.
     * Let's take a look at a set of standard generation cases:
     * <ul>
     * <li>For example interface :
     *  <pre>public interface ExampleInterface { void say(String message) }</pre>
     *  Generated call instructions: ExampleInterfacesay(message)
     * </li>
     * <li>If you have added annotations {@link com.alibaba.qlexpress4.annotation.QLFunction}:
     * Generated call instructions: {@link QLFunction#value()}(message)
     * </li>
     * </ul>
     *
     * @param sdkTargetClass The class object of the SDK target proxy type.
     * @param sdkProxyObject The proxy object of the SDK target proxy type.
     */
    public void addSdkMethodFunction(Class<?> sdkTargetClass, Object sdkProxyObject) {
        requireNonNull(sdkTargetClass, "sdkTargetClass");
        requireNonNull(sdkProxyObject, "sdkProxyObject");
        Method[] methods = sdkTargetClass.getDeclaredMethods();
        for (Method method : methods) {
            if (!BasicUtil.isPublic(method)) {
                continue;
            }
            String parameterNames = getMethodParameterNames(method);
            String standardizedScriptName =
                    formatStandardizedClearFunctionName(sdkTargetClass.getName(), method.getName());
            String ruleScriptName = (standardizedScriptName).replaceAll("[^a-zA-Z0-9]", "");
            standardizedScriptCorrespond.putIfAbsent(standardizedScriptName,
                    /* cache standardizedScriptName directly set as method call */
                    ruleScriptName.concat("(").concat(parameterNames).concat(")"));
            express4Runner.addFunction(ruleScriptName, new SdkQMethodFunction(sdkProxyObject, method, parameterNames));
            if (QLFunctionUtil.containsQLFunctionForMethod(method)) {
                for (String qlNameMod : QLFunctionUtil.getQLFunctionValue(method)) {
                    express4Runner.addAlias(qlNameMod, ruleScriptName);
                }
            }
        }
    }

    private static String getMethodParameterNames(Method method){
        Parameter[] parameters = method.getParameters();
        if (ArrayUtils.isEmpty(parameters)){
            return "";
        }
        return Arrays.stream(method.getParameters()).map(Parameter::getName)
                .collect(Collectors.joining(","));
    }

    /**
     * Format as a standard and clear class method assignment definition
     * “targetClassName + "@" + methodName”.
     *
     * @param targetClassName the target class name.
     * @param methodName      the target class method.
     * @return a standard and clear class method assignment definition.
     */
    public static String formatStandardizedClearFunctionName(Object targetClassName, Object methodName) {
        return targetClassName + "@" + methodName;
    }

    /**
     * Execute the script set in the context, with no other configuration variables.
     * <p>
     * The above instruction added to {@link #addSdkMethodFunction(Class, Object)} conforms to the syntax
     * of its certain function name,but it is not clear enough. When calling, the following calling rules
     * are provided to correspond to the calling function name:
     * <ul>
     * <li>if {@code ExampleInterfacesay(message)} , you can use ExampleInterface@say replace,
     * parameter part does not need to be written.</li>
     * <li>{@link QLFunction#value()} Just use it normally,parameter part does not need to be
     * written</li>
     * </ul>
     *
     * @param script Call the script.
     * @return The return result of calling the script,the specific type can be viewed in the processing
     * rules:{@link top.osjf.sdk.core.support.SdkSupport#resolveResponse}
     * @throws QLException ---
     *                     <ul>
     *                     <li>{@link com.alibaba.qlexpress4.exception.QLSyntaxException}Script syntax error.</li>
     *                     <li>{@link com.alibaba.qlexpress4.exception.QLRuntimeException}Script runtime error, SDK
     *                     component call error, need to trace SDK call process.</li>
     *                     <li>{@link com.alibaba.qlexpress4.exception.QLTimeoutException}The script timed out,
     *                     visible call configuration {@link QLOptions}.</li>
     *                     </ul>
     */
    public Object execute(String script) throws QLException {
        return execute(script, Collections.emptyMap());
    }

    /**
     * Execute the script set in the context, can pass in custom parameter variables.
     * <p>
     * The above instruction added to {@link #addSdkMethodFunction(Class, Object)} conforms to the syntax
     * of its certain function name,but it is not clear enough. When calling, the following calling rules
     * are provided to correspond to the calling function name:
     * <ul>
     * <li>if {@code ExampleInterfacesay(message)} , you can use ExampleInterface@say replace,
     * parameter part does not need to be written.</li>
     * <li>{@link QLFunction#value()} Just use it normally,parameter part does not need to be
     * written</li>
     * </ul>
     *
     * @param script  Call the script.
     * @param context The calling context can be a parameter for calling a component method.
     * @return The return result of calling the script,the specific type can be viewed in the processing
     * rules:{@link top.osjf.sdk.core.support.SdkSupport#resolveResponse}
     * @throws QLException ---
     *                     <ul>
     *                     <li>{@link com.alibaba.qlexpress4.exception.QLSyntaxException}Script syntax error.</li>
     *                     <li>{@link com.alibaba.qlexpress4.exception.QLRuntimeException}Script runtime error, SDK
     *                     component call error, need to trace SDK call process.</li>
     *                     <li>{@link com.alibaba.qlexpress4.exception.QLTimeoutException}The script timed out,
     *                     visible call configuration {@link QLOptions}.</li>
     *                     </ul>
     */
    public Object execute(String script, Map<String, Object> context) throws QLException {
        return execute(script, context, QLOptions.DEFAULT_OPTIONS);
    }

    /**
     * Execute the script set in the context, can pass in custom parameter variables and
     * configure custom calling options.
     * <p>
     * The above instruction added to {@link #addSdkMethodFunction(Class, Object)} conforms to the syntax
     * of its certain function name,but it is not clear enough. When calling, the following calling rules
     * are provided to correspond to the calling function name:
     * <ul>
     * <li>if {@code ExampleInterfacesay(message)} , you can use ExampleInterface@say replace,
     * parameter part does not need to be written.</li>
     * <li>{@link QLFunction#value()} Just use it normally,parameter part does not need to be
     * written</li>
     * </ul>
     *
     * @param script    Call the script.
     * @param context   The calling context can be a parameter for calling a component method.
     * @param qlOptions Execute call options.
     * @return The return result of calling the script,the specific type can be viewed in the processing
     * rules:{@link top.osjf.sdk.core.support.SdkSupport#resolveResponse}
     * @throws QLException ---
     *                     <ul>
     *                     <li>{@link com.alibaba.qlexpress4.exception.QLSyntaxException}Script syntax error.</li>
     *                     <li>{@link com.alibaba.qlexpress4.exception.QLRuntimeException}Script runtime error, SDK
     *                     component call error, need to trace SDK call process.</li>
     *                     <li>{@link com.alibaba.qlexpress4.exception.QLTimeoutException}The script timed out,
     *                     visible call configuration {@link QLOptions}.</li>
     *                     </ul>
     */
    public Object execute(String script, @Nullable Map<String, Object> context, QLOptions qlOptions)
            throws QLException {
        return express4Runner.execute(getCorrespondScript(script), context, qlOptions).getResult();
    }

    private String getCorrespondScript(String script) {
        if (standardizedScriptCorrespond.containsKey(script)) {
            return standardizedScriptCorrespond.get(script);
        }

        //Check whether the method call complies with the '()' suffix rule
        // and the rule of attaching parameter names in a timely manner
        if (!script.endsWith(")")) {
            CustomFunction function = express4Runner.getFunction(script);
            if (function == null){
                throw new IllegalArgumentException("No SDK Function named " + script);
            }
            if (function instanceof SdkQMethodFunction) {
                script = ((SdkQMethodFunction) function).addScriptParameterNames(script);
            }
            else { script = script.concat("()"); }

        }
        return script;
    }

    /**
     * The parameter name combination string of the method attached to the extension {@link QMethodFunction}.
     */
    private static class SdkQMethodFunction extends QMethodFunction {
        @Nullable private final String parameterNames;
        public SdkQMethodFunction(Object object, Method method, @Nullable String parameterNames) {
            super(object, method);
            this.parameterNames = parameterNames;
        }
        public String addScriptParameterNames(String script){
            return script + "(" + parameterNames + ")";
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
         * @throws IllegalStateException    if script not registered in {@link #standardizedScriptCorrespond}
         */
        public ScriptBuilder build() {
            if (!type.isAnnotationPresent(Sdk.class)) {
                throw new IllegalArgumentException("Type must be annotated with @" + Sdk.class.getSimpleName());
            }
            script = type.getName() + "@" + methodName;
            if (!expressRunner.standardizedScriptCorrespond.containsKey(script)) {
                throw new IllegalStateException("Unregistered script information.");
            }
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
         * @see SdkExpressRunner#execute(String)
         */
        public Object execute() throws QLException {
            return expressRunner.execute(getScript());
        }

        /*
         * (non-javadoc)
         * @see SdkExpressRunner#execute(String, Map)
         */
        public Object execute(@Nullable Map<String, Object> context) throws QLException {
            return expressRunner.execute(getScript(), context);
        }

        /*
         * (non-javadoc)
         * @see SdkExpressRunner#execute(String, Map, QLOptions)
         */
        public Object execute(@Nullable Map<String, Object> context, QLOptions qlOptions)
                throws QLException {
            return expressRunner.execute(getScript(), context, qlOptions);
        }
    }
}
