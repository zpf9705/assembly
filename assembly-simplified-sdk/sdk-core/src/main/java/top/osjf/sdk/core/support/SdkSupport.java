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

import top.osjf.sdk.core.exception.RequestCreateException;
import top.osjf.sdk.core.exception.UnknownRequestParameterException;
import top.osjf.sdk.core.exception.UnknownResponseParameterException;
import top.osjf.sdk.core.process.*;
import top.osjf.sdk.core.util.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
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

    /***The prefix name of the set method.*/
    protected static final String SET_METHOD_PREFIX = "set";

    /**
     * Since 1.0.2,cache modification to weak references, freeing up memory in appropriate
     * places to prevent memory leaks.
     */
    protected static final Map<Class<? extends Request>, List<Field>> requestFilteredDeclaredFieldCache
            = new SynchronizedWeakHashMap<>();

    /**
     * Since 1.0.2,cache modification to weak references, freeing up memory in appropriate
     * places to prevent memory leaks.
     */
    protected static final Map<Class<? extends Request>, List<Method>> requestMethodCache
            = new SynchronizedWeakHashMap<>();

    /*** cache for dynamically obtain the type of response class.
     * Since 1.0.2,cache modification to weak references, freeing up memory in appropriate
     * places to prevent memory leaks.
     * */
    protected static final Map<Class<?>, Class<? extends Response>> GENERIC_CACHE = new SynchronizedWeakHashMap<>();

    /***The left angle bracket included in the generic.*/
    protected static final String LEFT_ANGLE_BRACKET = "<";

    /***The right angle bracket included in the generic.*/
    protected static final String RIGHT_ANGLE_BRACKET = ">";

    /**
     * Create corresponding request parameters based on extension
     * type annotations and interfaces.
     *
     * @param method Proxy target method.
     * @param args   Request parameters.
     * @return The request class parameters created.
     * @throws NullPointerException If the input method is {@literal null}.
     * @see ResponseData
     * @see RequestParam
     */
    public static Request<?> invokeCreateRequest(@NotNull Method method, Object[] args) {
        int length = ArrayUtils.isEmpty(args) ? 0 : args.length;

        //In a single parameter scenario, only consider whether it is a request class.
        if (length == 1) {
            Object arg = args[0];
            if (arg instanceof Request) {
                //Consider first whether it is the actual request parameter.
                return (Request<?>) arg;
            }
        }
        //When a single parameter is not a request, use reflection to construct
        // the request parameter based on the marked request type.

        /* Participate more in the processing logic of 0 parameters. */

        //The following is how to discover the types of request parameters through
        // specific annotations and interfaces.
        Class<? extends Request> requestType;

        //Consider annotations first.
        RequestParam requestParam = method.getAnnotation(RequestParam.class);
        if (requestParam != null) {
            requestType = requestParam.value();
        } else {

            //Secondly, consider whether the parameters inherit specific interfaces.
            List<Class<? extends Request>> requestTypes = Arrays.stream(args)
                    .map((Function<Object, Class<? extends Request>>) o ->
                            o instanceof RequestParameter ? ((RequestParameter) o).getRequestType() : null)
                    .filter(Objects::nonNull)
                    .distinct()
                    .collect(Collectors.toList());

            //If a specific interface with multiple parameters is used to indicate
            // the request type, I cannot know the specific type without ensuring uniqueness.
            if (CollectionUtils.isNotEmpty(requestTypes) || requestTypes.size() > 1) {
                throw new UnknownRequestParameterException();
            }

            //Select 1 for a specific type.
            requestType = requestTypes.get(0);
        }

        //After obtaining the type of the requested parameter, assign necessary
        // property values based on the parameter and specific annotations.
        return invokeCreateRequestConstructorWhenFailedUseSet(requestType, args);
    }

    /**
     * When obtaining a response, convert to the desired type, which can be
     * specified in {@link ResponseData}.
     *
     * @param method   Proxy target method.
     * @param response The response type obtained.
     * @return The required return object.
     * @throws NullPointerException If the input method is {@literal null}.
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
    public static <R extends Response> Class<R> getResponseRequiredType(@NotNull Request<?> request,
                                                                        @Nullable Class<? extends Response> def) {


        //The class object of the current request class.
        final Class<?> inletClass = request.getClass();

        //Try reading the cache first.
        Class<? extends Response> resultClass = GENERIC_CACHE.get(inletClass);
        if (resultClass != null) {
            return (Class<R>) resultClass;
        }

        //Loop stop marker.
        boolean goWhile = true;

        //Cache temporary type class objects.
        Map<Type, Class<?>> genericInternalCache = new ConcurrentHashMap<>(16);

        //There is no cache to execute the fetch logic.
        Class<?> clazz = inletClass;
        while (goWhile) {
            //Collect the type of the class for visual objects.
            //Including interfaces and classes.
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

            //Filter the main class of the request main class, taking the first one.
            Type typeFilter = types.stream().filter(linkType -> {
                Class<?> typeClass = getTypeClass(linkType, classLoader);
                if (typeClass == null) {
                    return false;
                }
                //Cache the class objects of the current classes for future use
                genericInternalCache.putIfAbsent(linkType, typeClass);
                return request.isAssignableRequest(typeClass);
            }).findFirst().orElse(null);

            //If there is nothing available, simply exit the loop.
            if (typeFilter == null) {
                goWhile = false;
                continue;
            }

            //If it is a type that carries a generic, then obtain whether
            // it contains a generic that responds to the main class and obtain it.
            if (typeFilter instanceof ParameterizedType) {
                Class<? extends Response> typeClass = getResponseGenericTypeClass(typeFilter, clazz.getClassLoader());
                if (typeClass != null) {
                    resultClass = typeClass;
                    //After obtaining it, bind the response generic of the
                    // main class and use it for subsequent caching.
                    GENERIC_CACHE.putIfAbsent(inletClass, resultClass);
                    goWhile = false;
                } else {

                    //The paradigm does not carry response generics, and the class
                    // object logic for filtering types is executed.
                    // According to the specification of the idea, it will not go this far.
                    clazz = genericInternalCache.get(typeFilter);
                }
            } else {

                //Non generic classes, directly use class objects of filter types for subsequent logic.
                clazz = genericInternalCache.get(typeFilter);
            }

        }

        //If the type is empty, cache a default type.
        if (resultClass == null) {
            if (def == null) {
                throw new IllegalStateException("No available generic classes were found.");
            }
            GENERIC_CACHE.put(inletClass, def);
        }
        return (Class<R>) GENERIC_CACHE.get(inletClass);
    }



    /*  ################################### Internal assistance methods. ###################################  */


    private static Class<? extends Response> getResponseGenericTypeClass(Type type, ClassLoader classLoader) {
        Type[] actualTypeArguments = ((ParameterizedType) type).getActualTypeArguments();
        // as Response and retrieve the first one.
        Class<? extends Response> responseTypeClass = null;
        for (Type actualTypeArgument : actualTypeArguments) {
            Class<?> typeClass = getTypeClass(actualTypeArgument, classLoader);
            if (Response.class.isAssignableFrom(typeClass)) {
                responseTypeClass = (Class<? extends Response>) typeClass;
                break;
            }
        }
        return responseTypeClass;
    }

    private static Class<?> getTypeClass(Type type, ClassLoader classLoader) {
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


    static Request<?> invokeCreateRequestConstructorWhenFailedUseSet(Class<? extends Request> requestType,
                                                                     Object... args) {
        Request<?> request;
        try {
            //First, directly instantiate the request class using the
            // construction method based on the parameters.
            request = createRequest(requestType, args);
        } catch (Throwable e) {
            //This step determines whether the parameter is empty to
            // determine whether the above is an empty construction instantiation.
            if (ArrayUtils.isEmpty(args)) throw new RequestCreateException(e);
            request = invokeCreateRequestUseSet(requestType, args);
        }
        return request;
    }

    static Request<?> createRequest(Class<? extends Request> requestType, Object... args)
            throws Exception {
        if (ArrayUtils.isEmpty(args)) {
            return requestType.newInstance();
        }
        List<Class<?>> parameterTypes = new ArrayList<>();
        for (Object arg : args) {
            parameterTypes.add(arg.getClass());
        }
        return requestType.getConstructor(parameterTypes.toArray(new Class[]{}))
                .newInstance(args);
    }

    static Request<?> invokeCreateRequestUseSet(Class<? extends Request> requestType,
                                                Object... args) {
        Request<?> request;
        try {
            //When parameter construction fails, first use an empty
            // construction to instantiate, and then find the set method.
            request = requestType.newInstance();

            List<Field> fields = getAndFilterRequestAndSuperDeclaredFields(requestType);
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
                    setRequestFieldValue(request, field, arg);
                } else {
                    //The real name of the field cannot be empty at this time.
                    String fieldName = requestField.value();
                    if (StringUtils.isBlank(fieldName)) {
                        throw new IllegalArgumentException("When using the set method to set a value, " +
                                "the actual field name cannot be empty.");
                    }
                    //The set method performs an assignment.
                    executeRequestFieldSetMethod(request, requestType,
                            SET_METHOD_PREFIX + Character.toUpperCase(fieldName.charAt(0))
                                    + fieldName.substring(1), arg);
                }
            }
        } catch (Throwable e) {
            //There is no remedy at this step, simply throw an exception.
            throw new RequestCreateException(e);
        }
        return request;
    }

    static void executeRequestFieldSetMethod(Request<?> request, Class<? extends Request> requestType,
                                             String methodName, Object arg) throws Exception {
        List<Method> methods = getRequestDeclaredMethods(requestType);
        for (Method method : methods) {
            if (method.getName().equals(methodName) && method.getParameterTypes().length == 1
                    && method.getParameterTypes()[0].equals(arg.getClass())) {
                try {
                    method.invoke(request, arg);
                } catch (IllegalAccessException e) {
                    method.setAccessible(true);
                    method.invoke(request, arg);
                    return;
                }
            }
        }
        throw new NoSuchMethodException(methodName);
    }

    static List<Method> getRequestDeclaredMethods(Class<? extends Request> requestType) {
        return requestMethodCache.computeIfAbsent(requestType, SdkSupport::getRequestDeclaredMethods0);
    }

    static List<Method> getRequestDeclaredMethods0(Class<? extends Request> requestType) {
        List<Method> allDeclaredMethods = new ArrayList<>();
        Class<?> searchType = requestType;
        while (searchType != null) {
            Method[] declaredMethods = searchType.getDeclaredMethods();
            if (ArrayUtils.isNotEmpty(declaredMethods)) {
                allDeclaredMethods.addAll(Arrays.asList(declaredMethods));
            }
            searchType = Object.class.equals(searchType.getSuperclass()) ? null : searchType.getSuperclass();
        }
        return allDeclaredMethods;
    }

    static List<Field> getAndFilterRequestAndSuperDeclaredFields(Class<? extends Request> requestType) {
        return requestFilteredDeclaredFieldCache.computeIfAbsent(requestType,
                SdkSupport::getAndFilterRequestAndSuperDeclaredFields0);
    }

    static List<Field> getAndFilterRequestAndSuperDeclaredFields0(Class<? extends Request> requestType) {
        List<Field> allDeclaredFields = new ArrayList<>();
        Class<?> searchType = requestType;
        while (searchType != null) {
            Field[] declaredFields = searchType.getDeclaredFields();
            if (ArrayUtils.isNotEmpty(declaredFields)) {
                allDeclaredFields.addAll(Arrays.asList(declaredFields));
            }
            searchType = Object.class.equals(searchType.getSuperclass()) ? null : searchType.getSuperclass();
        }
        return allDeclaredFields.stream()
                .filter(field -> field.isAnnotationPresent(RequestField.class)).collect(Collectors.toList());
    }

    static void setRequestFieldValue(Request<?> request, Field field, Object arg) throws Exception {
        try {
            field.set(request, arg);
        } catch (IllegalAccessException e) {
            field.setAccessible(true);
            field.set(request, arg);
        }
    }

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
