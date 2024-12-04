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

package top.osjf.sdk.http.support;

import com.palominolabs.http.url.UrlBuilder;
import top.osjf.sdk.core.process.Request;
import top.osjf.sdk.core.process.Response;
import top.osjf.sdk.core.support.SdkSupport;
import top.osjf.sdk.core.util.*;
import top.osjf.sdk.http.process.HttpRequest;
import top.osjf.sdk.http.util.UrlUtils;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;

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
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
@SuppressWarnings({"rawtypes", "unchecked"})
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

    /*** Cache for context type determination.
     * @since 1.0.2
     * */
    protected static final Map<BiPredicate<String, Charset>, String> content_type_predicates;

    /*** parse xml.
     * @since 1.0.2
     * */
    protected static DocumentBuilder builder;

    static {
        /* init documentBuilder cache */
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException ignored) {
        }
        /* init content type predicates cache */
        content_type_predicates = new LinkedHashMap<>();
        content_type_predicates.put((s, c) -> JSONUtil.isValidObjectOrArray(s), "application/json");
        content_type_predicates.put((s, c) ->
                        s.matches("^(?:[a-zA-Z0-9_\\-.]+=(?:[^&]*?)(?:&(?:[a-zA-Z0-9_\\-.]+=(?:[^&]*?)))*)?$"),
                "application/x-www-form-urlencoded");
        content_type_predicates.put(HttpSdkSupport::isXMLBody, "application/xml");
    }

    /**
     * Retrieve {@code "Content-type"} based on the content of the request body,
     * supporting the judgment of following types:
     * <ul>
     *     <li>{@code "application/json"}</li>
     *     <li>{@code "application/xml"}</li>
     *     <li>{@code "application/x-www-form-urlencoded"}</li>
     * </ul>
     * with the rest being {@literal null}.
     *
     * @param body    input body.
     * @param charset input charset.
     * @return {@code "Content-type"} or nullable if is no support type.
     * @since 1.0.2
     */
    public static String getContentTypeWithBody(Object body, Charset charset) {
        if (body == null || StringUtils.isBlank(body.toString())) return null;
        String bodyStr = body.toString();
        for (Map.Entry<BiPredicate<String, Charset>, String> entry : content_type_predicates.entrySet()) {
            if (entry.getKey().test(bodyStr, charset)) {
                return entry.getValue();
            }
        }
        return null;
    }

    /**
     * Return whether the input request message is a parsed XML.
     *
     * <p>Option 1 uses {@code DocumentBuilder} for XML formatting
     * and parsing, and if there are no exceptions, it can be determined
     * as an XML message.
     *
     * <p>Option 2 uses regular {@code ^<.*$} to determine if it starts
     * with {@code <}.
     *
     * @param body    input body.
     * @param charset input charset.
     * @return if {@code true} input body is xml,otherwise not.
     * @since 1.0.2
     */
    public static boolean isXMLBody(String body, Charset charset) {
        if (builder != null) {
            try {
                builder.parse(new ByteArrayInputStream(body.getBytes(charset)));
            } catch (Exception e) {
                return false;
            }
            return true;
        }
        return body.matches("^<.*$");
    }

    /**
     * Get the unique client URL (excluding query parameters).
     * <p>
     * This method takes a URL string as input. If the input URL is blank or does
     * not contain query parameters (i.e., the "?" character),it directly returns
     * the original URL. If the URL contains query parameters,it returns the part
     * of the URL before the "?",excluding the query parameters.
     *
     * @param url The input URL string
     * @return The URL string excluding query parameters
     */
    public static String getClientUnique(String url) {
        return StringUtils.isBlank(url) || !url.contains("?") ? url : url.substring(0, url.indexOf("?"));
    }

    /**
     * Return the {@code request body } and {@code URL access} address when parameters
     * are needed as URL concatenation and processed normally.
     * <p>
     * This method does not return a value and consumes the result through a custom
     * function {@link BiConsumer}.
     *
     * @param request        An object encapsulating HTTP request information.
     * @param url            The URL address of the request.
     * @param resultConsumer The consumption function for the final body and URL.
     * @throws Exception Format and encoding set related errors that
     *                   occur during the URL formatting process.
     */
    public static void resolveIfMontageUrlAndBody(HttpRequest request, String url,
                                                  BiConsumer<Object, String> resultConsumer) throws Exception {
        Object body;
        String realUrl;
        if (request.montage()) {
            //When URL parameter concatenation is required,
            // it is handled on a case by case basis here.
            Object montageObj;
            //Obtain segmentation parameters for special interfaces.
            if (request instanceof HttpRequest.MontageParam) {
                montageObj = ((HttpRequest.MontageParam) request).getParam();
                if (montageObj == null) {
                    //The segmentation parameter obtained by the special
                    // interface is null, and the body parameter is still used.
                    montageObj = request.getRequestParam();
                    body = null; //When the request parameter is a concatenation parameter,
                    // the body parameter is null.
                    //On the contrary, the body is normally set as a request parameter.
                } else body = request.getRequestParam();
            } else {
                //If no special interface is implemented,
                // simply set the request parameters as URL segmentation parameters.
                montageObj = request.getRequestParam();
                body = null;
            }
            //Format the URL for the special needs of splitting parameters.
            realUrl = HttpSdkSupport.formatMontageTrueUrl(url, montageObj, request.getCharset());
        } else {
            //Under normal circumstances, just set the parameters one by one.
            body = request.getRequestParam();
            realUrl = url;
        }
        resultConsumer.accept(body, realUrl);
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
                return request.isWrapperFor(typeClass);
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
     * @param montageObj The object to be converted. It can be of type {@code Map}, {@code String}, or other types.
     * @return The converted {@code Map<String, Object>} object. If no conversion is needed or the conversion fails,
     * {@literal null} is returned.
     * @throws IllegalArgumentException Thrown if the {@code body} type cannot be correctly converted to Map format.
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> resolveMontageObj(Object montageObj) {
        Map<String, Object> montageParams = null;
        if (montageObj != null) {
            if (montageObj instanceof Map) {
                //When obtaining the body type in map format,
                // it can be directly converted.
                Map<Object, Object> mapInstanceof = (Map<Object, Object>) montageObj;
                montageParams = new HashMap<>();
                for (Map.Entry<Object, Object> entry : mapInstanceof.entrySet()) {
                    montageParams.put(entry.getKey().toString(), entry.getValue());
                }
            } else if (montageObj instanceof String) {
                //Consider JSON format for string format and obtain the
                // specified map parameters from JSON conversion.
                //When it is not in JSON format, return null.
                montageParams = JSONUtil.getInnerMapByJsonStr((String) montageObj);
            } else {
                //Considering in the form of a single object, using JSON conversion
                // to convert this object to map format may result in JSON conversion
                // errors if the object is not familiar with defining it.
                try {
                    montageParams = JSONUtil.parseObject(JSONUtil.toJSONString(montageObj));
                } catch (Exception e) {
                    //Capture possible conversion errors, such as String type.
                    throw new IllegalArgumentException
                            ("The splicing URL requirement for the body parameter has been set, " +
                                    "but the [" + montageObj.getClass().getName() + "] type of the body does not match");
                }
            }
        }
        return montageParams;
    }

    /**
     * Use method {@link #resolveMontageObj} to analyze and obtain relevant additional URL query parameters,
     * and rely on {@code hutool}'s {@link UrlBuilder} to concatenate the parameters.
     *
     * @param montageObj The object to be converted. It can be of type {@code Map}, {@code String}, or other types.
     * @param charset    Encoding character set format.
     * @param url        Access addresses that require specific formatting.
     * @return Specific formatted access address.
     * @throws CharacterCodingException if decoding percent-encoded bytes fails and charsetDecoder is configured to
     *                                  report errors.
     * @throws MalformedURLException    if no protocol is specified, or an
     *                                  unknown protocol is found, or {@code spec} is {@code null}.
     */
    public static String formatMontageTrueUrl(String url, Object montageObj, Charset charset) throws
            CharacterCodingException, MalformedURLException {
        Map<String, Object> montageParams = resolveMontageObj(montageObj);
        if (MapUtils.isNotEmpty(montageParams)) {
            UrlBuilder urlBuilder = UrlUtils.toPalominolabsBuilder(url, charset);
            for (Map.Entry<String, Object> entry : montageParams.entrySet()) {
                urlBuilder.queryParam(entry.getKey(), String.valueOf(entry.getValue()));
            }
            url = urlBuilder.toUrlString();
        }
        return url;
    }

    /**
     * Converts an object to a Map format suitable for URL concatenation.
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
     * @param urlQueryParm The object to be converted. It can be of type {@code Map}, {@code String}, or other types.
     * @return The converted {@code Map<String, Object>} object. If no conversion is needed or the conversion fails,
     * {@literal null} is returned.
     * @throws IllegalArgumentException Thrown if the {@code body} type cannot be correctly converted to Map format.
     * @since 1.0.2
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> convertUrlQueryParamToMap(Object urlQueryParm) {
        Map<String, Object> queryParamMap = null;
        if (urlQueryParm != null) {
            if (urlQueryParm instanceof Map) {
                //When obtaining the body type in map format,
                // it can be directly converted.
                Map<Object, Object> mapInstanceof = (Map<Object, Object>) urlQueryParm;
                queryParamMap = new HashMap<>();
                for (Map.Entry<Object, Object> entry : mapInstanceof.entrySet()) {
                    queryParamMap.put(entry.getKey().toString(), entry.getValue());
                }
            } else if (urlQueryParm instanceof String) {
                //Consider JSON format for string format and obtain the
                // specified map parameters from JSON conversion.
                //When it is not in JSON format, return null.
                queryParamMap = JSONUtil.getInnerMapByJsonStr((String) urlQueryParm);
            } else {
                //Considering in the form of a single object, using JSON conversion
                // to convert this object to map format may result in JSON conversion
                // errors if the object is not familiar with defining it.
                try {
                    queryParamMap = JSONUtil.parseObject(JSONUtil.toJSONString(urlQueryParm));
                } catch (Exception e) {
                    //Capture possible conversion errors, such as String type.
                    throw new IllegalArgumentException
                            ("The splicing URL requirement for the body parameter has been set, " +
                                    "but the [" + urlQueryParm.getClass().getName() + "] type of the body does not match");
                }
            }
        }
        return queryParamMap;
    }

    /**
     * Builds and formats a complete URL with query parameters based on the provided URL and query parameters.
     *
     * <p>This method first resolves the query parameter object into a Map, then iterates over this Map,
     * adding each key-value pair as a query parameter to the URL. If the query parameter Map is empty,
     * it returns the original URL directly.
     *
     * @param url        The original URL string, excluding the query parameter part.
     * @param queryParam The object containing query parameters, which can be a Map, JavaBean, etc.,
     *                   depending on the implementation of the `resolveMontageObj` method.
     * @param charset    The character set used for URL encoding.
     * @return The built and formatted complete URL string, including query parameters.
     * @throws IllegalArgumentException If an error occurs during URL building,
     *                                  this exception is thrown with detailed error information.
     * @since 1.0.2
     */
    public static String buildAndFormatUrlWithQueryParams(String url, Object queryParam, Charset charset) {
        Map<String, Object> queryParamMap = resolveMontageObj(queryParam);
        if (MapUtils.isNotEmpty(queryParamMap)) {
            try {
                UrlBuilder urlBuilder = UrlUtils.toPalominolabsBuilder(url, charset);
                for (Map.Entry<String, Object> entry : queryParamMap.entrySet()) {
                    urlBuilder.queryParam(entry.getKey(), String.valueOf(entry.getValue()));
                }
                url = urlBuilder.toUrlString();
            } catch (Exception e) {
                throw new IllegalArgumentException(" Format URL error ", e);
            }
        }
        return url;
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
