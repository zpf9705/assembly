package top.osjf.assembly.cache.command.expiremap;

import top.osjf.assembly.cache.command.CacheInvocationHandler;
import top.osjf.assembly.cache.factory.expiremap.ExpiringMapCacheExecutor;

/**
 * Given target {@link ExpiringMapCacheExecutor} for {@link CacheInvocationHandler}.
 *
 * @author zpf
 * @since 1.0.0
 */
public class ExpiringMapInvocationHandler extends CacheInvocationHandler<ExpiringMapCacheExecutor> {

    private static final long serialVersionUID = 2955057534102013205L;

    public ExpiringMapInvocationHandler(ExpiringMapCacheExecutor target) {
        super(target);
    }
}
