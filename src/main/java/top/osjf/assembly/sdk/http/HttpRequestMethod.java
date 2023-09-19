package top.osjf.assembly.sdk.http;

import org.springframework.lang.NonNull;

import java.util.Map;


/**
 * Enumeration of HTTP request types.
 * <p>Implement the {@link HttpSdkEnum.Action} interface to define the action methods for each request method.</p>
 *
 * @author zpf
 * @see ApacheHttpSimpleRequestUtils
 * @see OkHttpSimpleRequestUtils
 * @since 1.1.1
 */
public enum HttpRequestMethod implements HttpSdkEnum.Action {

    GET {
        @Override
        public String doRequest(@NonNull Type type, @NonNull String url, Map<String, String> headers, Object requestParam,
                                Boolean montage) throws Exception {
            return type.getInstance().get(url, headers, requestParam, montage);
        }
    }, POST {
        @Override
        public String doRequest(@NonNull Type type, @NonNull String url, Map<String, String> headers, Object requestParam,
                                Boolean montage) throws Exception {
            return type.getInstance().post(url, headers, requestParam, montage);
        }
    }, PUT {
        @Override
        public String doRequest(@NonNull Type type, @NonNull String url, Map<String, String> headers, Object requestParam,
                                Boolean montage) throws Exception {
            return type.getInstance().put(url, headers, requestParam, montage);
        }
    }, DELETE {
        @Override
        public String doRequest(@NonNull Type type, @NonNull String url, Map<String, String> headers, Object requestParam,
                                Boolean montage) throws Exception {
            return type.getInstance().delete(url, headers, requestParam, montage);
        }
    };

    @Override
    public String doRequest(String url, Map<String, String> headers, Object requestParam, Boolean montage) {
        throw new UnsupportedOperationException();
    }
}
