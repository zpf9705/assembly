package top.osjf.assembly.simplified.sdk.http;

import top.osjf.assembly.util.http.ApacheHttpSimpleRequestUtils;

import java.util.Map;

/**
 * One of the implementation classes of {@link HttpMethod}, please
 * refer to{@link ApacheHttpSimpleRequestUtils} for implementation.
 *
 * @author zpf
 * @since 1.1.1
 */
public class ApacheHttpMethod implements HttpMethod {

    public static final HttpMethod INSTANCE = new ApacheHttpMethod();

    private ApacheHttpMethod() {
    }

    @Override
    public String get(String url, Map<String, String> headers, Object requestParam, boolean montage) throws Exception {
        return ApacheHttpSimpleRequestUtils.get(url, headers, requestParam, montage);
    }

    @Override
    public String post(String url, Map<String, String> headers, Object requestParam, boolean montage) throws Exception {
        return ApacheHttpSimpleRequestUtils.post(url, headers, requestParam, montage);
    }

    @Override
    public String put(String url, Map<String, String> headers, Object requestParam, boolean montage) throws Exception {
        return ApacheHttpSimpleRequestUtils.put(url, headers, requestParam, montage);
    }

    @Override
    public String delete(String url, Map<String, String> headers, Object requestParam, boolean montage) throws Exception {
        return ApacheHttpSimpleRequestUtils.delete(url, headers, requestParam, montage);
    }
}
