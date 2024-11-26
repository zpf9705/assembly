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

import top.osjf.sdk.core.enums.SdkEnum;

/**
 * <p>HTTP SDK Enumeration Interface, extending from the {@code SdkEnum} interface.
 *
 * <p>This interface defines enumeration types related to HTTP requests,
 * selecting the corresponding request scheme based on enumeration identifiers.
 * Currently, it primarily supports HTTP-type request methods.
 *
 * <p>Each enumeration value that implements this interface should provide
 * a method to obtain its corresponding HTTP request method.
 * This allows the SDK to construct and send corresponding HTTP requests
 * based on different enumeration values when in use.
 *
 * <p>This interface is mainly used to encapsulate enumeration values of
 * HTTP request methods, making it more flexible and clear to specify request
 * methods (such as GET, POST, etc.) when initiating HTTP requests.
 * <p>You can check the example code:
 * <pre>
 * {@code
 * public enum Sdk implements HttpSdkEnum {
 *
 * GET_SUPPLIER("***********", ApiProtocol.HTTP, ApiType.*, RequestMethod.POST),
 * UPDATE_REPORT_BACK("***********", ApiProtocol.HTTP, ApiType.*, RequestMethod.POST),
 *
 * private final String url;
 * private final ApiProtocol apiProtocol;
 * private final ApiType type;
 * private final RequestMethod requestMethod;
 *
 * public String getUlr(String uri){
 *      return String.format(this.url,this.apiProtocol.getPath(),uri,this.type.getType());
 * }
 *
 * public HttpRequestMethod getHttpRequestMethod){
 * return this.requestMethod;
 * }
 * }}
 * </pre>
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
public interface HttpSdkEnum extends SdkEnum {

    /**
     * <p>Select the corresponding request scheme based on this enumeration identifier,
     * currently supporting HTTP-type request methods.
     * <p>This method returns a HttpRequestMethod enumeration value, representing
     * the HTTP request method corresponding to the enumeration identifier.
     *
     * @return Returns a {@code HttpRequestMethod} enumeration value, representing
     * the HTTP request method.
     */
    HttpRequestMethod getRequestMethod();

    /**
     * Gets the currently used HTTP protocol enumeration instance.
     *
     * <p>This method does not accept any parameters and returns a {@link HttpProtocol}
     * enumeration instance representing the currently used HTTP protocol.
     * The return value can be either {@link HttpProtocol#HTTPS} or {@link HttpProtocol#HTTP},
     * depending on the application's configuration or the current network environment.
     * <p>
     * This parameter is optional and depends on your return value in {@link #getUrl}.
     * You can check the default operation of method {@link HttpRequest#formatUrl}.
     *
     * @return The currently used HTTP protocol enumeration instance (HTTPS or HTTP).
     * @since 1.0.2
     */
    HttpProtocol getProtocol();
}
