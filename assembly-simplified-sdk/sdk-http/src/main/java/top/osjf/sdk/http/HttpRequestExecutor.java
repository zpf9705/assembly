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

import feign.Client;
import feign.Request;
import feign.Response;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 * The HTTP request executor interface defines various HTTP request methods and provides a
 * unified request execution method.
 * <p>
 * This interface allows mapping lowercase HTTP method names to specific methods of the
 * current class through a unified {@link #unifiedDoRequest} method,And execute the corresponding
 * HTTP request. In addition, GET, POST, PUT, DELETE, TRACE,OPTIONS, HEAD, PATCH, etc. are
 * directly defined Specific HTTP request methods.
 * <p>
 * In each request method, it is allowed to specify the request address (URL), request headers,
 * request parameters (params), and Whether to concatenate the parameters in Map or Json format
 * to the URL and use them as a key value pair (month).
 * <p>
 * All request methods return a return value of type String and may throw exceptions.
 *
 *
 * <p>
 * The HttpRequestExecutor interface inherits from the feature client interface since version 1.0.2,
 * Adopting open label technology support for executing HTTP requests.
 * <p>
 * This interface defines an 'execute' method for sending HTTP requests and receiving responses.
 * The method takes a request object and a request option object as parameters and returns a response object.
 * If an I/O exception occurs during the request process, an IOException will be thrown.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
public interface HttpRequestExecutor extends Client {
    /**
     * {@inheritDoc}
     *
     * @param request {@inheritDoc}
     * @param options {@inheritDoc}
     * @return {@inheritDoc}
     * @throws IOException {@inheritDoc}
     * @since 1.0.2
     */
    @Override
    Response execute(Request request, Request.Options options) throws IOException;

    /**
     * Use lowercase HTTP method names to map the request method names of
     * the current class and make unified method calls.
     *
     * @param methodName Http method name map to this class method name.
     * @param url        The actual request address,must not be {@literal null}.
     * @param headers    Header information map,can be {@literal null}.
     * @param param      Request parameters with type {@link Object},can be {@literal null}.
     * @param montage    Whether to concatenate parameters in the form of {@link Map} or {@code Json}
     *                   as key/value after the URL.
     * @return The {@code String} type of the return value
     * @throws Exception unknown exception.
     */
    @Deprecated
    default String unifiedDoRequest(String methodName,
                                    String url,
                                    Map<String, String> headers, Object param, boolean montage)
            throws Exception {
        try {
            return getClass().getMethod(methodName,
                    String.class,
                    Map.class,
                    Object.class,
                    boolean.class).invoke(this, url, headers, param, montage).toString();
        } catch (InvocationTargetException e) {
            //If it is an execution exception of InvocationTargetException,
            // convert it to a reflection exception of the parent class and
            // put the target exception in the cause.
            throw new ReflectiveOperationException(e.getTargetException());
        }
    }

    /**
     * HTTP request method for {@code Get}.
     *
     * @param url     The actual request address,must not be {@literal null}.
     * @param headers Header information map,can be {@literal null}.
     * @param param   Request parameters with type {@link Object},can be {@literal null}.
     * @param montage Whether to concatenate parameters in the form of {@link Map} or {@code Json}
     *                as key/value after the URL.
     * @return The {@code String} type of the return value
     * @throws Exception unknown exception.
     */
    @Deprecated
    String get(String url, Map<String, String> headers, Object param, boolean montage) throws Exception;

    /**
     * HTTP request method for {@code Post}.
     *
     * @param url     The actual request address,must not be {@literal null}.
     * @param headers Header information map,can be {@literal null}.
     * @param param   Request parameters with type {@link Object},can be {@literal null}.
     * @param montage Whether to concatenate parameters in the form of {@link Map} or {@code Json}
     *                as key/value after the URL.
     * @return The {@code String} type of the return value
     * @throws Exception unknown exception.
     */
    @Deprecated
    String post(String url, Map<String, String> headers, Object param, boolean montage) throws Exception;

    /**
     * HTTP request method for {@code Put}.
     *
     * @param url     The actual request address,must not be {@literal null}.
     * @param headers Header information map,can be {@literal null}.
     * @param param   Request parameters with type {@link Object},can be {@literal null}.
     * @param montage Whether to concatenate parameters in the form of {@link Map} or {@code Json}
     *                as key/value after the URL.
     * @return The {@code String} type of the return value
     * @throws Exception unknown exception.
     */
    @Deprecated
    String put(String url, Map<String, String> headers, Object param, boolean montage) throws Exception;

    /**
     * HTTP request method for {@code Delete}.
     *
     * @param url     The actual request address,must not be {@literal null}.
     * @param headers Header information map,can be {@literal null}.
     * @param param   Request parameters with type {@link Object},can be {@literal null}.
     * @param montage Whether to concatenate parameters in the form of {@link Map} or {@code Json}
     *                as key/value after the URL.
     * @return The {@code String} type of the return value
     * @throws Exception unknown exception.
     */
    @Deprecated
    String delete(String url, Map<String, String> headers, Object param, boolean montage) throws Exception;

    /**
     * HTTP request method for {@code trace}.
     *
     * @param url     The actual request address,must not be {@literal null}.
     * @param headers Header information map,can be {@literal null}.
     * @param param   Request parameters with type {@link Object},can be {@literal null}.
     * @param montage Whether to concatenate parameters in the form of {@link Map} or {@code Json}
     *                as key/value after the URL.
     * @return The {@code String} type of the return value
     * @throws Exception unknown exception.
     */
    @Deprecated
    String trace(String url, Map<String, String> headers, Object param, boolean montage) throws Exception;

    /**
     * HTTP request method for {@code options}.
     *
     * @param url     The actual request address,must not be {@literal null}.
     * @param headers Header information map,can be {@literal null}.
     * @param param   Request parameters with type {@link Object},can be {@literal null}.
     * @param montage Whether to concatenate parameters in the form of {@link Map} or {@code Json}
     *                as key/value after the URL.
     * @return The {@code String} type of the return value
     * @throws Exception unknown exception.
     */
    @Deprecated
    String options(String url, Map<String, String> headers, Object param, boolean montage) throws Exception;

    /**
     * HTTP request method for {@code head}.
     *
     * @param url     The actual request address,must not be {@literal null}.
     * @param headers Header information map,can be {@literal null}.
     * @param param   Request parameters with type {@link Object},can be {@literal null}.
     * @param montage Whether to concatenate parameters in the form of {@link Map} or {@code Json}
     *                as key/value after the URL.
     * @return The {@code String} type of the return value
     * @throws Exception unknown exception.
     */
    @Deprecated
    String head(String url, Map<String, String> headers, Object param, boolean montage) throws Exception;

    /**
     * HTTP request method for {@code patch}.
     *
     * @param url     The actual request address,must not be {@literal null}.
     * @param headers Header information map,can be {@literal null}.
     * @param param   Request parameters with type {@link Object},can be {@literal null}.
     * @param montage Whether to concatenate parameters in the form of {@link Map} or {@code Json}
     *                as key/value after the URL.
     * @return The {@code String} type of the return value
     * @throws Exception unknown exception.
     */
    @Deprecated
    String patch(String url, Map<String, String> headers, Object param, boolean montage) throws Exception;
}
