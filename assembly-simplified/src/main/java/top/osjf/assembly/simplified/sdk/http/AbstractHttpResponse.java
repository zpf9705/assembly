package top.osjf.assembly.simplified.sdk.http;

import top.osjf.assembly.simplified.sdk.process.AbstractResponse;
import top.osjf.assembly.simplified.sdk.process.DefaultErrorResponse;

import java.util.Objects;

/**
 * Http response abstract node class, used to define common states,
 * unknown error messages, success plans, etc.
 *
 * <p>You can check the example code:
 * <pre>
 * {@code
 * public class TestR extends AbstractHttpResponse {
 *
 *     private Boolean success;
 *
 *     private Integer code;
 *
 *     private String message;
 *
 *     private Object errors;
 *
 *     private List<Supplier> data;
 * }}
 * </pre>
 *
 * <p>Due to differences in encapsulation interfaces, public fields are not provided here.
 * If you need to default, please refer to {@link DefaultErrorResponse}.
 * <dl>
 *     <dt>{@link DefaultErrorResponse#buildSdkExceptionResponse(String)}</dt>
 *     <dt>{@link DefaultErrorResponse#buildUnknownResponse(String)}</dt>
 *     <dt>{@link DefaultErrorResponse#buildDataErrorResponse(String)}</dt>
 * </dl>
 *
 * <p>The prerequisite for use is to check if the field name is consistent
 * with yours, otherwise the default information in {@link AbstractResponse}
 * will be obtained.
 *
 * @author zpf
 * @since 1.1.1
 */
@SuppressWarnings("serial")
public abstract class AbstractHttpResponse extends AbstractResponse implements HttpResponse {

    //update for 2.1.2
    //.......
    //copy form org.apache.http.HttpStatus

    // --- 1xx Informational ---
    /** {@code 100 Continue} (HTTP/1.1 - RFC 2616) */
    protected static final int SC_CONTINUE = 100;
    protected static final String SC_CONTINUE0 = "100";
    /** {@code 101 Switching Protocols} (HTTP/1.1 - RFC 2616)*/
    protected static final int SC_SWITCHING_PROTOCOLS = 101;
    protected static final String SC_SWITCHING_PROTOCOLS0 = "101";
    /** {@code 102 Processing} (WebDAV - RFC 2518) */
    protected static final int SC_PROCESSING = 102;
    protected static final String SC_PROCESSING0 = "102";

    // --- 2xx Success ---

    /** {@code 200 OK} (HTTP/1.0 - RFC 1945) */
    protected static final int SC_OK = 200;
    protected static final String SC_OK0 = "200";
    /** {@code 201 Created} (HTTP/1.0 - RFC 1945) */
    protected static final int SC_CREATED = 201;
    protected static final String SC_CREATED0 = "201";
    /** {@code 202 Accepted} (HTTP/1.0 - RFC 1945) */
    protected static final int SC_ACCEPTED = 202;
    protected static final String SC_ACCEPTED0 = "202";
    /** {@code 203 Non Authoritative Information} (HTTP/1.1 - RFC 2616) */
    protected static final int SC_NON_AUTHORITATIVE_INFORMATION = 203;
    protected static final String SC_NON_AUTHORITATIVE_INFORMATION0 = "203";
    /** {@code 204 No Content} (HTTP/1.0 - RFC 1945) */
    protected static final int SC_NO_CONTENT = 204;
    protected static final String SC_NO_CONTENT0 = "204";
    /** {@code 205 Reset Content} (HTTP/1.1 - RFC 2616) */
    protected static final int SC_RESET_CONTENT = 205;
    protected static final String SC_RESET_CONTENT0 = "205";
    /** {@code 206 Partial Content} (HTTP/1.1 - RFC 2616) */
    protected static final int SC_PARTIAL_CONTENT = 206;
    protected static final String SC_PARTIAL_CONTENT0 = "206";
    /**
     * {@code 207 Multi-Status} (WebDAV - RFC 2518)
     * or
     * {@code 207 Partial Update OK} (HTTP/1.1 - draft-ietf-http-v11-spec-rev-01?)
     */
    protected static final int SC_MULTI_STATUS = 207;
    protected static final String SC_MULTI_STATUS0 = "207";

    // --- 3xx Redirection ---

