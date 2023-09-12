package top.osjf.assembly.sdk.process;


import com.alibaba.fastjson.JSON;
import org.springframework.lang.Nullable;
import top.osjf.assembly.sdk.SdkException;
import top.osjf.assembly.sdk.client.Client;
import top.osjf.assembly.sdk.client.HttpClient;

import java.util.Collections;
import java.util.Map;

/**
 * Request abstract node class, used to define the public parameters or methods of the real request parameter class.
 * <p>
 * You can check the example code:
 * <pre>
 * {@code
 * public class TestP extends AbstractRequestParams<TestR> {
 *
 *     public SdkEnum matchSdk() {
 *         return new SdkEnum() {
 *
 *             public String getUlr(String host) {
 *                 return "";
 *             }
 *
 *             public RequestMethod getRequestMethod() {
 *                 return RequestMethod.POST;
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
 * @since 1.1.0
 */
public abstract class AbstractRequestParams<R extends AbstractResponse> implements Request<R> {

    @Override
    public Map<String, String> getHeadMap() {
        return Collections.emptyMap();
    }

    @Override
    public void validate() throws SdkException {
    }

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
        return HttpClient.class;
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
