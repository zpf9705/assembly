package top.osjf.assembly.cache.factory;

import net.jodah.expiringmap.ExpiringMap;
import top.osjf.assembly.cache.config.expiringmap.ExpiringMapClients;
import top.osjf.assembly.cache.listener.ByteMessage;
import top.osjf.assembly.cache.listener.DefaultExpiringmapExpirationListener;
import top.osjf.assembly.cache.persistence.CachePersistenceSolver;
import top.osjf.assembly.util.annotation.NotNull;
import top.osjf.assembly.util.data.ByteIdentify;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;

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
public class ExpireMapCenter extends AbstractRecordActivationCenter<ExpireMapCenter, ByteIdentify, ByteIdentify>
        implements Supplier<ExpiringMap<ByteIdentify, ByteIdentify>> {

    private static final long serialVersionUID = -7878806306402600655L;

    /**
     * {@link ExpireMapCenter} of globally unique singletons.
     */
    private static volatile ExpireMapCenter expireMapCenter;

    /**
     * The cache center supports the core of classes.
     */
    private final ExpiringMap<ByteIdentify, ByteIdentify> expiringMap;

    /**
     * Wrapper function for key/value.
     */
    private static final Function<Object[], ByteIdentify> wrapperFunction =
            args -> AbstractCacheExecutor.Holder.createByteIdentify(args[0], args[1]);

    /**
     * Private Constructor use a singleton instance {@link ExpiringMap}.
     *
     * @param expiringMap A singleton object with {@link ExpiringMap}.
     */
    private ExpireMapCenter(ExpiringMap<ByteIdentify, ByteIdentify> expiringMap) {
        this.expiringMap = expiringMap;
    }

    /**
     * Create a cache center about {@link ExpiringMap} using
     * custom configuration.
     *
     * @param clients Configuration of {@link ExpiringMap}.
     * @return a cache center about {@link ExpiringMap}.
     */
    protected static synchronized ExpireMapCenter createExpireMapCenter(@NotNull ExpiringMapClients clients) {
        if (expireMapCenter == null) {
            expireMapCenter = createExpireMapCenter0(clients);
            setGlobalCenter(expireMapCenter);
        }
        return expireMapCenter;
    }

    /* create ExpireMapCenter within ExpiringMapClients */
    static ExpireMapCenter createExpireMapCenter0(ExpiringMapClients clients) {
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
        if (expireMapCenter == null) throw new CenterUninitializedException(ExpireMapCenter.class);
        return expireMapCenter;
    }

    @Override
    public ExpiringMap<ByteIdentify, ByteIdentify> get() {
        return expiringMap;
    }

    @Override
    public void reload(@NotNull ByteIdentify key, @NotNull ByteIdentify value, @NotNull Long duration,
                       @NotNull TimeUnit unit) {
        if (this.expiringMap == null) return;
        this.expiringMap.put(key, value, duration, unit);
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
