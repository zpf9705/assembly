package top.osjf.assembly.cache.persistence;

import java.util.concurrent.TimeUnit;

/**
 * After the project restarts, when the scheduled cache is restored,
 * listen for the restored content, including key value pairs and
 * corresponding time units.
 *
 * @author zpf
 * @since 1.0.0
 */
public interface ListeningRecovery {

    /**
     * The key and value, as well as their remaining time information,
     * have been restored to the cache center.
     *
     * @param key         The key to recover cache.
     * @param value       The value to recover cache.
     * @param surplusTime The remaining time to recover cache.
     * @param timeUnit    The remaining time unit to recover cache.
     */
    default void recovery(Object key, Object value, Long surplusTime, TimeUnit timeUnit) {
        recovery(key, value);
    }

    /**
     * The key and value have been restored to the cache center.
     *
     * @param key         The key to recover cache.
     * @param value       The value to recover cache.
     */
    default void recovery(Object key, Object value) {

    }
}
