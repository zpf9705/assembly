package top.osjf.assembly.sdk;

import org.springframework.lang.NonNull;
import top.osjf.assembly.utils.HttpUtils;

import java.util.Map;

/**
 * SDK related attribute method definition interface,mainly including URL concatenation .
 * <p>
 * Request scheme selection, and related custom enumeration names .
 * <p>
 * You can check the example code:
 * <pre>
 * {@code
 * public enum Sdk implements SdkEnum {
 *
 * GET_SUPPLIER("***********", ApiProtocol.HTTP, ApiType.*, RequestMethod.POST),
 *
 * UPDATE_REPORT_BACK("***********", ApiProtocol.HTTP, ApiType.*, RequestMethod.POST),
 *
 * UPDATE_SHIPMENT_STATUS("***********", ApiProtocol.HTTP, ApiType.*, RequestMethod.POST),
 *
 * SAVE_OR_REMOVE_ADD_SERVICE("***********", ApiProtocol.HTTP, ApiType.*, RequestMethod.POST),
 *
 * private final String url;
 *
 * private final ApiProtocol apiProtocol;
 *
 * private final ApiType type;
 *
 * private final RequestMethod requestMethod;
 *
 * public String getUlr(String uri){
 * return String.format(this.url,this.apiProtocol.getPath(),uri,this.type.getType());
 * }
 *
 * public RequestMethod getRequestMethod(){
 * return this.requestMethod;
 * }
 * }}
 * </pre>
 *
 * @author zpf
 * @since 1.1.0
 */
public interface SdkEnum {

    /**
     * Get request url , generally, string formatting is required.
     *
     * @param host The host name of the SDK.
     * @return The request address for the SDK.
     */
    String getUlr(String host);

    /**
     * <p>Select the corresponding request scheme based on this enumeration identifier,
     * currently supporting the type of HTTP</p>
     *
     * @return {@link RequestMethod}.
     */
    RequestMethod getRequestMethod();

    /**
     * Get Enumeration Name.
     *
     * @return {@link Enum#name()}
     */
    String name();

    /**
     * API Request Address HTTP Protocol Header Enumeration Selection.
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

    /**
     * The interface used for defining HTTP request actions defines
     * different request method use cases for each enumeration scheme.
     */
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
        }
    }
}
