package top.osjf.assembly.cache.operations;

import top.osjf.assembly.cache.serializer.PairSerializer;
import top.osjf.assembly.util.annotation.CanNull;
import top.osjf.assembly.util.annotation.NotNull;

import java.util.Collection;
import java.util.Map;

/**
 * The direct method entry of the {@link CacheTemplate} operation template,
 * all additions, deletions, modifications, and queries related to caching,
 * and the definition of methods should be aggregated to this interface and
 * implemented one by one in {@link CacheTemplate}. Moreover, from the class
 * operation interface, such as {@link ValueOperations}, it needs to be accessible
 * through this interface.
 * <p>
 * This can be seen as a summary of the overall API, which is the standardization
 * of the template class.
 * <p>
 * In the future, the interface can be inherited by the addition of cache components
 * and method extensions,Alternatively, add and implement on top of this interface.
 * @param <K> The type of key.
 * @param <V> The type of value.
 * @author zpf
 * @since 1.0.0
 **/
public interface CacheCommonsOperations<K, V> {

    /**
     * Execute cache scheme based on fallback interface {@link CacheValueCallback}.
     *
     * @param callback Callback action and must not be {@literal null}.
     * @param <T>      Generics of returns a value object.
     * @return Returns a value object.
     */
    @CanNull
    <T> T execute(CacheValueCallback<T> callback);

    /**
     * Delete given {@code key}.
     *
     * @param key must not be {@literal null}.
     * @return {@literal true} if the key was removed.
     */
    @CanNull
    Boolean delete(K key);

    /**
     * Delete given {@code keys}.
     *
     * @param keys must not be {@literal null}.
     * @return The number of keys that were removed.
     */
    @CanNull
    Long delete(Collection<K> keys);

    /**
     * Delete given {@code keys} with this type [and end contains].
     *
     * @param key must not be {@literal null}.
     * @return Be removed in the similar key of key/value pair
     */
    Map<K, V> deleteType(K key);

    /**
     * Remove all currently exist in the data in memory.
     *
     * @return Determine the results , If the is true the removal of success
     */
    Boolean deleteAll();

    /**
     * Judge ths key {@code key} Whether exit.
     *
     * @param key must not be {@literal null}.
     * @return Determine the results , If it is true is the existence and vice does not exist.
     */
    Boolean exist(K key);

    /**
     * Obtain serialization support classes for {@code Key}.
     *
     * @return {@link PairSerializer}.
     */
    @NotNull
    PairSerializer<K> getKeySerializer();

    /**
     * Obtain serialization support classes for {@code Value}.
     *
     * @return {@link PairSerializer}.
     */
    @NotNull
    PairSerializer<V> getValueSerializer();

    /**
     * The implementation of {@link ValueOperations} is all based on {@link CacheTemplate},
     * so a {@link ValueOperations} can be obtained through this standard interface.
     *
     * @return {@link ValueOperations}
     */
    ValueOperations<K, V> opsForValue();

    /**
     * The implementation of {@link TimeOperations} is all based on {@link CacheTemplate},
     * so a {@link TimeOperations} can be obtained through this standard interface.
     *
     * @return {@link TimeOperations}
     */
    TimeOperations<K, V> opsForTime();
}
