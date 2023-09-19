package top.osjf.assembly.sdk.http;

import java.util.Map;

/**
 * Defined four commonly used method types for HTTP request patterns.
 *
 * @author zpf
 * @since 1.1.1
 */
public interface HttpMethod {

    /**
     * HTTP request method for {@code Get}.
     *
     * @param url     The actual request address,must not be {@literal null}.
     * @param headers Header information map,can be {@literal null}.
     * @param param   Request parameters with type {@link Object},can be {@literal null}.
     * @param montage Whether to concatenate parameters in the form of {@link Map} or {@code Json}
     *                as key/value after the URL.
     * @return The {@code String} type of the return value
     * @throws Exception unknown exception.
     */
    String get(String url, Map<String, String> headers, Object param, boolean montage) throws Exception;

    /**
     * HTTP request method for {@code Post}.
     *
     * @param url     The actual request address,must not be {@literal null}.
     * @param headers Header information map,can be {@literal null}.
     * @param param   Request parameters with type {@link Object},can be {@literal null}.
     * @param montage Whether to concatenate parameters in the form of {@link Map} or {@code Json}
     *                as key/value after the URL.
     * @return The {@code String} type of the return value
     * @throws Exception unknown exception.
     */
    String post(String url, Map<String, String> headers, Object param, boolean montage) throws Exception;

    /**
     * HTTP request method for {@code Put}.
     *
     * @param url     The actual request address,must not be {@literal null}.
     * @param headers Header information map,can be {@literal null}.
     * @param param   Request parameters with type {@link Object},can be {@literal null}.
     * @param montage Whether to concatenate parameters in the form of {@link Map} or {@code Json}
     *                as key/value after the URL.
     * @return The {@code String} type of the return value
     * @throws Exception unknown exception.
     */
    String put(String url, Map<String, String> headers, Object param, boolean montage) throws Exception;

    /**
     * HTTP request method for {@code Delete}.
     *
     * @param url     The actual request address,must not be {@literal null}.
     * @param headers Header information map,can be {@literal null}.
     * @param param   Request parameters with type {@link Object},can be {@literal null}.
     * @param montage Whether to concatenate parameters in the form of {@link Map} or {@code Json}
     *                as key/value after the URL.
     * @return The {@code String} type of the return value
     * @throws Exception unknown exception.
     */
    String delete(String url, Map<String, String> headers, Object param, boolean montage) throws Exception;

    /**
     * Obtain singleton objects for different HTTP request patterns.
     */
    interface InstanceCapable {

        HttpMethod getInstance();
    }
}
