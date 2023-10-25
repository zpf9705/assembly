package top.osjf.assembly.cache.factory;

import top.osjf.assembly.cache.listener.MessageCapable;
import top.osjf.assembly.cache.persistence.Entry;
import top.osjf.assembly.util.annotation.NotNull;

import java.util.concurrent.TimeUnit;

/**
 * Mainly applied to restart will persist the value of the recovery by using the method of the interface.
 * @param <K> The type of key.
 * @param <V> The type of value.
 * @author zpf
 * @since 1.0.0
 */
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
     * @param capable must not be {@literal null}.
     */
    void cleanSupportingElements(@NotNull MessageCapable capable);
}
