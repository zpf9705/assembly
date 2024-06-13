package top.osjf.assembly.simplified.sdk.process;

/**
 * Only a single input parameter is supported to implement this
 * interface to inform the proxy class of the type of request parameter.
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
