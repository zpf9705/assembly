package top.osjf.assembly.sdk.http;


import com.alibaba.fastjson.JSON;
import org.springframework.lang.Nullable;
import top.osjf.assembly.sdk.client.Client;
import top.osjf.assembly.sdk.process.AbstractRequestParams;

/**
 * Http request abstract node class, used to define the public parameters or methods of the real request parameter class.
 * <p>
 * You can check the example code:
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
 * @author zpf
 * @since 1.1.1
 */
public abstract class AbstractHttpRequestParams<R extends AbstractHttpResponse> extends AbstractRequestParams<R>
        implements HttpRequest<R> {

    @Override
    public Object getRequestParam() {
        Object param = getParam();
        if (param == null) {
            return null;
        }
        if (defaultToJson()) {
            param = JSON.toJSONString(param);
        }
        return param;
    }

    @Override
    @SuppressWarnings("rawtypes")
    public Class<? extends Client> getClientCls() {
        return DefaultHttpClient.class;
    }

    /**
     * The abstract method for obtaining the actual request body. If {@link #defaultToJson()} requires
     * JSON serialization, it will be done in {@link #getRequestParam()}. If it is not a JSON request
     * parameter, this method needs to be rewritten to directly convert the input parameter format and
     * set {@link #defaultToJson()} to {@literal true}.
     *
     * @return Returns an input parameter object, which may have multiple forms of existence or may be {@literal null}.
     */
    @Nullable
    public abstract Object getParam();

    /**
     * Mark whether to use input parameters as JSON serial numbers.
     *
     * @return True is required, while false is not required.
     */
    public boolean defaultToJson() {
        return true;
    }
}
