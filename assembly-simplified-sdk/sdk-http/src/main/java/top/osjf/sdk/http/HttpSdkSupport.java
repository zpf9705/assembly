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

package top.osjf.sdk.http;

import top.osjf.sdk.core.process.Request;
import top.osjf.sdk.core.process.Response;
import top.osjf.sdk.core.support.SdkSupport;
import top.osjf.sdk.core.util.ArrayUtils;
import top.osjf.sdk.core.util.JSONUtil;
import top.osjf.sdk.core.util.SynchronizedWeakHashMap;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>The abstract {@code HttpSdkSupport} class is an abstract class that inherits from
 * the {@code SdkSupport} class.It provides some common support functionalities for HTTP
 * requests.
 *
 * <p>This class defines some static constants to represent the angle bracket symbols
 * in generics,and a static Map collection for caching dynamically obtained response
 * class types.The cache type is modified to use weak references to release memory at
 * appropriate places and prevent memory leaks.
 *
 * <p>Provide a detailed introduction to two important static support methods:</p>
 * <ul>
 *     <li>{@link #checkContentType}:
 *         This method checks if the Content-Type is included in the request headers.
 *         If not, it defaults to application/json. It first checks if the incoming
 *         headers are not null and contain Content-Type.If not, it decides whether to
 *         add Content-Type as application/json based on whether the request body parameters
 *         are in JSON format or whether the method confirms JSON serialization. If the
 *         request body parameters are in JSON format and not concatenated for URL parameters,
 *         it initializes the headers and adds the Content-Type.</li>
 *
 *     <li>{@link #getResponseRequiredType}:
 *         This method retrieves the required response type for a given request.
 *         It first attempts to retrieve the type from a cache. If not found, it traverses
 *         the class hierarchy (including interfaces and parent classes)of the request
 *         object to find a matching response type. If a matching generic type is found,
 *         it returns that type;otherwise, if no matching type is found, it returns the
 *         default type.</li>
 * </ul>
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
public abstract class HttpSdkSupport extends SdkSupport {

    /***The left angle bracket included in the generic.*/
    protected static final String LEFT_ANGLE_BRACKET = "<";

    /***The right angle bracket included in the generic.*/
    protected static final String RIGHT_ANGLE_BRACKET = ">";

    /*** cache for dynamically obtain the type of response class.
     * Since 1.0.2,cache modification to weak references, freeing up memory in appropriate
     * places to prevent memory leaks.
     * */
    protected static final Map<Class<?>, Object> rps_classes = new SynchronizedWeakHashMap<>();

    /**
     * Check if there is a context and give a default {@code application/json}
     * when it does not exist.
     *
     * @param headers existing request headers.
     * @param request current http request.
     * @return When the incoming request header is {@literal null}
     * , the map will be initialized and the corresponding request
     * header content will be automatically added based on the required
     * parameters. Otherwise, after checking, the original request heade
     * r map will be returned.
     */
    @SuppressWarnings("rawtypes")
    public static Map<String, String> checkContentType(Map<String, String> headers, HttpRequest request) {
        //Normal inclusion of context directly returns the current request header.
        if (headers != null && headers.containsKey("Content-Type")) {
            return headers;
        }
        try {
            //When no context type is specified, the default JSON format will
            // be added to the context based on whether the carried request
            // body parameters are in JSON format or whether the method confirms
            // JSON serialization.
            boolean isJson = false;
            //The situation of abstract definition methods.
            if (request instanceof AbstractHttpRequestParams) {
                isJson = ((AbstractHttpRequestParams<?>) request).defaultToJson();
            } else {
                //Using non abstract class defaults.
                Object requestParam = request.getRequestParam();
                if (requestParam != null && JSONUtil.isValidObject(requestParam.toString())) {
                    isJson = true;
                }
            }
            if (isJson &&
                    //When meeting JSON requirements, it is necessary to
                    // ensure that the current JSON parameters are not
                    // concatenated for URL parameters.
                    !request.montage()) {
                // Initialize information for empty request bodies to meet the situation.
                if (headers == null) headers = new ConcurrentHashMap<>(1);
                headers.putIfAbsent("Content-Type", "application/json");
            }
            /* Consider ignoring map errors that cannot be supported for addition. */
        } catch (Exception ignored) {
        }
        return headers;
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
     * @see Class#getGenericInterfaces()
     * @see Class#getGenericSuperclass()
     */
    public static Object getResponseRequiredType(Request<?> request, Object def) {

        if (request == null) {
            return null;
        }
        //Loop stop marker.
        boolean goWhile = true;

        //The final return type.
        Object type = null;

        //The class object of the current request class.
        final Class<?> inletClass = request.getClass();

        //Try reading the cache first.
        Object inlet = rps_classes.get(inletClass);
        if (inlet != null) {
            return inlet;
        }

        //Cache temporary type class objects.
        Map<Type, Class<?>> type_classes = new ConcurrentHashMap<>(16);

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
                type_classes.putIfAbsent(linkType, typeClass);
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
                Object arguments = getActualResponseTypeArguments(typeFilter, clazz.getClassLoader());
                if (arguments != null) {
                    type = arguments;
                    //After obtaining it, bind the response generic of the
                    // main class and use it for subsequent caching.
                    rps_classes.putIfAbsent(inletClass, type);
                    goWhile = false;
                } else {

                    //The paradigm does not carry response generics, and the class
                    // object logic for filtering types is executed.
                    // According to the specification of the idea, it will not go this far.
                    clazz = type_classes.get(typeFilter);
                }
            } else {

                //Non generic classes, directly use class objects of filter types for subsequent logic.
                clazz = type_classes.get(typeFilter);
            }

        }

        //If the type is empty, cache a default type.
        if (type == null) {
            rps_classes.put(inletClass, def);
        }
        return rps_classes.get(inletClass);
    }

    /**
     * Converts an object to a Map format suitable for URL concatenation.
     *
     * <p>This method receives a boolean {@code montage} and an object {@code body}
     * as parameters. When {@code montage == true} and {@code body != null}, this
     * method converts {@code body} to a {@code Map<String, Object>} object according
     * to its type for use in URL concatenation.
     *
     * <p>The conversion logic is as follows:</p>
     * <ul>
     * <li>If {@code body} is of type {@code Map}, it is directly converted to {@code Map<String, Object>},
     * with all keys being converted to string type.</li>
     * <li>If {@code body} is of type {@code String}, it attempts to parse it as a JSON
     * string and extract the internal Map parameters. If parsing fails or it is not a valid
     * JSON format, {@literal null} is returned.</li>
     * <li>If {@code body} is of another type, it attempts to convert it to a JSON string
     * and then parse the JSON string into a Map object. If any exception occurs during this
     * process (e.g., {@code body} cannot be serialized to a JSON string), an {@code IllegalArgumentException}
     * is thrown.</li>
     * </ul>
     *
     * <p>This method is primarily used to handle different types of request bodies and is
     * very useful when the request body needs to be converted to Map format for URL concatenation.
     *
     * @param montage A flag indicating whether to perform the conversion. If {@code false}, {@literal null}
     *                is returned directly.
     * @param body    The object to be converted. It can be of type {@code Map}, {@code String}, or other types.
     * @return The converted {@code Map<String, Object>} object. If no conversion is needed or the conversion fails,
     * {@literal null} is returned.
     * @throws IllegalArgumentException Thrown if the {@code body} type cannot be correctly converted to Map format.
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> urlMontageBody(boolean montage, Object body) {
        Map<String, Object> montageParams = null;
        if (montage && body != null) {
            if (body instanceof Map) {
                //When obtaining the body type in map format,
                // it can be directly converted.
                Map<Object, Object> mapInstanceof = (Map<Object, Object>) body;
                montageParams = new HashMap<>();
                for (Map.Entry<Object, Object> entry : mapInstanceof.entrySet()) {
                    montageParams.put(entry.getKey().toString(), entry.getValue());
                }
            } else if (body instanceof String) {
                //Consider JSON format for string format and obtain the
                // specified map parameters from JSON conversion.
                //When it is not in JSON format, return null.
                montageParams = JSONUtil.getInnerMapByJsonStr((String) body);
            } else {
                //Considering in the form of a single object, using JSON conversion
                // to convert this object to map format may result in JSON conversion
                // errors if the object is not familiar with defining it.
                try {
                    montageParams = JSONUtil.parseObject(JSONUtil.toJSONString(body));
                } catch (Exception e) {
                    //Capture possible conversion errors, such as String type.
                    throw new IllegalArgumentException
                            ("The splicing URL requirement for the body parameter has been set, " +
                                    "but the [" + body.getClass().getName() + "] type of the body does not match");
                }
            }
        }
        return montageParams;
    }

    private static Object getActualResponseTypeArguments(Type type, ClassLoader classLoader) {
        Type[] actualTypeArguments = ((ParameterizedType) type).getActualTypeArguments();
        // as Response and retrieve the first one.
        return Arrays.stream(actualTypeArguments).filter(argType -> {
                    Class<?> typeClass = getTypeClass(argType, classLoader);
                    return typeClass != null && Response.class.isAssignableFrom(typeClass);
                })
                .findFirst()
                .orElse(null);
    }

    private static Class<?> getTypeClass(Type type, ClassLoader classLoader) {
        String typeName = type.getTypeName();
        String className = typeName;
        //A generic type name that carries<...>, After removing such content,
        // the original name of the class is obtained.
        if (typeName.contains(LEFT_ANGLE_BRACKET) && typeName.contains(RIGHT_ANGLE_BRACKET)) {
            className = typeName.split(LEFT_ANGLE_BRACKET)[0];
        }
        Class<?> classObj;
        try {
            classObj = Class.forName(className, false, classLoader);
        } catch (ClassNotFoundException e) {
            classObj = null;
        }
        return classObj;
    }

    public static Object[] toLoggerArray(Object... args) {
        return args;
    }
}
