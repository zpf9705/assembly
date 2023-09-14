package top.osjf.assembly.sdk.http;

import io.reactivex.rxjava3.functions.Function4;
import top.osjf.assembly.sdk.SdkEnum;

import java.util.Map;

/**
 * HTTP SDK related attribute method definition interface,mainly including URL concatenation .
 * <p>
 * Request scheme selection, and related custom enumeration names .
 * <p>
 * You can check the example code:
 * <pre>
 * {@code
 * public enum Sdk implements HttpSdkEnum {
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
 * public HttpRequestMethod getHttpRequestMethod){
 * return this.requestMethod;
 * }
 * }}
 * </pre>
 *
 * @author zpf
 * @since 1.1.1
 */
public interface HttpSdkEnum extends SdkEnum {

    /**
     * <p>Select the corresponding request scheme based on this enumeration identifier,
     * currently supporting the type of HTTP</p>
     *
     * @return {@link HttpRequestMethod}.
     */
    HttpRequestMethod getRequestMethod();

    interface Action extends Function4<String, Map<String, String>, Object, Boolean, String> {

        @Override
        String apply(String s, Map<String, String> stringStringMap, Object o, Boolean montage);
    }
}
