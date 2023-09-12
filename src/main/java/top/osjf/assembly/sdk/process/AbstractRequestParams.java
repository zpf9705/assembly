package top.osjf.assembly.sdk.process;


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
    @SuppressWarnings("rawtypes")
    public Class<? extends Client> getClientCls() {
        return HttpClient.class;
    }
}
