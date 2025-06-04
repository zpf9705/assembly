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
import com.alibaba.qlexpress4.annotation.QLFunction;
import com.alibaba.qlexpress4.exception.QLException;
import com.alibaba.qlexpress4.runtime.Parameters;
import com.alibaba.qlexpress4.runtime.QContext;
import com.alibaba.qlexpress4.runtime.function.CustomFunction;
import com.alibaba.qlexpress4.runtime.function.QMethodFunction;
import com.alibaba.qlexpress4.utils.BasicUtil;
import com.alibaba.qlexpress4.utils.QLFunctionUtil;
import top.osjf.sdk.core.exception.SdkException;
import top.osjf.sdk.core.lang.Nullable;
import top.osjf.sdk.core.util.ArrayUtils;
import top.osjf.sdk.core.util.ReflectUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

/**
 * The {@code SdkExpressRunner} class is used to execute scripts based on {@code QLExpress4}
 * and supports extending functionality through SDK proxy methods.
 *
 * <p>This class encapsulates the {@code QLExpress4} executor and provides convenient methods to add
 * functionality calls to SDK methods.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.3
 * @see ScriptExecutorBuilder
 * @see ScriptExecutor
 */
public class SdkExpressRunner {

    private final Express4Runner express4Runner;

    /**
     * Store the mapping relationship between standardized script names and actual script names.
     * The key is the standardized script name (format: class name @ method name), and the value
     * is the actual script name (name after removing special characters).
     */
    private final Map<String, String> standardizedScriptCorrespond = new ConcurrentHashMap<>();

    /**
     * Initialize a {@link Express4Runner} instance using {@link InitOptions} to
     * construct {@code SdkExpressRunner}.
     * @param initOptions Initialization options, used to configure the behavior
     *                   of {@link Express4Runner}.
     */
    public SdkExpressRunner(InitOptions initOptions) {
        express4Runner = new Express4Runner(initOptions);
    }

    /**
     * Create a new script executor builder for building and executing scripts.
     *
     * @return Script Executor Builder Instance.
     */
    public ScriptExecutorBuilder newScript() {
        return new ScriptExecutorBuilder(this);
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

    private static String getMethodParameterNames(Method method) {
        Parameter[] parameters = method.getParameters();
        if (ArrayUtils.isEmpty(parameters)) {
            return "";
        }
        return Arrays.stream(method.getParameters()).map(Parameter::getName)
                .collect(Collectors.joining(","));
    }

    /**
     * @return Non-modifiable standard script mapping object.
     */
    protected Map<String, String> getStandardizedScriptCorrespond() {
        return Collections.unmodifiableMap(standardizedScriptCorrespond);
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
    @Nullable
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
    @Nullable
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
    @Nullable
    public Object execute(String script, @Nullable Map<String, Object> context, QLOptions qlOptions)
            throws QLException {
        QLResult result = express4Runner.execute(getCorrespondScript(script), context, qlOptions);
        return result != null ? result.getResult() : null;
    }

    private String getCorrespondScript(String script) {
        if (standardizedScriptCorrespond.containsKey(script)) {
            return standardizedScriptCorrespond.get(script);
        }

        //Check whether the method call complies with the '()' suffix rule
        // and the rule of attaching parameter names in a timely manner
        if (!script.endsWith(")")) {
            CustomFunction function = express4Runner.getFunction(script);
            if (function == null) {
                throw new IllegalArgumentException("No SDK Function named " + script);
            }
            if (function instanceof SdkQMethodFunction) {
                script = ((SdkQMethodFunction) function).addScriptParameterNames(script);
            } else {
                script = script.concat("()");
            }

        }
        return script;
    }

    /**
     * The parameter name combination string of the method attached to the extension {@link QMethodFunction}.
     */
    private static class SdkQMethodFunction extends QMethodFunction {
        private final Method method;
        private final Object object;
        @Nullable
        private final String parameterNames;

        public SdkQMethodFunction(Object object, Method method, @Nullable String parameterNames) {
            super(object, method);
            this.method = method;
            this.object = object;
            this.parameterNames = parameterNames;
        }

        public String addScriptParameterNames(String script) {
            return script + "(" + parameterNames + ")";
        }

        @Override
        public Object call(QContext qContext, Parameters parameters) throws Throwable {
            try {
                return super.call(qContext, parameters);
            }
            catch (Throwable e) {
                return internalExceptionCall(e, parameters);
            }
        }

        private Object internalExceptionCall(Throwable e, Parameters parameters) throws Throwable {

            if (e instanceof InvocationTargetException) {
                Throwable targetException = ((InvocationTargetException) e).getTargetException();
                // The relevant subclasses of the SDK throw exceptions directly.
                if (targetException instanceof SdkException) {
                    throw targetException;
                }
            }

            // Reorganize the search according to the method parameter type and order.
            List<Object> arguments = new CopyOnWriteArrayList<>(BasicUtil.argumentsArr(parameters));
            List<Object> sortedArguments = new ArrayList<>();
            for (Class<?> parameterType : method.getParameterTypes()) {
                Object sortedArg = null;
                for (Object arg : arguments) {
                    if (parameterType.isInstance(arg)) {
                        sortedArg = arg;
                        arguments.remove(arg);
                        break;
                    }
                }
                sortedArguments.add(sortedArg);
            }

            try {
                ReflectUtil.makeAccessible(method);
                return method.invoke(object, sortedArguments.toArray());
            }
            catch (IllegalAccessException | IllegalArgumentException ex){
                throw new SdkExpressRunnerException(ex); // wrapper RuntimeException to SdkExpressRunnerException .
            }
            catch (InvocationTargetException ex) {
                // wrapper target RuntimeException to SdkExpressRunnerException .
                throw new SdkExpressRunnerException(ex.getTargetException());
            }
        }
    }
}
