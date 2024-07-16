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
 * HTTP SDK related attribute method definition interface,mainly including URL concatenation .
 *
 * <p>Request scheme selection, and related custom enumeration names .
 *
 * <p>You can check the example code:
 * <pre>
 * {@code
 * public enum Sdk implements HttpSdkEnum {
 *
 * GET_SUPPLIER("***********", ApiProtocol.HTTP, ApiType.*, RequestMethod.POST),
 *
 * UPDATE_REPORT_BACK("***********", ApiProtocol.HTTP, ApiType.*, RequestMethod.POST),
 *
 * UPDATE_SHIPMENT_STATUS("***********", ApiProtocol.HTTP, ApiType.*, RequestMethod.POST),
 *
 * SAVE_OR_REMOVE_ADD_SERVICE("***********", ApiProtocol.HTTP, ApiType.*, RequestMethod.POST),
 *
 * private final String url;
 *
 * private final ApiProtocol apiProtocol;
 *
 * private final ApiType type;
 *
 * private final RequestMethod requestMethod;
 *
 * public String getUlr(String uri){
 * return String.format(this.url,this.apiProtocol.getPath(),uri,this.type.getType());
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
     * currently supporting the type of HTTP</p>
     *
     * @return {@link HttpRequestMethod}.
     */
    HttpRequestMethod getRequestMethod();
}
