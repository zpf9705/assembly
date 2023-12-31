package top.osjf.assembly.simplified.sdk.http;

import top.osjf.assembly.simplified.sdk.client.Client;
import top.osjf.assembly.simplified.sdk.process.AbstractRequestParams;
import top.osjf.assembly.util.annotation.CanNull;
import top.osjf.assembly.util.json.FastJsonUtils;

/**
 * Http request abstract node class, used to define the public parameters
 * or methods of the real request parameter class.
 *
 * <p>You can check the example code:
 * <pre>
 * {@code
 * public class TestP extends AbstractHttpRequestParams<TestR> {
 *
 *     public HttpSdkEnum matchSdk() {
 *         return new HttpSdkEnum() {
 *
 *             public String getUlr(String host) {
 *                 return "";
 *             }
 *
 *             public HttpRequestMethod getHttpRequestMethod() {
 *                 return HttpRequestMethod.POST;
 *             }
 *
 *             public String name() {
 *                 return "null";
 *             }
 *         };
 *     }
 *
 *     public Object getRequestParam() {
 *         return "";
 *     }
 *
 *     public Class<TestR> getResponseCls() {
 *         return TestR.class;
 *     }
 * }}
 * </pre>
 *
 * @param <R> Implement a unified response class data type.
 * @author zpf
 * @since 1.1.1
 */
@SuppressWarnings("serial")
public abstract class AbstractHttpRequestParams<R extends AbstractHttpResponse> extends AbstractRequestParams<R>
        implements HttpRequest<R> {

    @Override
    @CanNull
    public Object getRequestParam() {
        Object param = getParam();
        if (param == null) {
            return null;
        }
        if (defaultToJson()) {
            param = FastJsonUtils.toJSONString(param);
        }
        return param;
    }

    @Override
    public boolean montage() {
        return false;
    }

    @Override
    @SuppressWarnings("rawtypes")
    public Class<? extends Client> getClientCls() {
        return ApacheHttpClient.class;
    }

    /**
     * The abstract method for obtaining the actual request body.
     * <p>If {@link #defaultToJson()} requires JSON serialization,
     * it will be done in {@link #getRequestParam()}.
     * <p>If it is not a JSON request parameter, this method needs
     * to be rewritten to directly convert the input parameter format
     * and set {@link #defaultToJson()} to {@literal true}.
     *
     * @return Returns an input parameter object, which may have
     * multiple forms of existence or may be {@literal null}.
     */
    @CanNull
    public Object getParam(){
        return null;
    }

    /**
     * Mark whether to use input parameters as JSON serial numbers.
     *
     * @return True is required, while false is not required.
     */
    public boolean defaultToJson() {
        return true;
    }
}
