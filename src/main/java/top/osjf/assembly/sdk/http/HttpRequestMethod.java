package top.osjf.assembly.sdk.http;

import org.springframework.lang.NonNull;
import top.osjf.assembly.utils.HttpUtils;

import java.util.Map;


/**
 * Enumeration of HTTP request types.
 * <p>Implement the {@link HttpSdkEnum.Action} interface to define the action methods for each request method.</p>
 *
 * @author zpf
 * @see HttpUtils
 * @since 1.1.1
 */
public enum HttpRequestMethod implements HttpSdkEnum.Action {

    GET {
        @Override
        public String apply(@NonNull String url, Map<String, String> headers, Object requestParam, Boolean montage)
                throws Exception {
            return HttpUtils.get(url, headers, requestParam, montage);
        }
    }, POST {
        @Override
        public String apply(@NonNull String url, Map<String, String> headers, Object requestParam, Boolean montage)
                throws Exception {
            return HttpUtils.post(url, headers, requestParam, montage);
        }
    }, PUT {
        @Override
        public String apply(@NonNull String url, Map<String, String> headers, Object requestParam, Boolean montage)
                throws Exception {
            return HttpUtils.put(url, headers, requestParam, montage);
        }
    }, DELETE {
        @Override
        public String apply(@NonNull String url, Map<String, String> headers, Object requestParam, Boolean montage)
                throws Exception {
            return HttpUtils.delete(url, headers, requestParam, montage);
        }
    }
}
