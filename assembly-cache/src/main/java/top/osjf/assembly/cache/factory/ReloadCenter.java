package top.osjf.assembly.cache.factory;

import top.osjf.assembly.cache.listener.ByteMessage;
import top.osjf.assembly.cache.persistence.Entry;
import top.osjf.assembly.util.annotation.NotNull;
import top.osjf.assembly.util.lang.ArrayUtils;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * Mainly applied to restart will persist the value of the
 * recovery by using the method of the interface.
 *
 * @param <K> The type of key.
 * @param <V> The type of value.
 * @author zpf
 * @since 1.0.0
 */
@SuppressWarnings("unchecked")
public interface ReloadCenter<K, V> {

    /**
     * Cache value recovery with {@link Entry}.
     *
     * @param key      must not be {@literal null}.
     * @param value    must not be {@literal null}.
     * @param duration must not be {@literal null}.
     * @param unit     must not be {@literal null}.
     */
    void reload(@NotNull K key, @NotNull V value, @NotNull Long duration, @NotNull TimeUnit unit);


    /**
     * Remove expired keys of auxiliary elements.
     *
     * @param message must not be {@literal null}.
     */
    void cleanSupportingElements(@NotNull ByteMessage message);


    /**
     * The default value wrapper function returns the first
     * bit of the array set if it is not empty.
     *
     * @param <T> The type of wrapper.
     * @return Wrap function.
     * @since 1.1.4
     */
    static <T> Function<Object[], T> defaultWrapper() {
        return objs -> ArrayUtils.isNotEmpty(objs) ? (T) objs[0] : null;
    }

    /**
     * Define a wrapper function for the key. If it is empty,
     * simply fill in the original value. This method can add
     * an extension to the generic.
     *
     * @return Define a wrapper function for the key.
     * @throws ClassCastException   Note that the type of the packaging
     *                              parameter array is abnormal.
     * @throws NullPointerException Note the null exception in the
     *                              packaging parameter array.
     * @since 1.1.4
     */
    default Function<Object[], K> wrapKeyFunc() {
        return defaultWrapper();
    }

    /**
     * Define a wrapper function for the value. If it is empty,
     * simply fill in the original value. This method can add
     * an extension to the generic.
     *
     * @return Define a wrapper function for the value.
     * @throws ClassCastException   Note that the type of the packaging
     *                              parameter array is abnormal.
     * @throws NullPointerException Note the null exception in the
     *                              packaging parameter array.
     * @since 1.1.4
     */
    default Function<Object[], V> wrapValueFunc() {
        return defaultWrapper();
    }
}
