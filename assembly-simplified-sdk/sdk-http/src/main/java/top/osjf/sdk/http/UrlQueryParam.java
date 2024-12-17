package top.osjf.sdk.http;

import top.osjf.sdk.core.support.Nullable;

/**
 * URL Query Parameter Interface.
 * <p>
 * The interface defines a method for obtaining the value associated
 * with the query parameter in the URL.
 * <p>
 * The object implementing this interface should be able to provide
 * data corresponding to specific query parameters.
 *
 * @see UrlQueryHttpRequest
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.2
 */
public interface UrlQueryParam {

    /**
     * Gets the value associated with the URL query parameter.
     * <p>
     * The value associated with the query parameter specified
     * in the URL, preferably a map structure, JSON string, or
     * entity object.
     *
     * @return Query parameter objects with desired features.
     * @see top.osjf.sdk.http.support.HttpSdkSupport#convertUrlQueryParamToMap(Object)
     */
    @Nullable
    Object getParam();
}
