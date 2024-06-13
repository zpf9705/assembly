package top.osjf.assembly.simplified.sdk.process;

import java.lang.annotation.*;

/**
 * Annotations for concise request parameters can be carried on proxy methods
 * to avoid directly encapsulating parameters using subclasses of {@link Request},
 * and instead focus directly on the body of business parameters.
 *
 * <p>The specific usage method is as follows.
 * <div><h3>Supporting multiple parameters, first find the construction method
 * based on the order of parameters, and if there is no construction method,
 * find the set method.</h3></div>
 * <pre>
 *
 *  public class RequestImpl extends AbstractHttpRequestParams&lt;HttpResultResponse&lt;List&lt;Supplier&gt;&gt;&gt; {
 *
 *      private QueryDto queryDto;
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
