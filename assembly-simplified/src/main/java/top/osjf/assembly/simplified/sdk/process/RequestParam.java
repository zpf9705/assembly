package top.osjf.assembly.simplified.sdk.process;

import top.osjf.assembly.simplified.sdk.SdkUtils;

import java.lang.annotation.*;
import java.lang.reflect.Method;

/**
 * Used to mark the {@link Request} parameter type that needs to be
 * encapsulated by the current request input parameter during the
 * execution of the SDK method using annotations.
 *
 * <p>On the basis of {@link RequestParameter},supporting multiple
 * parameters, first find the construction method based on the order
 * of parameters, and if there is no construction method, find the
 * set method.Of course, these need to be used in conjunction with
 * annotations {@link RequestField}.
 *
 * <p>The parsing scheme for implementing parameter diversification
 * processing by combining two annotations can be customized. For
 * the use of SDK proxies to parse the above annotations, you can
 * refer to method {@link SdkUtils#invokeCreateRequest(Method, Object[])}
 * and the following code examples.
 *
 * <pre>
 *
 *  public class RequestImpl extends AbstractHttpRequestParams&lt;HttpResultResponse&lt;List&lt;Supplier&gt;&gt;&gt; {
 *
 *      &#064;RequestField("queryDto")
 *      private QueryDto queryDto;
 *
 *      &#064;RequestField("token")
 *      private String token;
 *
 *      public RequestImpl(QueryDto queryDto,String token){
 *          this.queryDto = queryDto;
 *          this.token = token;
 *      }
 *
 *      public void setQueryDto(QueryDto queryDto){
 *          this.queryDto = queryDto;
 *      }
 *
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
 *          return "your host";
 *      }
 *      &#064;Override
 *      public String name() {
 *         return "your api name";
 *     }
 *      };
 *    }
 *  }
 *
 *      &#064;Sdk(hostProperty = "${your.host}")
 *      public interface Service {
 *
 *          &#064;RequestParam(RequestImpl.class)
 *          HttpResultResponse&lt;List&lt;Data&gt;&gt; queryData(QueryDto dto,String token);
 *      }
 * </pre>
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @see top.osjf.assembly.simplified.sdk.SdkUtils#invokeCreateRequest(Method, Object[])
 * @see RequestParameter
 * @since 2.2.6
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestParam {

    /**
     * Return the type of the request parameter.
     *
     * @return type of the request parameter.
     */
    @SuppressWarnings("rawtypes")
    Class<? extends Request> value();
}
