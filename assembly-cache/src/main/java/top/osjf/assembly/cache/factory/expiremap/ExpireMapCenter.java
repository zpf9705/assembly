package top.osjf.assembly.cache.factory.expiremap;

import net.jodah.expiringmap.ExpirationListener;
import net.jodah.expiringmap.ExpiringMap;
import org.apache.commons.collections4.CollectionUtils;
import top.osjf.assembly.cache.config.expiringmap.ExpiringMapClients;
import top.osjf.assembly.cache.factory.RecordActivationCenter;
import top.osjf.assembly.cache.listener.MessageCapable;
import top.osjf.assembly.cache.persistence.ExpireBytesPersistenceSolver;
import top.osjf.assembly.cache.persistence.PersistenceSolver;
import top.osjf.assembly.util.SpiLoads;
import top.osjf.assembly.util.annotation.NotNull;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Cache center based on {@link ExpiringMap}.
 * <p>
 * This class will help implement the method of using {@link ExpiringMapClients}to configure .
 * <p>
 * Singleton objects of {@link ExpiringMap} and placing them in {@link RecordActivationCenter},
 * as well as rewriting and caching information read through file recovery.
 * <p>
 * Once this class is encapsulated, it is not allowed to instantiate empty constructs.
 * It must be done through the above method and always maintain a unique operand.
 *
 * @author zpf
 * @since 1.0.0
 */
public final class ExpireMapCenter extends RecordActivationCenter<ExpireMapCenter, byte[], byte[]> {

    private static final long serialVersionUID = -7878806306402600655L;

    /**
     * Singleton for {@link ExpireMapCenter}
     */
    private static volatile ExpireMapCenter expireMapCenter;

    /**
     * Core for cache client {@link ExpiringMap}
     */
    private ExpiringMap<byte[], byte[]> solveDifferentialGenericSingleton;

    /**
     * Do not instance for no args construct
     */
    private ExpireMapCenter() {
    }

    /**
     * Instance for {@link ExpiringMap}
     *
     * @param solveDifferentialGenericSingleton not be {@literal null}
     */
    private ExpireMapCenter(ExpiringMap<byte[], byte[]> solveDifferentialGenericSingleton) {
        this.solveDifferentialGenericSingleton = solveDifferentialGenericSingleton;
    }

    /**
     * Singleton with {@code ExpireMapClientConfiguration}
     *
     * @param clients must no be {@literal null}.
     * @return {@link net.jodah.expiringmap.ExpiringMap}
     */
    public static ExpireMapCenter singletonWithConfiguration(@NotNull ExpiringMapClients clients) {
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
     * Get Singleton instance for {@code ExpireMapCenter}
     *
     * @return {@link ExpireMapCenter}
     */
    public static ExpireMapCenter getExpireMapCenter() {
        Objects.requireNonNull(expireMapCenter, "ExpireMapCenter no Initialize");
        return expireMapCenter;
    }

    /**
     * Get operation with a {@code ExpiringMap}
     *
     * @return {@link net.jodah.expiringmap.ExpiringMap}
     */
    public ExpiringMap<byte[], byte[]> getExpiringMap() {
        return this.solveDifferentialGenericSingleton;
    }

    /**
     * Build Singleton with {@code ExpireMapClientConfiguration}.
     *
     * @param clients must no be {@literal null}.
     * @return {@link ExpireMapCenter}
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    private static ExpireMapCenter buildSingleton(@NotNull ExpiringMapClients clients) {
        ExpiringMap<byte[], byte[]> solveDifferentialGenericSingleton = ExpiringMap.builder()
                .maxSize(clients.getMaxSize())
                .expiration(clients.getDefaultExpireTime(), clients.getDefaultExpireTimeUnit())
                .expirationPolicy(clients.getExpirationPolicy())
                .variableExpiration()
                .build();
        if (CollectionUtils.isNotEmpty(clients.getSyncExpirationListeners())) {
            for (ExpirationListener expirationListener : clients.getSyncExpirationListeners()) {
                //sync
                solveDifferentialGenericSingleton.addExpirationListener(expirationListener);

            }
        }
        if (CollectionUtils.isNotEmpty(clients.getASyncExpirationListeners())) {
            for (ExpirationListener expirationListener : clients.getASyncExpirationListeners()) {
                //async
                solveDifferentialGenericSingleton.addAsyncExpirationListener(expirationListener);

            }
        }
        return new ExpireMapCenter(solveDifferentialGenericSingleton);
    }

    @Override
    public ExpireMapCenter getHelpCenter() {
        return getExpireMapCenter();
    }

    @Override
    public void reload(@NotNull byte[] key, @NotNull byte[] value, @NotNull Long duration,
                       @NotNull TimeUnit unit) {
        if (this.solveDifferentialGenericSingleton == null) {
            return;
        }
        this.solveDifferentialGenericSingleton.put(key, value, duration, unit);
        this.getContain().putIfAbsent(key, value);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void cleanSupportingElements(@NotNull MessageCapable capable) {
        //Remove control information
        this.getContain().remove(capable.getByteKey(), capable.getByteValue());
        //Remove persistent cache
        PersistenceSolver<byte[], byte[]> solver = SpiLoads.findSpi(PersistenceSolver.class)
                .getSpecifiedServiceBySubClass(ExpireBytesPersistenceSolver.class);
        if (solver != null) {
            solver.removePersistence(capable.getByteKey(), capable.getByteValue());
        }
    }
}
