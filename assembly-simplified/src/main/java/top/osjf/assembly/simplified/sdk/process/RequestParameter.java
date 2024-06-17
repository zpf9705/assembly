package top.osjf.assembly.simplified.sdk.process;

import java.lang.reflect.Method;

/**
 * The functional definition of this interface is applicable to knowing
 * the type of request encapsulation class when calling the SDK function
 * with a single parameter.
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
 * @see top.osjf.assembly.simplified.sdk.SdkUtils#invokeCreateRequest(Method, Object[])
 * @see RequestParam
 * @since 2.2.6
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
