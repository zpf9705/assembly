package top.osjf.assembly.sdk.http;

import top.osjf.assembly.utils.OkHttpUtils;

import java.util.Map;

/**
 * One of the implementation classes of {@link HttpMethod}, please refer to {@link OkHttpUtils} for implementation.
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
        return OkHttpUtils.get(url, headers, requestParam, montage);
    }

    @Override
    public String post(String url, Map<String, String> headers, Object requestParam, boolean montage) throws Exception {
        return OkHttpUtils.post(url, headers, requestParam, montage);
    }

    @Override
    public String put(String url, Map<String, String> headers, Object requestParam, boolean montage) throws Exception {
        return OkHttpUtils.put(url, headers, requestParam, montage);
    }

    @Override
    public String delete(String url, Map<String, String> headers, Object requestParam, boolean montage) throws Exception {
        return OkHttpUtils.delete(url, headers, requestParam, montage);
    }
}
