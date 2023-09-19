package top.osjf.assembly.sdk.http;

import java.util.Map;


/**
 * HTTP tool request client class based on Square's open-source products.
 * <p>Please refer to {@link CommonsHttpClient} for the specific request process.</p>
 *
 * @author zpf
 * @since 1.1.1
 */
@SuppressWarnings("serial")
@HttpClient(type = Type.OK_HTTP)
public class OkHttpClient<R extends HttpResponse> extends CommonsHttpClient<R> {

    public OkHttpClient(String url) {
        super(url);
    }

    @Override
    public String doRequest(HttpRequestMethod method, Map<String, String> headers,
                            Object requestParam, Boolean montage) throws Exception {
        super.doRequest(method, headers, requestParam, montage);
        return method.doRequest(getUrl(), headers, requestParam, montage);
    }
}
