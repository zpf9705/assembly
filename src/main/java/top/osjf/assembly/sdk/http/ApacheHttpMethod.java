package top.osjf.assembly.sdk.http;

import top.osjf.assembly.utils.ApacheHttpUtils;

import java.util.Map;

/**
 * One of the implementation classes of {@link HttpMethod}, please refer to {@link ApacheHttpUtils} for implementation.
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
        return ApacheHttpUtils.get(url, headers, requestParam, montage);
    }

    @Override
    public String post(String url, Map<String, String> headers, Object requestParam, boolean montage) throws Exception {
        return ApacheHttpUtils.post(url, headers, requestParam, montage);
    }

    @Override
    public String put(String url, Map<String, String> headers, Object requestParam, boolean montage) throws Exception {
        return ApacheHttpUtils.put(url, headers, requestParam, montage);
    }

    @Override
    public String delete(String url, Map<String, String> headers, Object requestParam, boolean montage) throws Exception {
        return ApacheHttpUtils.delete(url, headers, requestParam, montage);
    }
}