    /** {@code 300 Mutliple Choices} (HTTP/1.1 - RFC 2616) */
    protected static final int SC_MULTIPLE_CHOICES = 300;
    protected static final String SC_MULTIPLE_CHOICES0 = "300";
    /** {@code 301 Moved Permanently} (HTTP/1.0 - RFC 1945) */
    protected static final int SC_MOVED_PERMANENTLY = 301;
    protected static final String SC_MOVED_PERMANENTLY0 = "301";
    /** {@code 302 Moved Temporarily} (Sometimes {@code Found}) (HTTP/1.0 - RFC 1945) */
    protected static final int SC_MOVED_TEMPORARILY = 302;
    protected static final String SC_MOVED_TEMPORARILY0 = "302";
    /** {@code 303 See Other} (HTTP/1.1 - RFC 2616) */
    protected static final int SC_SEE_OTHER = 303;
    protected static final String SC_SEE_OTHER0 = "303";
    /** {@code 304 Not Modified} (HTTP/1.0 - RFC 1945) */
    protected static final int SC_NOT_MODIFIED = 304;
    protected static final String SC_NOT_MODIFIED0 = "304";
    /** {@code 305 Use Proxy} (HTTP/1.1 - RFC 2616) */
    protected static final int SC_USE_PROXY = 305;
    protected static final String SC_USE_PROXY0 = "305";
    /** {@code 307 Temporary Redirect} (HTTP/1.1 - RFC 2616) */
    protected static final int SC_TEMPORARY_REDIRECT = 307;
    protected static final String SC_TEMPORARY_REDIRECT0 = "307";

    // --- 4xx Client Error ---

    /** {@code 400 Bad Request} (HTTP/1.1 - RFC 2616) */
    protected static final int SC_BAD_REQUEST = 400;
    protected static final String SC_BAD_REQUEST0 = "400";
    /** {@code 401 Unauthorized} (HTTP/1.0 - RFC 1945) */
    protected static final int SC_UNAUTHORIZED = 401;
    protected static final String SC_UNAUTHORIZED0 = "401";
    /** {@code 402 Payment Required} (HTTP/1.1 - RFC 2616) */
    protected static final int SC_PAYMENT_REQUIRED = 402;
    protected static final String SC_PAYMENT_REQUIRED0 = "402";
    /** {@code 403 Forbidden} (HTTP/1.0 - RFC 1945) */
    protected static final int SC_FORBIDDEN = 403;
    protected static final String SC_FORBIDDEN0 = "403";
    /** {@code 404 Not Found} (HTTP/1.0 - RFC 1945) */
    protected static final int SC_NOT_FOUND = 404;
    protected static final String SC_NOT_FOUND0 = "404";
    /** {@code 405 Method Not Allowed} (HTTP/1.1 - RFC 2616) */
    protected static final int SC_METHOD_NOT_ALLOWED = 405;
    protected static final String SC_METHOD_NOT_ALLOWED0 = "405";
    /** {@code 406 Not Acceptable} (HTTP/1.1 - RFC 2616) */
    protected static final int SC_NOT_ACCEPTABLE = 406;
    protected static final String SC_NOT_ACCEPTABLE0 = "406";
    /** {@code 407 Proxy Authentication Required} (HTTP/1.1 - RFC 2616)*/
    protected static final int SC_PROXY_AUTHENTICATION_REQUIRED = 407;
    protected static final String SC_PROXY_AUTHENTICATION_REQUIRED0 = "407";
    /** {@code 408 Request Timeout} (HTTP/1.1 - RFC 2616) */
    protected static final int SC_REQUEST_TIMEOUT = 408;
    protected static final String SC_REQUEST_TIMEOUT0 = "408";
    /** {@code 409 Conflict} (HTTP/1.1 - RFC 2616) */
    protected static final int SC_CONFLICT = 409;
    protected static final String SC_CONFLICT0 = "409";
    /** {@code 410 Gone} (HTTP/1.1 - RFC 2616) */
    protected static final int SC_GONE = 410;
    protected static final String SC_GONE0 = "410";
    /** {@code 411 Length Required} (HTTP/1.1 - RFC 2616) */
    protected static final int SC_LENGTH_REQUIRED = 411;
    protected static final String SC_LENGTH_REQUIRED0 = "411";
    /** {@code 412 Precondition Failed} (HTTP/1.1 - RFC 2616) */
    protected static final int SC_PRECONDITION_FAILED = 412;
    protected static final String SC_PRECONDITION_FAILED0 = "412";
    /** {@code 413 Request Entity Too Large} (HTTP/1.1 - RFC 2616) */
    protected static final int SC_REQUEST_TOO_LONG = 413;
    protected static final String SC_REQUEST_TOO_LONG0 = "413";
    /** {@code 414 Request-URI Too Long} (HTTP/1.1 - RFC 2616) */
    protected static final int SC_REQUEST_URI_TOO_LONG = 414;
    protected static final String SC_REQUEST_URI_TOO_LONG0 = "414";
    /** {@code 415 Unsupported Media Type} (HTTP/1.1 - RFC 2616) */
    protected static final int SC_UNSUPPORTED_MEDIA_TYPE = 415;
    protected static final String SC_UNSUPPORTED_MEDIA_TYPE0 = "415";
    /** {@code 416 Requested Range Not Satisfiable} (HTTP/1.1 - RFC 2616) */
    protected static final int SC_REQUESTED_RANGE_NOT_SATISFIABLE = 416;
    protected static final String SC_REQUESTED_RANGE_NOT_SATISFIABLE0 = "416";
    /** {@code 417 Expectation Failed} (HTTP/1.1 - RFC 2616) */
    protected static final int SC_EXPECTATION_FAILED = 417;
    protected static final String SC_EXPECTATION_FAILED0 = "417";

