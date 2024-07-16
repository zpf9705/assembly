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

package top.osjf.sdk.core.process;

import top.osjf.sdk.core.support.SdkSupport;

import java.lang.reflect.Method;

/**
 * The functional definition of this interface is applicable to knowing
 * the type of request encapsulation class when calling the SDK function
 * with a single parameter.
 *
 * <p>The analysis case can be viewed in {@link SdkSupport#invokeCreateRequest}.
 *
 * <div><h3>Examples of usage methods are as follows</h3></div>
 * <pre>
 *  public class QueryDto implements RequestParameter {
 *
 *      private String token;
 *
 *     &#064;Override
 *     public Object getRequestType(){
 *         return RequestImpl.class;
 *     }
 *  }
 * </pre>
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @see SdkSupport#invokeCreateRequest
 * @see RequestParam
 * @since 1.0.0
 */
public interface RequestParameter {

    /**
     * Return the type of the request parameter.
     *
     * @return type of the request parameter.
     */
    @SuppressWarnings("rawtypes")
    Class<? extends Request> getRequestType();
}
