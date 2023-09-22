package top.osjf.assembly.cache.factory;

import cn.hutool.aop.ProxyUtil;
import top.osjf.assembly.cache.command.expiremap.ExpiringMapInvocationHandler;
import top.osjf.assembly.cache.config.expiringmap.ExpiringMapClients;
import top.osjf.assembly.util.annotation.NotNull;

/**
 * The creation class of the cache execution factory for {@link net.jodah.expiringmap.ExpiringMap}.
 *
 * @author zpf
 * @since 1.0.0
 */
public class ExpiringMapCacheFactory implements CacheFactory {

    private final ExpiringMapCacheExecutor executor;

    public ExpiringMapCacheFactory(@NotNull ExpiringMapClients clients) {
        this.executor = doCreateExpiringMapExecutor(clients);
    }

    @Override
    @NotNull
    public CacheExecutor executor() {
        return this.executor;
    }

    /**
     * Create a jdk proxy based {@link ExpiringMapCacheExecutor} object using its configuration
     * {@link ExpiringMapClients}.
     *
     * @param clients The configuration interface for the Expiring map , must not be {@link null}.
     * @return The cache factory executor of the Expiring map.
     */
    public ExpiringMapCacheExecutor doCreateExpiringMapExecutor(ExpiringMapClients clients) {
        //Real object generated singleton operation
        ExpireMapCenter expireMapCenter = ExpireMapCenter.singletonWithConfiguration(clients);
        //To approach the processor
        ExpiringMapInvocationHandler processor = new ExpiringMapInvocationHandler(
                new ExpiringMapCacheExecutorImpl(() -> expireMapCenter));
        //returns a jdk proxy object
        return ProxyUtil.newProxyInstance(processor, processor.getTarget().getClass().getInterfaces());
    }
}
