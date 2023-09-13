package top.osjf.assembly.sdk.http;

import org.springframework.lang.NonNull;
import top.osjf.assembly.utils.HttpUtils;

import java.util.Map;


/**
 * Enumeration of HTTP request types.
 * <p>Implement the {@link HttpSdkEnum.Action} interface to define the action methods for each request method.</p>
 *
 * @see HttpUtils
 * @author zpf
 * @since 1.1.1
 */
public enum HttpRequestMethod implements HttpSdkEnum.Action {

    GET {
        @Override
        public String apply(@NonNull String url, Map<String, String> headers, Object requestParam) {
            return HttpUtils.get(url, headers, requestParam);
        }
    }, POST {
        @Override
        public String apply(@NonNull String url, Map<String, String> headers, Object requestParam) {
            return HttpUtils.post(url, headers, requestParam);
        }
    }, PUT {
        @Override
        public String apply(@NonNull String url, Map<String, String> headers, Object requestParam) {
            return HttpUtils.put(url, headers, requestParam);
        }
    }, DELETE {
        @Override
        public String apply(@NonNull String url, Map<String, String> headers, Object requestParam) {
            return HttpUtils.delete(url, headers, requestParam);
        }
    }
}
