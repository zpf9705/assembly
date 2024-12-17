/*
 * Copyright 2024-? the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package top.osjf.sdk.http;

import top.osjf.sdk.core.Response;

/**
 * The {@code HttpResponse} interface extends the Response interface and is used to define
 * standard status codes and related methods for HTTP responses.
 *
 * <p>This interface enumerates various status codes defined in the HTTP protocol,
 * including 1xx informational status codes, 2xx successful status codes,
 * 3xx redirection status codes, 4xx client error status codes, and 5xx server
 * error status codes. Each status code corresponds to an integer and a string
 * representation, making it convenient for referencing and comparing in code.
 *
 * <p>In addition, this interface inherits the {@link #isSuccess()} and {@link #getMessage()}
 * methods from the Response interface, which are used to determine whether the
 * response is successful and to obtain the response message, respectively.
 * At the same time, this interface adds a getCode() method to obtain the status code
 * of the response.
 *
 * <p>By implementing this interface, HTTP responses can be easily handled, and
 * corresponding logic processing can be performed based on the status codes.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
public interface HttpResponse extends Response {

    //update for 2.1.2
    //.......
    //copy form org.apache.http.HttpStatus

    // --- 1xx Informational ---
    /**
     * {@code 100 Continue} (HTTP/1.1 - RFC 2616)
     */
    int SC_CONTINUE = 100;
    String SC_CONTINUE0 = "100";
    /**
     * {@code 101 Switching Protocols} (HTTP/1.1 - RFC 2616)
     */
    int SC_SWITCHING_PROTOCOLS = 101;
    String SC_SWITCHING_PROTOCOLS0 = "101";
    /**
     * {@code 102 Processing} (WebDAV - RFC 2518)
     */
    int SC_PROCESSING = 102;
    String SC_PROCESSING0 = "102";

    // --- 2xx Success ---

    /**
     * {@code 200 OK} (HTTP/1.0 - RFC 1945)
     */
    int SC_OK = 200;
    String SC_OK0 = "200";
    /**
     * {@code 201 Created} (HTTP/1.0 - RFC 1945)
     */
    int SC_CREATED = 201;
    String SC_CREATED0 = "201";
    /**
     * {@code 202 Accepted} (HTTP/1.0 - RFC 1945)
     */
    int SC_ACCEPTED = 202;
    String SC_ACCEPTED0 = "202";
    /**
     * {@code 203 Non Authoritative Information} (HTTP/1.1 - RFC 2616)
     */
    int SC_NON_AUTHORITATIVE_INFORMATION = 203;
    String SC_NON_AUTHORITATIVE_INFORMATION0 = "203";
    /**
     * {@code 204 No Content} (HTTP/1.0 - RFC 1945)
     */
    int SC_NO_CONTENT = 204;
    String SC_NO_CONTENT0 = "204";
    /**
     * {@code 205 Reset Content} (HTTP/1.1 - RFC 2616)
     */
    int SC_RESET_CONTENT = 205;
    String SC_RESET_CONTENT0 = "205";
    /**
     * {@code 206 Partial Content} (HTTP/1.1 - RFC 2616)
     */
    int SC_PARTIAL_CONTENT = 206;
    String SC_PARTIAL_CONTENT0 = "206";
    /**
     * {@code 207 Multi-Status} (WebDAV - RFC 2518)
     * or
     * {@code 207 Partial Update OK} (HTTP/1.1 - draft-ietf-http-v11-spec-rev-01?)
     */
    int SC_MULTI_STATUS = 207;
    String SC_MULTI_STATUS0 = "207";

    // --- 3xx Redirection ---

    /**
     * {@code 300 Mutliple Choices} (HTTP/1.1 - RFC 2616)
     */
    int SC_MULTIPLE_CHOICES = 300;
    String SC_MULTIPLE_CHOICES0 = "300";
    /**
     * {@code 301 Moved Permanently} (HTTP/1.0 - RFC 1945)
     */
    int SC_MOVED_PERMANENTLY = 301;
    String SC_MOVED_PERMANENTLY0 = "301";
    /**
     * {@code 302 Moved Temporarily} (Sometimes {@code Found}) (HTTP/1.0 - RFC 1945)
     */
    int SC_MOVED_TEMPORARILY = 302;
    String SC_MOVED_TEMPORARILY0 = "302";
    /**
     * {@code 303 See Other} (HTTP/1.1 - RFC 2616)
     */
    int SC_SEE_OTHER = 303;
    String SC_SEE_OTHER0 = "303";
    /**
     * {@code 304 Not Modified} (HTTP/1.0 - RFC 1945)
     */
    int SC_NOT_MODIFIED = 304;
    String SC_NOT_MODIFIED0 = "304";
    /**
     * {@code 305 Use Proxy} (HTTP/1.1 - RFC 2616)
     */
    int SC_USE_PROXY = 305;
    String SC_USE_PROXY0 = "305";
    /**
     * {@code 307 Temporary Redirect} (HTTP/1.1 - RFC 2616)
     */
    int SC_TEMPORARY_REDIRECT = 307;
    String SC_TEMPORARY_REDIRECT0 = "307";

    // --- 4xx Client Error ---

    /**
     * {@code 400 Bad Request} (HTTP/1.1 - RFC 2616)
     */
    int SC_BAD_REQUEST = 400;
    String SC_BAD_REQUEST0 = "400";
    /**
     * {@code 401 Unauthorized} (HTTP/1.0 - RFC 1945)
     */
    int SC_UNAUTHORIZED = 401;
    String SC_UNAUTHORIZED0 = "401";
    /**
     * {@code 402 Payment Required} (HTTP/1.1 - RFC 2616)
     */
    int SC_PAYMENT_REQUIRED = 402;
    String SC_PAYMENT_REQUIRED0 = "402";
    /**
     * {@code 403 Forbidden} (HTTP/1.0 - RFC 1945)
     */
    int SC_FORBIDDEN = 403;
    String SC_FORBIDDEN0 = "403";
    /**
     * {@code 404 Not Found} (HTTP/1.0 - RFC 1945)
     */
    int SC_NOT_FOUND = 404;
    String SC_NOT_FOUND0 = "404";
    /**
     * {@code 405 Method Not Allowed} (HTTP/1.1 - RFC 2616)
     */
    int SC_METHOD_NOT_ALLOWED = 405;
    String SC_METHOD_NOT_ALLOWED0 = "405";
    /**
     * {@code 406 Not Acceptable} (HTTP/1.1 - RFC 2616)
     */
    int SC_NOT_ACCEPTABLE = 406;
    String SC_NOT_ACCEPTABLE0 = "406";
    /**
     * {@code 407 Proxy Authentication Required} (HTTP/1.1 - RFC 2616)
     */
    int SC_PROXY_AUTHENTICATION_REQUIRED = 407;
    String SC_PROXY_AUTHENTICATION_REQUIRED0 = "407";
    /**
     * {@code 408 Request Timeout} (HTTP/1.1 - RFC 2616)
     */
    int SC_REQUEST_TIMEOUT = 408;
    String SC_REQUEST_TIMEOUT0 = "408";
    /**
     * {@code 409 Conflict} (HTTP/1.1 - RFC 2616)
     */
    int SC_CONFLICT = 409;
    String SC_CONFLICT0 = "409";
    /**
     * {@code 410 Gone} (HTTP/1.1 - RFC 2616)
     */
    int SC_GONE = 410;
    String SC_GONE0 = "410";
    /**
     * {@code 411 Length Required} (HTTP/1.1 - RFC 2616)
     */
    int SC_LENGTH_REQUIRED = 411;
    String SC_LENGTH_REQUIRED0 = "411";
    /**
     * {@code 412 Precondition Failed} (HTTP/1.1 - RFC 2616)
     */
    int SC_PRECONDITION_FAILED = 412;
    String SC_PRECONDITION_FAILED0 = "412";
    /**
     * {@code 413 Request Entity Too Large} (HTTP/1.1 - RFC 2616)
     */
    int SC_REQUEST_TOO_LONG = 413;
    String SC_REQUEST_TOO_LONG0 = "413";
    /**
     * {@code 414 Request-URI Too Long} (HTTP/1.1 - RFC 2616)
     */
    int SC_REQUEST_URI_TOO_LONG = 414;
    String SC_REQUEST_URI_TOO_LONG0 = "414";
    /**
     * {@code 415 Unsupported Media Type} (HTTP/1.1 - RFC 2616)
     */
    int SC_UNSUPPORTED_MEDIA_TYPE = 415;
    String SC_UNSUPPORTED_MEDIA_TYPE0 = "415";
    /**
     * {@code 416 Requested Range Not Satisfiable} (HTTP/1.1 - RFC 2616)
     */
    int SC_REQUESTED_RANGE_NOT_SATISFIABLE = 416;
    String SC_REQUESTED_RANGE_NOT_SATISFIABLE0 = "416";
    /**
     * {@code 417 Expectation Failed} (HTTP/1.1 - RFC 2616)
     */
    int SC_EXPECTATION_FAILED = 417;
    String SC_EXPECTATION_FAILED0 = "417";

    /**
     * Static constant for a 419 error.
     * {@code 419 Insufficient Space on Resource}
     * (WebDAV - draft-ietf-webdav-protocol-05?)
     * or {@code 419 Proxy Reauthentication Required}
     * (HTTP/1.1 drafts?)
     */
    int SC_INSUFFICIENT_SPACE_ON_RESOURCE = 419;
    String SC_INSUFFICIENT_SPACE_ON_RESOURCE0 = "419";
    /**
     * Static constant for a 420 error.
     * {@code 420 Method Failure}
     * (WebDAV - draft-ietf-webdav-protocol-05?)
     */
    int SC_METHOD_FAILURE = 420;
    String SC_METHOD_FAILURE0 = "420";
    /**
     * {@code 422 Unprocessable Entity} (WebDAV - RFC 2518)
     */
    int SC_UNPROCESSABLE_ENTITY = 422;
    String SC_UNPROCESSABLE_ENTITY0 = "422";
    /**
     * {@code 423 Locked} (WebDAV - RFC 2518)
     */
    int SC_LOCKED = 423;
    String SC_LOCKED0 = "423";
    /**
     * {@code 424 Failed Dependency} (WebDAV - RFC 2518)
     */
    int SC_FAILED_DEPENDENCY = 424;
    String SC_FAILED_DEPENDENCY0 = "424";

    // --- 5xx Server Error ---

    /**
     * {@code 500 Server Error} (HTTP/1.0 - RFC 1945)
     */
    int SC_INTERNAL_SERVER_ERROR = 500;
    String SC_INTERNAL_SERVER_ERROR0 = "500";
    /**
     * {@code 501 Not Implemented} (HTTP/1.0 - RFC 1945)
     */
    int SC_NOT_IMPLEMENTED = 501;
    String SC_NOT_IMPLEMENTED0 = "501";
    /**
     * {@code 502 Bad Gateway} (HTTP/1.0 - RFC 1945)
     */
    int SC_BAD_GATEWAY = 502;
    String SC_BAD_GATEWAY0 = "502";
    /**
     * {@code 503 Service Unavailable} (HTTP/1.0 - RFC 1945)
     */
    int SC_SERVICE_UNAVAILABLE = 503;
    String SC_SERVICE_UNAVAILABLE0 = "503";
    /**
     * {@code 504 Gateway Timeout} (HTTP/1.1 - RFC 2616)
     */
    int SC_GATEWAY_TIMEOUT = 504;
    String SC_GATEWAY_TIMEOUT0 = "504";
    /**
     * {@code 505 HTTP Version Not Supported} (HTTP/1.1 - RFC 2616)
     */
    int SC_HTTP_VERSION_NOT_SUPPORTED = 505;
    String SC_HTTP_VERSION_NOT_SUPPORTED0 = "505";

    /**
     * {@code 507 Insufficient Storage} (WebDAV - RFC 2518)
     */
    int SC_INSUFFICIENT_STORAGE = 507;
    String SC_INSUFFICIENT_STORAGE0 = "507";

    /**
     * {@inheritDoc}
     */
    @Override
    boolean isSuccess();

    /**
     * {@inheritDoc}
     */
    @Override
    String getMessage();

    /**
     * Returns the status code that defines the status of an HTTP request.
     * <p>This attribute is often related to the business due to inconsistent
     * definition types, so {@link Object} is used here as a universal replacement
     * for the type determination of the status code.
     *
     * @return status code that defines the status of an HTTP request.
     */
    Object getCode();
}
