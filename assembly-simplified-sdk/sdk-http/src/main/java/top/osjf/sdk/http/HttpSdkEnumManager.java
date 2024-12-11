package top.osjf.sdk.http;

import top.osjf.sdk.core.process.Request;
import top.osjf.sdk.http.process.HttpSdkEnum;

import javax.annotation.concurrent.ThreadSafe;

/**
 * {@code HttpSdkEnumManager} is used to retrieve and cache a dedicated
 * immutable {@code HttpSdkEnum} object based on the current request object
 * {@code Request}.
 *
 * <p>This interface can be extended according to Java's SPI mechanism
 * {@link java.util.ServiceLoader}, with annotations {@link top.osjf.sdk.core.support.LoadOrder},
 * to achieve self defined extensions.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.2
 */
@ThreadSafe
@SuppressWarnings("rawtypes")
public interface HttpSdkEnumManager {

    /**
     * Based on a current request object {@code Request}, retrieve and cache a
     * dedicated immutable {@code HttpSdkEnum} object, hoping that the implementation
     * of this method is thread safe.
     *
     * @param currentRequest The current executing request
     *                       {@code Request} object.
     * @return Request relevant metadata information interface {@code HttpSdkEnum}.
     * @throws IllegalStateException If there are no available annotations for the
     *                               currently executed {@code HttpSdkEnumCultivate},
     *                               this exception object will be thrown.
     */
    HttpSdkEnum getAndSetHttpSdkEnum(Request currentRequest) throws IllegalStateException;
}
