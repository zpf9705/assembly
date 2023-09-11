package top.osjf.assembly.sdk;

import org.springframework.lang.NonNull;
import top.osjf.assembly.utils.HttpUtils;

import java.util.Map;

/**
 * SDK related attribute method definition interface,mainly including URL concatenation .
 * <p>
 * Request scheme selection, and related custom enumeration names .
 *
 * @author zpf
 * @since 1.1.0
 */
public interface SdkEnum {

    /**
     * Get request url , generally, string formatting is required
     *
     * @param host host address
     * @return request url address
     */
    String getUlr(String host);

    /**
     * Select the corresponding request scheme based on this enumeration identifier,
     * currently supporting the type of HTTP
     *
     * @return {@link RequestMethod}
     */
    RequestMethod getRequestMethod();

    /**
     * Get Enumeration Name
     *
     * @return {@link Enum#name()}
     */
    String name();

    /**
     * API Request Address HTTP Protocol Header Enumeration Selection
     */
    enum ApiProtocol {

        HTTPS("https:"),

        HTTP("http:");

        private final String path;

        ApiProtocol(String path) {
            this.path = path;
        }

        public String getPath() {
            return path;
        }
    }

    interface DoAction {

        String action(@NonNull String url, Map<String, String> headers, Object requestParam);
    }

    /**
     * Enumeration of currently supported types for HTTP.
     */
    enum RequestMethod implements DoAction {
        GET {
            @Override
            public String action(@NonNull String url, Map<String, String> headers, Object requestParam) {
                return HttpUtils.get(url, headers, requestParam);
            }
        }, POST {
            @Override
            public String action(@NonNull String url, Map<String, String> headers, Object requestParam) {
                return HttpUtils.post(url, headers, requestParam);
            }
        }, PUT {
            @Override
            public String action(@NonNull String url, Map<String, String> headers, Object requestParam) {
                return HttpUtils.put(url, headers, requestParam);
            }
        }, DELETE {
            @Override
            public String action(@NonNull String url, Map<String, String> headers, Object requestParam) {
                return HttpUtils.delete(url, headers, requestParam);
            }
        };
    }
}
