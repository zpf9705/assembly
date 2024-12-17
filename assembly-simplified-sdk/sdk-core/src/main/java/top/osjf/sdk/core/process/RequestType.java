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

import java.lang.annotation.*;

/**
 * Used to mark the {@link Request} parameter type that needs to be
 * encapsulated by the current request input parameter during the
 * execution of the SDK method using annotations.
 *
 * <p>On the basis of {@link RequestTypeSupplier},supporting multiple
 * parameters, first find the construction method based on the order
 * of parameters, and if there is no construction method, find the
 * set method.Of course, these need to be used in conjunction with
 * annotations {@link RequestField}.
 *
 * <p>The parsing scheme for implementing parameter diversification
 * processing by combining two annotations can be customized. For
 * the use of SDK proxies to parse the above annotations, you can
 * refer to method {@link SdkSupport#invokeCreateRequest}
 * and the following code examples.
 * <p>
 * Using its <a href="https://mvnrepository.com/artifact/top.osjf.sdk/sdk-http">sdk-http</a>
 * implementation example code:
 * <pre>
 *  public class ExampleHttpRequestParams
 *  extends AbstractHttpRequest&lt;HttpResultResponse&lt;List&lt;Example&gt;&gt;&gt; {
 *
 *      &#064;RequestField("queryDto")
 *      private QueryDto queryDto;
 *      &#064;RequestField("token")
 *      private String token;
 *
 *      public RequestImpl(QueryDto queryDto,String token){
 *          this.queryDto = queryDto;
 *          this.token = token;
 *      }
 *      public void setQueryDto(QueryDto queryDto){
 *          this.queryDto = queryDto;
 *      }
 *      public void setToken(String token){
 *          this.token = token;
 *      }
 *
 *      &#064;Override
 *      public HttpSdkEnum matchHttpSdk() {
 *          return new HttpSdkEnum() {
 *      &#064;Override
 *      public HttpRequestMethod getRequestMethod() {
 *          return HttpRequestMethod.POST;
 *      }
 *      &#064;Override
 *      public String getUlr(String host) {
 *          return "https:// + host +"example/query.json";
 *      }
 *      &#064;Override
 *      public String name() {
 *         return "EXAMPLE";
 *     }
 *      };
 *    }
 *  }
 *
 *    &#064;Sdk(hostProperty = "${your.host}")
 *    public interface Service {
 *        &#064;RequestType(ExampleHttpRequestParams.class)
 *        HttpResultResponse&lt;List&lt;Example&gt;&gt; queryData(QueryDto dto,String token);
 *    }
 * </pre>
 * <p>
 * Starting from version 1.0.2, this annotation will be used to mark {@link Request}
 * types, and when searching for {@link Request} types, its priority level will be
 * higher than {@link RequestTypeSupplier}.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @see SdkSupport#invokeCreateRequest
 * @see RequestTypeSupplier
 * @since 1.0.0
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestType {

    /**
     * Return the type of the request parameter.
     *
     * @return type of the request parameter.
     */
    @SuppressWarnings("rawtypes")
    Class<? extends Request> value();
}
