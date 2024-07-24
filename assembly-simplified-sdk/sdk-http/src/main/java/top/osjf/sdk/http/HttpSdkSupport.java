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
import top.osjf.sdk.core.util.MapUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Support classes for http SDK.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
public abstract class HttpSdkSupport extends SdkSupport {

    /*** content type name */
    protected static final String named = "Content-Type";

    /*** default Json content type */
    protected static final String default_content_type = "application/json";

    /***The left angle bracket included in the generic.*/
    protected static final String LEFT_ANGLE_BRACKET = "<";

    /***The right angle bracket included in the generic.*/
    protected static final String RIGHT_ANGLE_BRACKET = ">";

    /*** cache for dynamically obtain the type of response class.*/
    protected static final Map<Class<?>, Object> rps_classes = new ConcurrentHashMap<>(16);

    /**
     * Check if the existing request headers contain {@link #named}, and
     * if no default JSON is provided.
     *
     * @param headers existing request headers.
     */
    public static void checkContentType(Map<String, String> headers) {
        if (MapUtils.isEmpty(headers)) {
            return;
        }
        //if no Content-Type
        if (!headers.containsKey(named)) {
            //default to JSON Content-Type
            headers.put(named, default_content_type);
        }
    }

    /**
     * Return the corresponding class generic carried by the request class.
     *
     * @param request Current request.
     * @param def     Default type value.
     * @return Object types.
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
