package top.osjf.assembly.cache.core;

import top.osjf.assembly.cache.help.ExpireHelper;
import top.osjf.assembly.util.annotation.CanNull;

/**
 * Callback interface for Redis 'low level' code. To be used with {@link ExpireTemplate} execution methods
 *
 * @author zpf
 * @since 3.0.0
 */
public interface ExpireValueCallback<T> {

    /**
     * Gets called by {@link ExpireTemplate} with an active Expire connection. Does not need to care about activating or
     * closing the Helper or handling exceptions.
     *
     * @param helper Expire Helper
     * @return a result object or {@code null} if none
     */
    @CanNull
    T doInExpire(ExpireHelper helper);
}
