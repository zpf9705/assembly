package top.osjf.assembly.cache.persistence;

import top.osjf.assembly.util.annotation.CanNull;
import top.osjf.assembly.util.annotation.NotNull;
import top.osjf.assembly.util.logger.Console;

import java.util.concurrent.TimeUnit;

/**
 * Caching persistent file method operation interface. According to the
 * definition of persistent methods,operations such as adding, modifying,
 * deleting, and cleaning the current persistent file can be performed.
 * Please refer to the implementation class for details.
 * @param <K> the type of keys maintained by this cache.
 * @param <V> the type of cache values.
 * @author zpf
 * @since 1.0.0
 */
public interface CachePersistenceSolver<K, V> {

    //default CachePersistenceSolver
    @SuppressWarnings("rawtypes") // since 1.1.4
    CachePersistenceSolver INSTANCE = new BytesCachePersistenceSolver();

    /**
     * Put {@code key} and {@code value} and {@code duration} and {@code timeUnit} in to persistence
     *
     * @param key      must not be {@literal null}.
     * @param value    must not be {@literal null}.
     * @param duration can be {@literal null}.
     * @param timeUnit can be  {@literal null}.
     */
    void putPersistence(@NotNull K key, @NotNull V value, @CanNull Long duration, @CanNull TimeUnit timeUnit);

    /**
     * Replace the corresponding {@code  key} {@code value} the value of a {@code newValue}
     *
     * @param key      must not be {@literal null}.
     * @param newValue must not be {@literal null}.
     */
    void replaceValuePersistence(@NotNull K key, @NotNull V newValue);

    /**
     * Set a {@code key} and {@code value} with new duration , but if {@code key} exist
     *
     * @param key      must not be {@literal null}.
     * @param duration must not be {@literal null}.
     * @param timeUnit must not be {@literal null}.
     */
    void replaceDurationPersistence(@NotNull K key, @NotNull Long duration, @NotNull TimeUnit timeUnit);

    /**
     * Rest a {@code key} and {@code value} combination of persistence
     *
     * @param key must not be {@literal null}.
     */
    void restDurationPersistence(@NotNull K key);

    /**
     * Remove a {@code key} and {@code value} persistence record
     *
     * @param key must not be {@literal null}.
     */
    void removePersistenceWithKey(@NotNull K key);

    /**
     * Remove a {@code key} Similar  persistence record
     *
     * @param key must not be {@literal null}.
     */
    void removeSimilarKeyPersistence(@NotNull K key);

    /**
     * Remove all the cache files
     */
    default void removeAllPersistence() {
        run(AbstractCachePersistence::cleanAllCacheFile, "removeAllPersistence");
    }

    /**
     * Run the method and capture the exception
     *
     * @param runnable method runnable
     * @param method   method name
     */
    default void run(@NotNull Runnable runnable, @NotNull String method) {
        MethodRunnableCapable capable = Runner.getCapable();
        capable.run(runnable,
                esg -> Console.warn("Run the cache Persistence method [{}] An exception occurs [{}]", method, esg));
    }
}
