package top.osjf.assembly.cache.operations;

import top.osjf.assembly.cache.factory.CacheExecutor;
import top.osjf.assembly.util.annotation.CanNull;

/**
 * Callback interface for Redis 'low level' code.
 * <p>To be used with {@link CacheTemplate} execution methods.
 * @param <T> The type of value.
 * @author zpf
 * @since 1.0.0
 */
public interface CacheValueCallback<T> {

    @CanNull
    T doInExecutor(CacheExecutor executor);
}
