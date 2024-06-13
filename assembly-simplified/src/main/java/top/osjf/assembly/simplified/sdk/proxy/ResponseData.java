package top.osjf.assembly.simplified.sdk.proxy;

import top.osjf.assembly.simplified.sdk.process.Response;

/**
 * In the case of proxy services, when there is no need to encapsulate
 * body return parameters and only need to return the truly needed object,
 * this interface can be implemented to obtain the true return object on
 * the basis of successful request.
 *
 * <div><h3>Examples of usage methods are as follows</h3></div>
 * <pre>
 *  public class ResponseImpl extends AbstractHttpResponse implements ResponseData {
 *
 *      private String token;
 *
 *      private boolean success;
 *
 *     &#064;Override
 *     public boolean isSuccess(){
 *         return success;
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
