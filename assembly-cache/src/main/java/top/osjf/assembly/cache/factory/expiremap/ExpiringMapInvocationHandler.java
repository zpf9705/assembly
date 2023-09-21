package top.osjf.assembly.cache.factory.expiremap;

import top.osjf.assembly.cache.factory.CacheInvocationHandler;

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
