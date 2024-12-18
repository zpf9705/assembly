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

package top.osjf.sdk.core;

import top.osjf.sdk.core.support.SdkSupport;

/**
 * The functional interface used to check the success of this
 * request and obtain the return data of the request is defined
 * in order for the caller to pay less attention to distinguishing
 * {@link Response#isSuccess()} related situations, and can directly
 * call a method to achieve request success check and data acquisition.
 * Of course, this situation can be configured.
 *
 * <p>Of course, regarding the SDK proxy result analysis, an implementation
 * of the interface definition concept has been added. You can focus on
 * method {@link SdkSupport#getResponse}.
 *
 * <div><h3>Examples of usage methods are as follows</h3></div>
 * <pre>
 *  public class ExampleResponseData extends AbstractHttpResponse implements ResponseData {
 *
 *      private String token;
 *
 *     &#064;Override
 *     public boolean inspectionResponseResult(){
 *         return true;
 *     }
 *
 *     &#064;Override
 *     public Object getData(){
 *         return token;
 *     }
 *  }
 *
 *  // When interface ResponseData is not implemented:
 *  interface ExampleInterface {
 *      ExampleResponseData getToken(ExampleRequest request);
 *  }
 *
 *  //When interface ResponseData is implemented:
 *  interface ExampleInterface {
 *      String getToken(ExampleRequest request);
 *  }
 * </pre>
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
public interface ResponseData extends Response {

    /**
     * Whether to check the success of the response after the
     * request is completed.
     *
     * @return if {@code true} inspection to response result
     * {@link #isSuccess()},otherwise.
     */
    boolean inspectionResponseResult();

    /**
     * Return Define the corresponding return data object.
     *
     * @return Define the corresponding return data object.
     */
    Object getData();
}
