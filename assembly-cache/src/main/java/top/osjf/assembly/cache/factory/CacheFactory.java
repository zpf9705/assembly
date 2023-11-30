package top.osjf.assembly.cache.factory;

import top.osjf.assembly.cache.operations.CacheTemplate;
import top.osjf.assembly.util.annotation.NotNull;

/**
 * Thread safe cache executor acquisition interface.
 * <p>
 * In order to ensure the cache distribution problem caused
 * by multiple generic initialization {@link CacheTemplate},
 * the concept of a factory is specifically introduced here
 * to ensure the only way to set the cache.
 * <p>
 * Obtain a unique {@link CacheExecutor} on this interface,
 * ensuring that multiple operations call the same environment
 * cache.
 * @author zpf
 * @since 1.0.0
 */
public interface CacheFactory {

    @NotNull
    CacheExecutor executor();
}
