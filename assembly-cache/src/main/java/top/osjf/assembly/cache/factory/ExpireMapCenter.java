package top.osjf.assembly.cache.factory;

import net.jodah.expiringmap.ExpiringMap;
import top.osjf.assembly.cache.config.expiringmap.ExpiringMapClients;
import top.osjf.assembly.cache.listener.ByteMessage;
import top.osjf.assembly.cache.listener.DefaultExpiringmapExpirationListener;
import top.osjf.assembly.cache.persistence.CachePersistenceSolver;
import top.osjf.assembly.cache.serializer.CacheByteIdentify;
import top.osjf.assembly.util.annotation.NotNull;
import top.osjf.assembly.util.data.ByteIdentify;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

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
public class ExpireMapCenter extends AbstractRecordActivationCenter<ExpireMapCenter, ByteIdentify, ByteIdentify> {

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
     * Format function for key/value.
     */
    private static final Function<Object[], ByteIdentify> wrapperFunction =
            args -> new CacheByteIdentify((byte[]) args[0], (String) args[1]);

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
        singleton.addExpirationListener(DefaultExpiringmapExpirationListener.INSTANCE);
        return new ExpireMapCenter(singleton);
    }

    @Override
    public ExpireMapCenter getHelpCenter() {
        return getExpireMapCenter();
    }

    @Override
    public void reload(@NotNull ByteIdentify key, @NotNull ByteIdentify value, @NotNull Long duration,
                       @NotNull TimeUnit unit) {
        if (this.singleton == null) {
            return;
        }
        this.singleton.put(key, value, duration, unit);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void cleanSupportingElements(@NotNull ByteMessage message) {
        //Remove persistent cache
        CachePersistenceSolver.INSTANCE.removePersistenceWithKey(message.getByteKey());
    }

    @Override
    public Function<Object[], ByteIdentify> wrapperKeyFunction() {
        return wrapperFunction;
    }

    @Override
    public Function<Object[], ByteIdentify> wrapperValueFunction() {
        return wrapperFunction;
    }
}
