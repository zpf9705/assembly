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

import com.google.common.annotations.Beta;
import com.palominolabs.http.url.UrlBuilder;
import top.osjf.sdk.core.lang.NotNull;
import top.osjf.sdk.core.lang.Nullable;
import top.osjf.sdk.core.support.SdkSupport;
import top.osjf.sdk.core.util.JSONUtil;
import top.osjf.sdk.core.util.StringUtils;
import top.osjf.sdk.http.util.UrlUtils;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
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
@SuppressWarnings({"unchecked"})
public abstract class HttpSdkSupport extends SdkSupport {

    /*** Cache for context type determination.
     * @since 1.0.2
     * */
    protected static final Map<BiPredicate<String, Charset>, String> content_type_predicates;

    /*** parse xml.
     * @since 1.0.2
     * */
    protected static DocumentBuilder builder;

    public static final String CONTENT_TYPE_NAME = "Content-Type";

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
        content_type_predicates.put(HttpSdkSupport::isXMLBody, "application/xml");
        content_type_predicates.put((s, c) -> isFromBody(s), "application/x-www-form-urlencoded");
    }

    /**
     * Retrieve {@link #CONTENT_TYPE_NAME} based on the content of the request body,
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
     * Return whether the input request body is a parsed XML.
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
    public static boolean isXMLBody(@NotNull String body, @Nullable Charset charset) {
        if (builder != null) {
            try {
                builder.parse
                        (new ByteArrayInputStream(charset != null ? body.getBytes(charset) : body.getBytes()));
            } catch (Exception e) {
                return false;
            }
            return true;
        }
        return body.matches("^<.*$");
    }

    /**
     * Return whether the input request body is a from.
     *
     * <p>This method checks that the current input request
     * body satisfies at least one key/value combination after
     * being split by the symbol {@code &}.
     *
     * <p>Can meet the following criteria for judgment:
     * <pre>
     *     {@code
     *     String example = "ack=1"; //true
     *     String example1 = "ack=1&acr=2"; //true
     *     String example2 = "=3&"; //false
     *     String example3 = "ack=1&*314"; //true
     *     String example4 = "&==ack1"; //false
     *     }
     * </pre>
     *
     * @param body input body.
     * @return if {@code true} input body is from,otherwise not.
     * @throws NullPointerException if input body is {@literal null}.
     * @since 1.0.2
     */
    @Beta
    public static boolean isFromBody(@NotNull String body) {
        String pair0 = body.split("&")[0];
        return pair0.contains("=") && !pair0.substring(0, pair0.indexOf("=")).isEmpty();
    }

    /**
     * Appends the character set to the given content type if a character set is available.
     *
     * @param contentType the original content type header value
     * @param charset     the character set to append
     * @return the content type header value with the character set appended, or the original
     * value if no character set is available
     * @throws NullPointerException if input contentType is {@literal null}.
     * @since 1.0.3
     */
    public static String appendCharsetToContentType(String contentType, @Nullable Charset charset) {
        if (charset != null) {
            return contentType + ";charset=" + charset;
        }
        return contentType;
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
    public static Map<String, Object> convertUrlQueryParamToMap(Object urlQueryParm) {
        if (urlQueryParm == null) return null;
        Map<String, Object> queryParamMap;
        try {
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
                queryParamMap = JSONUtil.parseObject(JSONUtil.toJSONString(urlQueryParm));
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("convert url query Param filed ", e);
        }
        if (queryParamMap == null)
            throw new IllegalArgumentException("Failed to convert URL query parameters. " +
                    "Please set the parameters to 'Map', 'JSON string', or entity object.");
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
     * @throws IllegalArgumentException If an error occurs during URL building or convert url query param
     *                                  this exception is thrown with detailed error information.
     * @throws NullPointerException     if input url is {@literal null}.
     * @since 1.0.2
     */
    public static String buildAndFormatUrlWithQueryParams(@NotNull String url, Object queryParam, Charset charset) {
        Map<String, Object> queryParamMap = convertUrlQueryParamToMap(queryParam);
        try {
            UrlBuilder urlBuilder = UrlUtils.toPalominolabsBuilder(url, charset);
            for (Map.Entry<String, Object> entry : queryParamMap.entrySet()) {
                urlBuilder.queryParam(entry.getKey(), String.valueOf(entry.getValue()));
            }
            url = urlBuilder.toUrlString();
        } catch (Exception e) {
            throw new IllegalArgumentException(" Format URL error ", e);
        }
        return url;
    }
}
