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


package top.osjf.sdk.http.spi;

import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.Map;

/**
 * {@code HttpResponse} is an interface that describes HTTP request response information,
 * which is not equivalent to the {@link top.osjf.sdk.http.HttpResponse} of the SDK.
 *
 * <p>The data related to this interface is the real information returned by the request server,
 * rather than the developer's custom SDK response class. Of course, the information of this
 * interface will be given to the SDK's custom response class to provide query support.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.2
 */
public interface HttpResponse extends Serializable {

    /**
     * Gets the status code of the HTTP response.
     *
     * <p>The status code is a three-digit integer that indicates the status
     * of the HTTP response. For example, 200 indicates a successful request,
     * and 404 indicates that the resource was not found.
     *
     * @return The status code of the HTTP response.
     */
    int getStatusCode();

    /**
     * Gets the status message of the HTTP response.
     *
     * <p>The status message is a brief text description corresponding to the
     * status code, providing additional information about the response status.
     * For example, "OK" corresponds to the 200 status code.
     *
     * @return The status message of the HTTP response
     */
    String getStatusMessage();

    /**
     * Gets the header information of the HTTP response.
     *
     * <p>The header information is a collection of key-value pairs that contains
     * metadata about the HTTP response.
     * For example, {@code Content-Type}, {@code Content-Length},etc.
     *
     * @return A Map containing the header information of the HTTP response.
     */
    Map<String, Object> getHeadMap();

    /**
     * Gets the {@code Charset} set of the HTTP response.
     *
     * <p>The character set specifies the encoding of the response body content.
     * For example, UTF-8, ISO-8859-1, etc.
     *
     * @return The character set of the HTTP response.
     */
    Charset getCharset();

    /**
     * Gets the body content of the HTTP response.
     *
     * <p>The response body contains the actual response data, such as HTML documents,
     * JSON objects, etc.
     *
     * @return The body content of the HTTP response.
     */
    String getBody();

    /**
     * Return the HTTP protocol version instance object, and return a generalized
     * object according to different docking frameworks, which can be converted
     * as needed.
     * <p>
     * <strong>The following are the object types of the relevant definition
     * protocol versions that support the framework, which can be compared
     * according to the selection.</strong>
     * <ul>
     * <li>Open-feign http <strong>{@code feign.Request.ProtocolVersion}</strong></li>
     * <li>Apache http <strong>{@code org.apache.http.ProtocolVersion}</strong></li>
     * <li>Apache hc5 <strong>{@code org.apache.hc.core5.http.ProtocolVersion}</strong></li>
     * <li>OK http <strong>{@code okhttp3.Protocol}</strong></li>
     * <li>Google http not provided yet.</li>
     * <li>Jaxrs2 http not provided yet.</li>
     * </ul>
     * @return The HTTP protocol version instance.
     */
    ProtocolVersion getProtocolVersion();
}
