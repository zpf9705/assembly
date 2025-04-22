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

package top.osjf.sdk.core.support;

import top.osjf.sdk.core.*;
import top.osjf.sdk.core.caller.*;
import top.osjf.sdk.core.exception.UnknownRequestParameterException;
import top.osjf.sdk.core.exception.UnknownResponseParameterException;
import top.osjf.sdk.core.lang.NotNull;
import top.osjf.sdk.core.spi.SpiLoader;
import top.osjf.sdk.core.util.*;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

/**
 * SDK Support class for handling the creation of request objects and conversion
 * of response data.
 *
 * <p>This class provides a series of static methods for generating request objects
 * based on method parameters and annotations,as well as converting response data
 * into the desired type. By utilizing reflection and annotation processing, this
 * class can dynamically handle different types of requests and responses.
 *
 * <p>Key features include:
 * <ul>
 *     <li>Support for marking request types through annotations and interfaces.</li>
 *     <li>Automatic creation of request objects based on method parameters.</li>
 *     <li>Conversion of response data into specified types.</li>
 *     <li>Use of weak reference caching to prevent memory leaks.</li>
 * </ul>
 *
 * <p>Designed as an abstract class, although no abstract methods are currently
 * defined, it leaves room for extension,allowing for the addition of more SDK
 * support-related functionalities in the future.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public abstract class SdkSupport {

    /**
     * Since 1.0.2,cache modification to weak references, freeing up memory in appropriate
     * places to prevent memory leaks.
     */
    protected static final Map<String, Method> METHOD_CACHE = new SynchronizedWeakHashMap<>();

    /*** cache for dynamically obtain the type of response class.
     * Since 1.0.2,cache modification to weak references, freeing up memory in appropriate
     * places to prevent memory leaks.
     * */
    protected static final Map<Class<?>, Type> GENERIC_CACHE = new SynchronizedWeakHashMap<>();

    /***The left angle bracket included in the generic.*/
    protected static final String LEFT_ANGLE_BRACKET = "<";

    /***The right angle bracket included in the generic.*/
    protected static final String RIGHT_ANGLE_BRACKET = ">";

    /**
     * The parsing requires the use of parameters to create a record interface {@link RequestExecuteMetadata}
     * for various metadata of the SDK request class.
     *
     * @since 1.0.2
     */
    private static class ParameterResolveRequestExecuteMetadata implements RequestExecuteMetadata {
        Request<?> request;
        Method method;
        OptionsMetadata optionsMetadata;
        ParameterResolveRequestExecuteMetadata(Request<?> request,
                                               Method method,
                                               List<Callback> callbacks,
                                               ThrowablePredicate throwablePredicate,
                                               AsyncPubSubExecutorProvider executorProvider) {
            this.request = request;
            this.method = method;
            if (RequestCaller.condition(method)) {
                optionsMetadata = new ParameterResolveOptionsMetadata(callbacks, throwablePredicate, executorProvider);
            }
        }
        @Override
        public Request<?> getRequest() {
            return request;
        }
        @Override
        public Method getMethod() {
            return method;
        }
        @Override public OptionsMetadata getOptionsMetadata() {
            return optionsMetadata;
        }

        /**
         * The relevant attribute instance object about {@link CallOptions} obtained from parameter
         * parsing, the existence basis of this class, and the existence of annotation {@link CallOptions}.
         * @since 1.0.2
         */
        static class ParameterResolveOptionsMetadata implements OptionsMetadata {
            List<Callback> callbacks;
            ThrowablePredicate throwablePredicate;
            AsyncPubSubExecutorProvider executorProvider;
            ParameterResolveOptionsMetadata(List<Callback> callbacks,
                                            ThrowablePredicate throwablePredicate,
                                            AsyncPubSubExecutorProvider executorProvider) {
                this.callbacks = callbacks;
                this.throwablePredicate = throwablePredicate;
                this.executorProvider = executorProvider;
            }
            @Override
            public List<Callback> getCallbacks() {
                return callbacks;
            }
            @Override
            public ThrowablePredicate getThrowablePredicate() {
                return throwablePredicate;
            }
            @Override public AsyncPubSubExecutorProvider getSubscriptionExecutorProvider() {
                return executorProvider;
            }
        }
    }

    /**
     * A simple implementation class for encapsulating and obtaining {@link AsyncPubSubExecutorProvider}.
     * @since 1.0.2
     */
    private static class AsyncPubSubExecutorProviderImpl implements AsyncPubSubExecutorProvider {
        Executor subscriptionExecutor;
        Executor observeExecutor;
        AsyncPubSubExecutorProviderImpl(Executor subscriptionExecutor,
                                        Executor observeExecutor) {
            this.subscriptionExecutor = subscriptionExecutor;
            this.observeExecutor = observeExecutor;
        }
        @Override public Executor getCustomSubscriptionExecutor() {
            return subscriptionExecutor;
        }
        @Override public Executor getCustomObserveExecutor() {
            return observeExecutor;
        }
    }

    /**
     * When passing parameters that are not directly displayed as {@code Request},
     * perform dynamic creation of {@code Request} based on specific annotations
     * {@link RequestType} and related interfaces {@link RequestTypeSupplier}.
     *
     * <p>Version 1.0.2 supports the use of dynamic parameters to build {@code Request}
     * instances, which includes annotations for {@link RequestSetter} using the
     * set scheme, {@link RequestConstructor} annotations using the constructor
     * parameter scheme, and support for specific {@link Callback} type parameters.
     *
     * <p>Intuitively experience through code cases:
     * <pre>{@code
     *
     *    public interface ExampleSdkInterface{
     *          RequestType(ExampleRequest.class)
     *          ExampleResponse test(@RequestSetter String address,@RequestConstructor Integer status,
     *          List<Callback> callbacks);
     *    }
     *
     *    class ExampleRequest implements Request<ExampleResponse>{
     *        private Integer status;
     *        private String address;
     *        public ExampleRequest(Integer status){
     *            this.status = status;
     *        }
     *        public void setAddress(String address){
     *            this.address = address;
     *        }
     *    }
     * }
     * </pre>
     *
     * @param method target method.
     * @param args   exec target method args.
     * @return the {@code Pair} with first {@code Request} and second
     * {@code List<Callback>}.
     * @throws NullPointerException             If the input method is {@literal null}.
     * @throws UnknownRequestParameterException If the {@code Request} instance found from the parameter
     *                                          is not unique or the {@code Request} type is not found
     *                                          from dynamic annotations or parameter specific interfaces.
     * @see RequestType
     * @see RequestTypeSupplier
     * @see RequestSetter
     * @see RequestConstructor
     * @see AsyncPubSubExecutorProvider
     * @see Subscription
     * @see Observe
     */
    public static RequestExecuteMetadata createRequest(Method method, Object[] args) {
        Request<?> request = null; //create request.
        List<Callback> callbacks = new ArrayList<>(); //parameter callbacks.
        ThrowablePredicate throwablePredicate = null;
        AsyncPubSubExecutorProvider executorProvider = null;
        Executor subscriptionExecutor = null;
        Executor observeExecutor = null;
        Map<String, Pair<Boolean, Object>> setterMetadata = new HashMap<>(); //support setter data.
        /*
         *  When the parameter does not provide the type of Request,
         *  find the annotation for the method.
         * */
        if (args == null) {
            RequestType requestType = method.getAnnotation(RequestType.class);
            if (requestType == null) {
                throw new UnknownRequestParameterException();
            }
            request = ReflectUtil.instantiates(requestType.value());
        } else {
            //First, filter to see if there are any Request instances.
            List<Object> requestInstances = Arrays.stream(args)
                    .filter(r -> r instanceof Request).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(requestInstances)) {
                if (requestInstances.size() > 1)
                    throw new UnknownRequestParameterException(); //Only one request can exist.
                request = (Request<?>) requestInstances.get(0);
            }
            //the reflection creation type when there is no request for the parameter.
            Class<? extends Request> requestType = null;
            //support for RequestConstructor annotation as constructor args.
            //Arrange the construction parameter groups in the order provided by the annotations.
            SortedMap<Integer, Object> constructorArgs = null;
            if (request == null) constructorArgs = new TreeMap<>();//Initialize only when no instance exists.
            Parameter[] parameters = method.getParameters();
            for (int i = 0; i < args.length; i++) {
                Object arg = args[i];
                if (arg == null) continue; // null filter continue.
                if (arg instanceof Request) continue;//It has been resolved in the above steps.
                /*
                 * Support type 1: Retrieve the type of Request from a specific interface
                 * and support subsequent parsing.
                 */
                if (arg instanceof RequestTypeSupplier && request == null) {
                    Class<? extends Request> rt = ((RequestTypeSupplier) arg).getRequestType();
                    if (requestType == null) {
                        requestType = rt;
                    } else {
                        if (!Objects.equals(requestType, rt)) {
                            throw new UnknownRequestParameterException();
                        }
                    }
                }
                Parameter parameter = parameters[i];
                /*
                 * Support type 2: Callback type parameter parsing, supporting object instance,
                 * collection, array and other types for subsequent parsing.
                 */
                if (arg instanceof Callback) {
                    callbacks.add((Callback) arg);
                } else if (arg instanceof Collection) {
                    //2-arg-collection-1: all of them are Callback elements.
                    if (((Collection<?>) arg).stream().allMatch(c -> c instanceof Callback)) {
                        callbacks.addAll((Collection<? extends Callback>) arg);
                    }
                } else if (arg.getClass().isArray()) {
                    //2-arg-array-1: all of them are Callback elements.
                    if (Callback.class.isAssignableFrom(arg.getClass().getComponentType())) {
                        for (Object o : ArrayUtils.toArray(arg)) {
                            callbacks.add((Callback) o);
                        }
                    }
                }
                /*
                 * Support type 3: Support annotation RequestConstructor parsing, provided that
                 * the parameter array does not have a Request instance.
                 */
                if (request == null && parameter.isAnnotationPresent(RequestConstructor.class)) {
                    RequestConstructor requestConstructor = parameter.getAnnotation(RequestConstructor.class);
                    if (requestConstructor.required()) {
                        constructorArgs.put(requestConstructor.order(), arg);
                    }
                }
                /*
                 * Support type 4:Support annotation RequestSetter parsing, perform set related
                 * assignment after obtaining the Request instance.
                 */
                if (parameter.isAnnotationPresent(RequestSetter.class)) {
                    RequestSetter requestSetter = parameter.getAnnotation(RequestSetter.class);
                    String name = requestSetter.name();
                    if (StringUtils.isBlank(name)) name = parameter.getName();
                    setterMetadata.put(name, Pair.create(requestSetter.useReflect(), arg));
                }
                /*
                 * Support type 5:Support parsing the first {@code ThrowablePredicate} instance
                 * object from parameters.
                 */
                if (throwablePredicate == null && arg instanceof ThrowablePredicate) {
                    throwablePredicate = (ThrowablePredicate) arg;
                }
                /*
                 * Support type 6:Support parsing the first {@code AsyncPubSubExecutorProvider} instance
                 * object from the parameters, or the subscriber {@code Executor} instance object marked
                 * with annotation {@code Subscription}, or the observer {@code Executor} instance object
                 * marked with annotation {@code Observe}.
                 */
                if (executorProvider == null && arg instanceof AsyncPubSubExecutorProvider) {
                    executorProvider = (AsyncPubSubExecutorProvider) arg;
                }
                if (executorProvider == null
                        && (subscriptionExecutor == null || observeExecutor == null)
                        && arg instanceof Executor) {
                    if (parameter.isAnnotationPresent(Subscription.class)) {
                        subscriptionExecutor = (Executor) arg;
                    } else if (parameter.isAnnotationPresent(Observe.class)) {
                        observeExecutor = (Executor) arg;
                    }
                }
            }
            //After the loop ends, initialize using the type based on whether
            // the request instance exists.
            if (request == null) {
                if (requestType == null) {
                    RequestType annotation = method.getAnnotation(RequestType.class);
                    if (annotation == null) {
                        throw new UnknownRequestParameterException();
                    }
                    requestType = annotation.value();
                }
                if (constructorArgs.isEmpty()) {
                    request = ReflectUtil.instantiates(requestType);
                } else {
                    request = ReflectUtil.instantiates(requestType,
                            constructorArgs.values().toArray());
                }
            }
        }
        //Finally, perform set support assignment on the instantiated request instance.
        if (!setterMetadata.isEmpty()) {
            for (Map.Entry<String, Pair<Boolean, Object>> entry : setterMetadata.entrySet()) {
                String name = entry.getKey();
                Pair<Boolean, Object> assignmentInfo = entry.getValue();
                Object value = assignmentInfo.getSecond();
                if (assignmentInfo.getFirst()) {
                    ReflectUtil.setFieldValue(request, name, value);
                } else {
                    executeSetMethod(request, request.getClass(), name, value);
                }
            }
        }
        return new ParameterResolveRequestExecuteMetadata(request, method, callbacks, throwablePredicate,
                executorProvider != null ? executorProvider :
                        (subscriptionExecutor != null || observeExecutor != null) ?
                                new AsyncPubSubExecutorProviderImpl(subscriptionExecutor, observeExecutor) : null);
    }

    /**
     * Determine the specific return value type based on the actual return type
     * of the proxy method and whether the obtained {@code Response} instance
     * extends to the {@link ResponseData} and {@link InspectionResponseData}
     * interfaces.
     *
     * @param method   target method.
     * @param response response instance.
     * @return The required return object.
     * @throws NullPointerException              if the input method is {@literal null}.
     * @throws UnknownResponseParameterException If the {@code ClassCastException} error reason
     *                                           is returned indicating that the object and code
     *                                           method types are inconsistent.
     * @see ResponseData
     * @see InspectionResponseData
     */
    public static Object resolveResponse(Method method, Response response) {
        if (response == null) return null;
        Class<?> returnType = method.getReturnType();

        //First, check if the return value type of the proxy method
        // is a subclass of Response. If so, return the current Response directly.
        if (Response.class.isAssignableFrom(returnType)) {
            return response;
        }
        Object data = null;
        if (response instanceof ResponseData) {
            ResponseData responseData = (ResponseData) response;
            if (responseData.inspectionResponseResult()) {
                //If the type value is specified, the data returned
                // when the request is successful is obtained.
                if (responseData.isSuccess()) {
                    data = responseData.getData();
                } else {
                    //The default data returned when a customization request fails.
                    if (responseData instanceof InspectionResponseData) {
                        data = ((InspectionResponseData) responseData).failedSeatData();
                    }
                }
                //On the contrary, directly return the data result.
                //issue for 2025.02.18
            } else data = responseData.getData();
        }

        //If the final data is not empty, then verify whether
        // it is of the type returned by the proxy method.
        if (data != null) {
            if (!returnType.isInstance(data)) {
                throw new UnknownResponseParameterException
                        (new ClassCastException(data.getClass().getName() + " cannot be cast to " + returnType.getName()));
            }
        }

        return data;
    }

    /**
     * Loads an instance of a specified type.
     *
     * <p>This method first attempts to load an instance of the specified type
     * using a high-priority service loader mechanism.
     *
     * <p>If that fails to provide an instance, it then attempts to load an instance
     * of a class specified by a fully qualified class name string ({@code def}).
     * If the class can be found and instantiated successfully, it returns the
     * new instance.If not, it throws an appropriate exception.
     *
     * <p>This approach allows for flexible instantiation, supporting both service-based
     * discovery and explicit class name specification.
     *
     * @param <T>  The type of the instance to load.
     * @param type The {@code Class<T>} object representing the type of the instance to load.
     * @param def  A fully qualified class name string of the class to instantiate if the
     *             service loader does not provide an instance.
     * @return An instance of the specified type.
     * @throws NullPointerException     If the input type is {@literal null}.
     * @throws IllegalArgumentException If the specified class name cannot be found or if
     *                                  an instance cannot be created from it.
     * @since 1.0.2
     */
    public static <T> T loadInstance(@NotNull Class<T> type, String def) {
        T instance = SpiLoader.of(type).loadHighestPriorityInstance();
        if (instance == null) {
            if (def == null){
                return null;
            }
            instance = ReflectUtil.instantiates(def, type.getClassLoader());
        }
        return instance;
    }

    /**
     * Retrieves the required response type for a given request.
     *
     * <p>This method determines and returns the required response type based on the
     * passed request object and its inheritance hierarchy.
     * It first attempts to retrieve the type from a cache, and if not found, traverses
     * the class hierarchy (including interfaces and parent classes)
     * of the request object to find a matching response type. If a matching generic type
     * is found, it is returned; otherwise,
     * if no matching type is found, the default type is returned.</p>
     *
     * @param request The request object used to determine the required response type.
     * @param def     The default type to return if no matching response type is found.
     * @return The required response type for the request, or the default type if none is found.
     * @throws NullPointerException  if input {@code Request} is {@literal null}.
     * @throws IllegalStateException If no available generic class is found and there is
     *                               no default value.
     * @see Class#getGenericInterfaces()
     * @see Class#getGenericSuperclass()
     */
    public static Type getResponseType(Request<?> request, Type def) {
        final Class<?> inletClass = request.getClass();
        Type resultType = GENERIC_CACHE.get(inletClass);
        if (resultType != null) {
            return resultType;
        }
        Class<?> clazz = inletClass;
        while (true) {
            ClassLoader classLoader = clazz.getClassLoader();
            List<Type> types = new ArrayList<>();
            Type genericSuperclass = clazz.getGenericSuperclass();
            if (genericSuperclass != null) {
                types.add(genericSuperclass);
            }
            Type[] genericInterfaces = clazz.getGenericInterfaces();
            if (ArrayUtils.isNotEmpty(genericInterfaces)) {
                types.addAll(Arrays.asList(genericInterfaces));
            }
            if (CollectionUtils.isEmpty(types)) {
                break;
            }
            Pair<Type, Class<?>> pair = getTypePair(types, classLoader);
            Type type = pair.getFirst();
            if (type instanceof ParameterizedType) {
                resultType = getResponseGenericType(type, classLoader);
                if (resultType != null) {
                    break;
                } else {
                    clazz = pair.getSecond();
                }
            } else {
                clazz = pair.getSecond();
            }
        }
        if (resultType == null && def == null) {
            throw new IllegalStateException("No available generic type were found.");
        }
        GENERIC_CACHE.putIfAbsent(inletClass, resultType != null ? resultType : def);
        return GENERIC_CACHE.get(inletClass);
    }


    /*  ################################### Internal assistance methods. ###################################  */

    //find and exec set method.
    static void executeSetMethod(Request<?> request, Class<? extends Request> requestType,
                                 String filedName, Object arg) {
        final String setMethodName = "set" + Character.toUpperCase(filedName.charAt(0))
                + filedName.substring(1);
        final String cacheKey = requestType.getName() + "@" + setMethodName;
        Method setMethod = METHOD_CACHE.computeIfAbsent(cacheKey, s -> {
            for (Method method : ReflectUtil.getAllDeclaredMethods(requestType)) {
                if (method.getName().equals(setMethodName) // name equal
                        && method.getParameterTypes().length == 1 // param len = 1
                        && method.getParameterTypes()[0].isAssignableFrom(arg.getClass())) // arg is param instance
                {
                    return method;
                }
            }
            throw new IllegalArgumentException(new NoSuchMethodException(setMethodName));
        });
        ReflectUtil.invokeMethod(request, setMethod, arg);
    }

    //Find a subclass belonging to top.osjf.sdk.core.Request from numerous generic classes.
    private static Pair<Type, Class<?>> getTypePair(List<Type> types, ClassLoader classLoader) {
        Type requestType = null;
        Class<?> requestClass = null;
        for (Type type : types) {
            requestClass = getAssignableTypeClass(Request.class, type, classLoader);
            if (requestClass != null) {
                requestType = type;
                break;
            }
        }
        return Pair.create(requestType, requestClass);
    }

    //Find the subclass generics belonging to top.osjf.sdk.core.Response
    // from the numerous generics of the specified type.
    static Type getResponseGenericType(Type type, ClassLoader classLoader) {
        Type[] actualTypes = ((ParameterizedType) type).getActualTypeArguments();
        // as Response and retrieve the first one.
        Type responseType = null;
        for (Type actualType : actualTypes) {
            if (getAssignableTypeClass(Response.class, actualType, classLoader) != null) {
                responseType = actualType;
                break;
            }
        }
        return responseType;
    }

    //Determine whether the specified type is a subclass of a certain type,
    // and if so, return it, not null.
    static Class<?> getAssignableTypeClass(Class<?> clazz, Type type, ClassLoader classLoader) {
        Class<?> typeClass = getTypeClass(type, classLoader);
        if (clazz.isAssignableFrom(typeClass)) {
            return typeClass;
        }
        return null;
    }

    //Obtain a class of type {@code Type} and consider the type {@code java.lang.reflect.ParameterizedType}.
    static Class<?> getTypeClass(Type type, ClassLoader classLoader) {
        if (type instanceof Class) {
            return (Class<?>) type;
        }
        String typeName = type.getTypeName();
        String className = typeName;
        //A generic type name that carries<...>, After removing such content,
        // the original name of the class is obtained.
        if (typeName.contains(LEFT_ANGLE_BRACKET) && typeName.contains(RIGHT_ANGLE_BRACKET)) {
            className = typeName.split(LEFT_ANGLE_BRACKET)[0];
        }
        return ReflectUtil.loadClass(className, classLoader);
    }
}
