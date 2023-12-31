package top.osjf.assembly.cache.factory;

import net.jodah.expiringmap.ExpiringMap;
import top.osjf.assembly.cache.config.expiringmap.ExpiringMapClients;
import top.osjf.assembly.cache.listener.ByteMessage;
import top.osjf.assembly.cache.listener.DefaultExpiringmapExpirationListener;
import top.osjf.assembly.cache.persistence.BytesCachePersistenceSolver;
import top.osjf.assembly.cache.persistence.CachePersistenceSolver;
import top.osjf.assembly.util.annotation.NotNull;
import top.osjf.assembly.util.data.ByteIdentify;
import top.osjf.assembly.util.spi.SpiLoads;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Cache center based on {@link ExpiringMap}.
 * <p>
 * This class will help implement the method of using {@link ExpiringMapClients}
 * to configure .
 * <p>
 * Singleton objects of {@link ExpiringMap} and placing them in
 * {@link AbstractRecordActivationCenter}, as well as rewriting and
 * caching information read through file recovery.
 * <p>
 * Once this class is encapsulated, it is not allowed to instantiate
 * empty constructs.
 * <p>It must be done through the above method and always maintain a
 * unique operand.</p>
 *
 * @author zpf
 * @since 1.0.0
 */
public class ExpireMapCenter extends AbstractRecordActivationCenter<ExpireMapCenter, byte[], byte[]> {

    private static final long serialVersionUID = -7878806306402600655L;

    /**
     * Singleton for {@link ExpireMapCenter}.
     */
    private static volatile ExpireMapCenter expireMapCenter;

    /**
     * Core for cache client {@link ExpiringMap}.
     */
    private ExpiringMap<ByteIdentify, ByteIdentify> singleton;

    /**
     * Do not instance for no args construct.
     */
    private ExpireMapCenter() {
    }

    /**
     * Instance for {@link ExpiringMap}.
     *
     * @param singleton A singleton object with {@link ExpiringMap}.
     */
    private ExpireMapCenter(ExpiringMap<ByteIdentify, ByteIdentify> singleton) {
        this.singleton = singleton;
    }

    /**
     * Singleton with {@code ExpireMapClientConfiguration}.
     *
     * @param clients must no be {@literal null}.
     * @return A {@link ExpiringMap}.
     */
    protected static ExpireMapCenter singletonWithConfiguration(@NotNull ExpiringMapClients clients) {
        if (expireMapCenter == null) {
            synchronized (ExpireMapCenter.class) {
                if (expireMapCenter == null) {
                    expireMapCenter = buildSingleton(clients);
                    setSingletonCenter(expireMapCenter);
                }
            }
        }
        return expireMapCenter;
    }

    /**
     * Get Singleton instance for {@code ExpireMapCenter}.
     *
     * @return A {@link ExpireMapCenter}.
     */
    public static ExpireMapCenter getExpireMapCenter() {
        Objects.requireNonNull(expireMapCenter, "ExpireMapCenter no Initialize");
        return expireMapCenter;
    }

    /**
     * Get operation with a {@code ExpiringMap}.
     *
     * @return A {@link ExpiringMap}.
     */
    public ExpiringMap<ByteIdentify, ByteIdentify> getSingleton() {
        return this.singleton;
    }

    /**
     * Build Singleton with {@code ExpireMapClientConfiguration}.
     *
     * @param clients must not be {@literal null}.
     * @return {@link ExpireMapCenter}.
     */
    private static ExpireMapCenter buildSingleton(@NotNull ExpiringMapClients clients) {
        ExpiringMap<ByteIdentify, ByteIdentify> singleton = ExpiringMap.builder()
                .maxSize(clients.getMaxSize())
                .expiration(clients.getDefaultExpireTime(), clients.getDefaultExpireTimeUnit())
                .expirationPolicy(clients.getExpirationPolicy())
                .variableExpiration()
                .build();
        singleton.addExpirationListener(DefaultExpiringmapExpirationListener.LISTENER);
        return new ExpireMapCenter(singleton);
    }

    @Override
    public ExpireMapCenter getHelpCenter() {
        return getExpireMapCenter();
    }

    @Override
    public void reload(@NotNull byte[] key, @NotNull byte[] value, @NotNull Long duration,
                       @NotNull TimeUnit unit) {
        if (this.singleton == null) {
            return;
        }
        this.singleton.put(new ByteIdentify(key), new ByteIdentify(value), duration, unit);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void cleanSupportingElements(@NotNull ByteMessage message) {
        //Remove persistent cache
        CachePersistenceSolver<byte[], byte[]> solver = SpiLoads.findSpi(CachePersistenceSolver.class)
                .getSpecifiedServiceBySubClass(BytesCachePersistenceSolver.class);
        if (solver != null) {
            solver.removePersistenceWithKey(message.getByteKey());
        }
    }
}
