package top.osjf.assembly.cache.persistence;

import java.util.concurrent.TimeUnit;

/**
 * After the project restarts, when the scheduled cache is restored,
 * listen for the restored content, including key value pairs and
 * corresponding time units.
 * @see top.osjf.assembly.cache.config.Configuration
 * @author zpf
 * @since 1.0.0
 */
public interface ListeningRecovery {

    /**
     * Restore cached keys and values.
     *
     * @param key   must not be {@literal null}.
     * @param value must not be {@literal null}.
     */
    void recovery(Object key, Object value);

    /**
     * Provide renewal time after recovery based on {@link #recovery(Object, Object)}.
     *
     * @param key      must not be {@literal null}.
     * @param value    must not be {@literal null}.
     * @param time     must not be {@literal null}.
     * @param timeUnit must not be {@literal null}.
     */
    default void recovery(Object key, Object value, Long time, TimeUnit timeUnit) {

    }
}