    /**
     * Static constant for a 419 error.
     * {@code 419 Insufficient Space on Resource}
     * (WebDAV - draft-ietf-webdav-protocol-05?)
     * or {@code 419 Proxy Reauthentication Required}
     * (HTTP/1.1 drafts?)
     */
    protected static final int SC_INSUFFICIENT_SPACE_ON_RESOURCE = 419;
    protected static final String SC_INSUFFICIENT_SPACE_ON_RESOURCE0 = "419";
    /**
     * Static constant for a 420 error.
     * {@code 420 Method Failure}
     * (WebDAV - draft-ietf-webdav-protocol-05?)
     */
    protected static final int SC_METHOD_FAILURE = 420;
    protected static final String SC_METHOD_FAILURE0 = "420";
    /** {@code 422 Unprocessable Entity} (WebDAV - RFC 2518) */
    protected static final int SC_UNPROCESSABLE_ENTITY = 422;
    protected static final String SC_UNPROCESSABLE_ENTITY0 = "422";
    /** {@code 423 Locked} (WebDAV - RFC 2518) */
    protected static final int SC_LOCKED = 423;
    protected static final String SC_LOCKED0 = "423";
    /** {@code 424 Failed Dependency} (WebDAV - RFC 2518) */
    protected static final int SC_FAILED_DEPENDENCY = 424;
    protected static final String SC_FAILED_DEPENDENCY0 = "424";

    // --- 5xx Server Error ---

    /** {@code 500 Server Error} (HTTP/1.0 - RFC 1945) */
    protected static final int SC_INTERNAL_SERVER_ERROR = 500;
    protected static final String SC_INTERNAL_SERVER_ERROR0 = "500";
    /** {@code 501 Not Implemented} (HTTP/1.0 - RFC 1945) */
    protected static final int SC_NOT_IMPLEMENTED = 501;
    protected static final String SC_NOT_IMPLEMENTED0 = "501";
    /** {@code 502 Bad Gateway} (HTTP/1.0 - RFC 1945) */
    protected static final int SC_BAD_GATEWAY = 502;
    protected static final String SC_BAD_GATEWAY0 = "502";
    /** {@code 503 Service Unavailable} (HTTP/1.0 - RFC 1945) */
    protected static final int SC_SERVICE_UNAVAILABLE = 503;
    protected static final String SC_SERVICE_UNAVAILABLE0 = "503";
    /** {@code 504 Gateway Timeout} (HTTP/1.1 - RFC 2616) */
    protected static final int SC_GATEWAY_TIMEOUT = 504;
    protected static final String SC_GATEWAY_TIMEOUT0 = "504";
    /** {@code 505 HTTP Version Not Supported} (HTTP/1.1 - RFC 2616) */
    protected static final int SC_HTTP_VERSION_NOT_SUPPORTED = 505;
    protected static final String SC_HTTP_VERSION_NOT_SUPPORTED0 = "505";

    /** {@code 507 Insufficient Storage} (WebDAV - RFC 2518) */
    protected static final int SC_INSUFFICIENT_STORAGE = 507;
    protected static final String SC_INSUFFICIENT_STORAGE0 = "507";

    public static final String SUCCESS_MESSAGE = "Congratulations";

    public static final String FAILED_MESSAGE = "Internal system error";

    /** {@code isSuccess} and {@code  getMessage} define http success situation.*/

    @Override
    public boolean isSuccess() {
        return Objects.equals(getCode(), SC_OK) || Objects.equals(getCode(),SC_OK0);
    }

    @Override
    public String getMessage() {
        return SUCCESS_MESSAGE;
    }

    /***To avoid affecting the rewrite return type of subclasses, it is written as an object type.*/
    public Object getCode() {
        return SC_OK;
    }
}
