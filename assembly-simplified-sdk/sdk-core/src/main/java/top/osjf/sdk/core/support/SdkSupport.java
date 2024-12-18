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

import com.google.common.collect.Lists;
import top.osjf.sdk.core.*;
import top.osjf.sdk.core.exception.RequestCreateException;
import top.osjf.sdk.core.exception.UnknownRequestParameterException;
import top.osjf.sdk.core.exception.UnknownResponseParameterException;
import top.osjf.sdk.core.util.*;
import top.osjf.sdk.core.caller.Callback;

import java.lang.reflect.*;
import java.util.*;
import java.util.function.Function;
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
    protected static final Map<Class<? extends Request>, List<Field>> FIELD_CACHE
            = new SynchronizedWeakHashMap<>();

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
     * When passing parameters that are not directly displayed as {@code Request},
     * perform dynamic creation of {@code Request} based on specific annotations
     * {@link RequestType} and related interfaces {@link RequestTypeSupplier}.
     *
     * <p>In version 1.0.2, proxy methods are supported to directly
     * add {@link Callback} parameters or their array forms. This
     * method can be self parsed and retained for execution at a
     * reasonable time in the future.
     * <p>The code example is as follows:
     * the passing parameters of {@code Callback} can be processed.
     * <pre>{@code
     *    public interface ExampleSdkInterface{
     *          RequestType(ExampleRequest.class)
     *          ExampleResponse test(Callback[] callbackArray,List<Callback> callbackList);
     *    }
     * }</pre>
     * <p>It can also handle mixed data types of {@code Object} arrays
     * or collections, filtering out {@code Callback} types and dynamically
     * creating {@code Request} as parameters.
     * <p>The code example is as follows:
     * <pre>{@code
     *    ExampleCallback exCallback = ...;
     *    Object[] callbackArray = {1,2,exCallback}; // 1,2 will be filtered and used
     *    //as a parameter to create a Request.
     *    ExampleCallback exCallback0 = ...;
     *    List<Object> callbackList = Lists.newArrayList("123",447,exCallback0); //"123",447 will
     *    //be filtered and used as a parameter to create a Request.
     *    public interface ExampleSdkInterface {
     *          RequestType(ExampleRequest.class)
     *          ExampleResponse test(Object[] callbackArray,List<Object> callbackList);
     *    }
     * }</pre>
     *
     * @param method target method.
     * @param args   exec target method args.
     * @return the {@code Pair} with first {@code Request} and second
     * {@code List<Callback>}.
     * @throws NullPointerException             If the input method is {@literal null}.
     * @throws UnknownRequestParameterException If the {@code Request} instance found from the parameter
     *                                          is not unique or the {@code Request} type is not found
     *                                          from dynamic annotations or parameter specific interfaces.
     * @see ResponseData
     * @see RequestType
     * @see Callback
     */
    public static Pair<Request<?>, List<Callback>> invokeCreateRequest(@NotNull Method method, @Nullable Object[] args) {
        Request<?> request = null;
        List<Callback> callbacks = new ArrayList<>();

        List<Object> argList = args == null ? Lists.newArrayList() : Lists.newArrayList(args);

        //Loop parameter array to find the single or array form of the Request
        // class and callback interface that supports method parsing.
        List<Object> delArgs = null;
        if (!argList.isEmpty()) {

            //Filter whether there is a Request instance in the parameter group.
            List<Object> requestInstances = argList.stream()
                    .filter(arg -> arg instanceof Request)
                    .collect(Collectors.toList());

            //Ensure that there is only one Request instance.
            if (CollectionUtils.isNotEmpty(requestInstances)) {
                if (requestInstances.size() > 1) {
                    throw new UnknownRequestParameterException();
                }
                request = (Request<?>) requestInstances.get(0);
            } else {
                //Parameter filtering removes items and only initializes
                // when the provided parameters do not have a Request instance.
                delArgs = new ArrayList<>();
            }
            for (Object arg : argList) {

                if (arg instanceof Callback) {
                    callbacks.add((Callback) arg);
                    if (delArgs != null) delArgs.add(arg);

                    /*
                     * The following types of filtering rules take the available
                     * top.osjf.sdk.core.util.caller.Callback parameters and keep
                     * the rest.
                     *
                     * If all parameters are of type top.osjf.sdk.core.util.caller.Callback,
                     * the entire parameter will be deleted and will not participate in the
                     * subsequent dynamic construction of Request.
                     * */

                } else if (arg instanceof Collection) {

                    //Collection type filtering.
                    filteringCollection(arg, callbacks, delArgs);
                } else if (arg.getClass().isArray()) {

                    //Array type filtering.
                    filteringArray(arg, callbacks, delArgs, argList);
                }

                /*
                 * If you define callbacks in entity objects or maps,
                 *  we do not support parsing here.
                 */
            }
        }

        //When no relevant Request instance is found for the above parameters,
        // perform a dynamic construction process.
        if (request == null) {

            //The callback method parameters do not participate in the
            // reflection dynamic construction of the request.
            if (CollectionUtils.isNotEmpty(delArgs)) argList.removeAll(delArgs);

            //When no found request in args , use reflection to construct
            // the request parameter based on the marked request type.

            /* Participate more in the processing logic of 0 parameters. */

            //The following is how to discover the types of request parameters through
            // specific annotations and interfaces.
            Class<? extends Request> requestType;

            //Annotations take precedence over parameters.
            //See top.osjf.sdk.core.RequestType javadoc.
            RequestType requestTypeAnnotation = method.getAnnotation(RequestType.class);
            if (requestTypeAnnotation != null) {
                requestType = requestTypeAnnotation.value();
            } else {

                //When there are no annotations, consider whether
                // the parameter provides a Request type.
                List<Class<? extends Request>> requestTypes = null;
                if (!argList.isEmpty()) {
                    requestTypes = Arrays.stream(args)
                            .map((Function<Object, Class<? extends Request>>) o ->
                                    o instanceof RequestTypeSupplier ? ((RequestTypeSupplier) o).getRequestType() : null)
                            .filter(Objects::nonNull)
                            .distinct()
                            .collect(Collectors.toList());
                }

                //If no relevant type is found or the number of found is greater
                // than 1, it cannot be confirmed as unique and will not be processed.
                //https://mvnrepository.com/artifact/top.osjf.sdk/sdk-core/1.0.1/issue1
                if (CollectionUtils.isEmpty(requestTypes) || requestTypes.size() > 1) {
                    throw new UnknownRequestParameterException();
                }
                requestType = requestTypes.get(0);
            }

            //After obtaining the type of the requested parameter, assign necessary
            // property values based on the parameter and specific annotations.
            request = invokeCreateRequestConstructorWhenFailedUseSet(requestType, argList.toArray());
        }

        return Pair.create(request, callbacks);
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
    @Nullable
    public static Object getResponse(@NotNull Method method, @Nullable Response response) {
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
            } else responseData.getData();
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
        T instance = ServiceLoadManager.loadHighPriority(type);
        if (instance == null) {
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
    public static Type getResponseType(@NotNull Request<?> request, @Nullable Type def) {
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
        if (resultType == null) {
            if (def == null) {
                throw new IllegalStateException("No available generic type were found.");
            }
            GENERIC_CACHE.put(inletClass, def);
        } else {
            GENERIC_CACHE.putIfAbsent(inletClass, resultType);
        }
        return GENERIC_CACHE.get(inletClass);
    }


    /*  ################################### Internal assistance methods. ###################################  */

    // filtering collection type
    //All hits are directly placed in the deletion list, and some hits delete some hit parameters.
    static void filteringCollection(@NotNull Object arg, @NotNull List<Callback> callbacks,
                                    @Nullable List<Object> delArgs) {
        Collection<Object> argc = (Collection<Object>) arg;
        List<Object> isCallbackArgs = null;
        for (Object ac : argc) {
            isCallbackArgs = resolveLoopCallback(ac, callbacks, isCallbackArgs);
        }
        if (delArgs != null && isCallbackArgs != null) {
            if (isCallbackArgs.size() == argc.size()) {
                delArgs.add(argc);
            } else {
                argc.removeAll(isCallbackArgs);
            }
        }
    }

    // filtering array type
    //All hits are directly placed in the deletion list, and some
    // hits delete some hit parameters and update the value.
    static void filteringArray(@NotNull Object arg, @NotNull List<Callback> callbacks,
                               @Nullable List<Object> delArgs, @NotNull List<Object> argList) {
        Object[] array = ArrayUtils.toArray(arg);
        List<Object> isCallbackArgs = null;
        for (Object arr : array) {
            isCallbackArgs = resolveLoopCallback(arr, callbacks, isCallbackArgs);
        }
        if (delArgs != null && isCallbackArgs != null) {
            if (isCallbackArgs.size() == array.length) {
                delArgs.add(arg);
            } else {
                List<Object> list = Lists.newArrayList(array);
                list.removeAll(isCallbackArgs);
                argList.set(argList.indexOf(arg), list.toArray());
            }
        }
    }

    //resolve arg to Callback in for loop.
    static List<Object> resolveLoopCallback(@NotNull Object arg, @NotNull List<Callback> callbacks,
                                            @Nullable List<Object> isCallback) {
        if (arg instanceof Callback) {
            callbacks.add((Callback) arg);
            if (isCallback == null) isCallback = new ArrayList<>();
            isCallback.add(arg);
        }
        return isCallback;
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

    //First, create the request according to the construction method.
    // If the creation fails, use the set method or reflection assignment with specific annotations.
    static Request<?> invokeCreateRequestConstructorWhenFailedUseSet(Class<? extends Request> requestType,
                                                                     Object... args) {
        Request<?> request;
        try {
            //First, directly instantiate the request class using the
            // construction method based on the parameters.
            request = ReflectUtil.instantiates(requestType, args);
        } catch (Throwable e) {
            //This step determines whether the parameter is empty to
            // determine whether the above is an empty construction instantiation.
            if (ArrayUtils.isEmpty(args)) throw new RequestCreateException(e);
            request = invokeCreateRequestUseSet(requestType, args);
        }
        return request;
    }

    //Create an object using an empty construct, and assign
    // values to its properties using the set method.
    static Request<?> invokeCreateRequestUseSet(Class<? extends Request> requestType,
                                                Object... args) {
        Request<?> request;
        try {
            //When parameter construction fails, first use an empty
            // construction to instantiate, and then find the set method.
            request = ReflectUtil.instantiates(requestType);

            List<Field> fields = getRequestFieldAnnotationFields(requestType);
            if (CollectionUtils.isEmpty(fields)) {
                //When using methods for assignment, annotations must be identified.
                throw new IllegalArgumentException("When no construction method is provided, please " +
                        "use \"top.osjf.assembly.simplified.sdk.process.RequestField\" to mark the real" +
                        " name of the field.");
            }
            for (int i = 0; i < fields.size(); i++) {
                Field field = fields.get(i);
                RequestField requestField = field.getAnnotation(RequestField.class);
                int order = getOrder(requestField, i, fields);
                Object arg = args[order];
                if (requestField.useReflect()) {
                    //Directly assign values in the case of sequential reflection assignment.
                    ReflectUtil.setFieldValue(request, field, arg);
                } else {
                    //The real name of the field cannot be empty at this time.
                    String fieldName = requestField.value();
                    if (StringUtils.isBlank(fieldName)) {
                        throw new IllegalArgumentException("When using the set method to set a value, " +
                                "the actual field name cannot be empty.");
                    }
                    //The set method performs an assignment.
                    executeRequestFieldSetMethod(request, requestType, fieldName, arg);
                }
            }
        } catch (Throwable e) {
            //There is no remedy at this step, simply throw an exception.
            throw new RequestCreateException(e);
        }
        return request;
    }

    //find and exec set method.
    static void executeRequestFieldSetMethod(Request<?> request, Class<? extends Request> requestType,
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

    //gets appoint requestType has annotation @RequestField fields.
    static List<Field> getRequestFieldAnnotationFields(Class<? extends Request> requestType) {
        return FIELD_CACHE.computeIfAbsent(requestType,
                t -> ReflectUtil.getAllDeclaredFields(requestType).stream()
                        .filter(field -> field.isAnnotationPresent(RequestField.class))
                        .collect(Collectors.toList()));
    }

    //get method arg order
    static int getOrder(RequestField requestField, int i, List<Field> fields) {
        int order = requestField.order();
        if (order == -1) {
            //If the default value is used for sorting,
            // it will be sorted in the default order of times.
            order = i;
        } else {
            if (order >= fields.size()) {
                throw new ArrayIndexOutOfBoundsException("Current order " + order + "," +
                        " parameter length " + fields.size() + ".");
            }
        }
        return order;
    }
}
