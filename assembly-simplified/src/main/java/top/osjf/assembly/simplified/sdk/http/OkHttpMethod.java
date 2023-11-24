package top.osjf.assembly.simplified.sdk.http;

import top.osjf.assembly.util.http.OkHttpSimpleRequestUtils;

import java.util.Map;

/**
 * One of the implementation classes of {@link HttpMethod}, please
 * refer to {@link OkHttpSimpleRequestUtils} for implementation.
 *
 * @author zpf
 * @since 1.1.1
 */
public class OkHttpMethod implements HttpMethod {

    public static final HttpMethod INSTANCE = new OkHttpMethod();

    private OkHttpMethod() {
    }

    @Override
    public String get(String url, Map<String, String> headers, Object requestParam, boolean montage) throws Exception {
        return OkHttpSimpleRequestUtils.get(url, headers, requestParam, montage);
    }

    @Override
    public String post(String url, Map<String, String> headers, Object requestParam, boolean montage) throws Exception {
        return OkHttpSimpleRequestUtils.post(url, headers, requestParam, montage);
    }

    @Override
    public String put(String url, Map<String, String> headers, Object requestParam, boolean montage) throws Exception {
        return OkHttpSimpleRequestUtils.put(url, headers, requestParam, montage);
    }

    @Override
    public String delete(String url, Map<String, String> headers, Object requestParam, boolean montage) throws Exception {
        return OkHttpSimpleRequestUtils.delete(url, headers, requestParam, montage);
    }
}
