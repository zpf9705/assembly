package top.osjf.assembly.simplified.sdk.process;

import java.lang.reflect.Method;

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
 * method {@link top.osjf.assembly.simplified.sdk.SdkUtils#getResponse(Method, Response)}.
 *
 * <div><h3>Examples of usage methods are as follows</h3></div>
 * <pre>
 *  public class ResponseImpl extends AbstractHttpResponse implements ResponseData {
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
 * </pre>
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 2.2.6
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
