package top.osjf.assembly.cache.config;

import top.osjf.assembly.cache.listener.ExpirationMessageListener;
import top.osjf.assembly.cache.persistence.ListeningRecovery;
import top.osjf.assembly.util.io.ScanUtils;
import top.osjf.assembly.util.lang.CollectionUtils;
import top.osjf.assembly.util.lang.ReflectUtils;
import top.osjf.assembly.util.logger.Console;
import top.osjf.assembly.util.system.SystemUtils;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Configuration property classes related to cache persistence.
 *
 * @author zpf
 * @since 1.0.0
 */
public final class Configuration {

    public static final String open_persistence = "assembly.cache.open.persistence";
    public static final String persistenceRunAsync = "assembly.cache.persistence.run.async";
    public static final String persistence_path = "assembly.cache.persistence.path";
    public static final String noPersistenceOfExpireTime = "assembly.cache.noPersistence.expire.time";
    public static final String noPersistenceOfExpireTimeUnit = "assembly.cache.noPersistence.expire.timeUnit";
    public static final String defaultExpireTime = "assembly.cache.default.expire.time";
    public static final String defaultExpireTimeUnit = "assembly.cache.default.expire.timeUnit";
    @Deprecated
    public static final String chooseClient = "assembly.cache.choose.client";
    @Deprecated
    public static final String listeningRecoverySubPath = "assembly.cache.listening.recovery.path";
    private static final long defaultNoPersistenceExpireTimeExample = 10L;
    private static final long defaultExpireTimeExample = 20L;
    private static boolean defaultCompareWithExpirePersistence = false;
    private static final AtomicBoolean load = new AtomicBoolean(false);
    private static long defaultNoPersistenceExpireTimeToMille;
    private static final Configuration CONFIGURATION = new Configuration();
    private static final List<ListeningRecovery> listeningRecoveries = new CopyOnWriteArrayList<>();//since 1.0.8
    private static final List<ExpirationMessageListener> expirationMessageListeners = new CopyOnWriteArrayList<>();//since 1.0.8

    private Configuration() {
    }

    public static Configuration getConfiguration() {
        return CONFIGURATION;
    }

    static {
        compareDefaultCompareWithExpirePersistence();
    }

    /**
     * Comparison when no input expiration time, the default Settings or default
     * expiration and default not persistent value of the size of the timestamp.
     */
    public static void compareDefaultCompareWithExpirePersistence() {
        if (!load.compareAndSet(false, true)) {
            return;
        }
        Configuration configuration = getConfiguration();
        TimeUnit defaultExpireTimeUnit = configuration.getDefaultExpireTimeUnit();
        if (defaultExpireTimeUnit == null) {
            return;
        }
        TimeUnit noPersistenceOfExpireTimeUnit = configuration.getNoPersistenceOfExpireTimeUnit();
        if (noPersistenceOfExpireTimeUnit == null) {
            return;
        }
        defaultNoPersistenceExpireTimeToMille =
                noPersistenceOfExpireTimeUnit.toMillis(configuration.getNoPersistenceOfExpireTime());
        try {
            defaultCompareWithExpirePersistence = defaultExpireTimeUnit.toMillis(configuration.getDefaultExpireTime())
                    >= defaultNoPersistenceExpireTimeToMille;
        } catch (Exception ignored) {
        }
    }

    /**
     * Whether you need to obtain the default persistent identity.
     *
     * @return if {@code true} persistence right.
     */
    public boolean isDefaultCompareWithExpirePersistence() {
        return defaultCompareWithExpirePersistence;
    }

    /**
     * Get the default not persistent {@link TimeUnit#toMillis(long)} value.
     *
     * @return result with long.
     */
    public long getDefaultNoPersistenceExpireTimeToMille() {
        return defaultNoPersistenceExpireTimeToMille;
    }

    /**
     * For whether to open the cache persistent system configuration button.
     *
     * @return if {@code true} will open.
     */
    public boolean getOpenPersistence() {
        return SystemUtils.getPropertyWithConvert(open_persistence, Boolean::parseBoolean, false);
    }

    /**
     * For whether to open the cache persistence operation asynchronous execution system configuration.
     *
     * @return if {@code true} will Async.
     */
    public boolean getPersistenceAsync() {
        return SystemUtils.getPropertyWithConvert(persistenceRunAsync, Boolean::parseBoolean, false);
    }

    /**
     * The persistent cache disk write path system configuration.
     *
     * @return if not null {@code PersistencePath} will write this path.
     */
    public String getPersistencePath() {
        return SystemUtils.getPropertyWithConvert(persistence_path, Function.identity(), null);
    }

    /**
     * Obtain a maximum time of system configuration without persistence.
     *
     * @return Below this time will not persistent.
     */
    public long getNoPersistenceOfExpireTime() {
        return SystemUtils.getPropertyWithConvert(noPersistenceOfExpireTime, Long::parseLong,
                defaultNoPersistenceExpireTimeExample);
    }

    /**
     * Obtain a maximum time unit of system configuration without persistence.
     *
     * @return {@link #getNoPersistenceOfExpireTime()}.
     */
    public TimeUnit getNoPersistenceOfExpireTimeUnit() {
        return SystemUtils.getPropertyWithConvert(noPersistenceOfExpireTimeUnit, TimeUnit::valueOf, null);
    }

    /**
     * Get the default cache time system configuration.
     *
     * @return If you don't set the cache time, will use the configured.
     */
    public long getDefaultExpireTime() {
        return SystemUtils.getPropertyWithConvert(defaultExpireTime, Long::parseLong, defaultExpireTimeExample);
    }

    /**
     * Get the default cache time unit system configuration.
     *
     * @return {@link #getDefaultExpireTime()}.
     */
    public TimeUnit getDefaultExpireTimeUnit() {
        return SystemUtils.getPropertyWithConvert(defaultExpireTimeUnit, TimeUnit::valueOf, null);
    }

    /**
     * Retrieve all cache recovery listeners.
     *
     * @return recovery listeners
     * @since 1.0.8
     */
    public List<ListeningRecovery> getListeningRecoveries() {
        return listeningRecoveries;
    }

    /**
     * Get choose cache client.
     *
     * @return choose client.
     */
    @Deprecated
    public String getChooseClient() {
        return SystemUtils.getPropertyWithConvert(chooseClient, Function.identity(), null);
    }

    /**
     * Scan the path of the instruction to obtain recovery listeners.
     *
     * @param paths Scan paths.
     * @since 1.0.8
     */
    public static void scanListeningRecoveryWithPaths(List<String> paths) {
        scanAndAdd(paths, ListeningRecovery.class, Configuration::addListeningRecovery);
    }

    /**
     * Scan the path of the instruction to obtain expired listeners.
     *
     * @param paths Scan paths.
     * @since 1.0.8
     */
    public static void scanExpirationMessageListenerWithPaths(List<String> paths) {
        scanAndAdd(paths, ExpirationMessageListener.class, Configuration::addExpirationMessageListener);
    }

    /**
     * Add a recovery listener with class.
     *
     * @param clazz recovery listener class.
     * @since 1.0.8
     */
    public static void addListeningRecovery(Class<? extends ListeningRecovery> clazz) {
        add(clazz, (Consumer<ListeningRecovery>) Configuration::addListeningRecovery);
    }

    /**
     * Add an expiration message listener with class.
     *
     * @param clazz expiration message listener class.
     * @since 1.0.8
     */
    public static void addExpirationMessageListener(Class<? extends ExpirationMessageListener> clazz) {
        add(clazz, (Consumer<ExpirationMessageListener>) Configuration::addExpirationMessageListener);
    }

    /**
     * Add a recovery listener.
     *
     * @param recovery recovery listeners.
     * @since 1.0.8
     */
    public static void addListeningRecovery(ListeningRecovery recovery) {
        if (recovery != null && !listeningRecoveries.contains(recovery)) {
            listeningRecoveries.add(recovery);
        }
    }

    /**
     * Add an expiration message Listener.
     *
     * @param listener expiration message listeners.
     * @since 1.0.8
     */
    public static void addExpirationMessageListener(ExpirationMessageListener listener) {
        if (listener != null && !expirationMessageListeners.contains(listener)) {
            expirationMessageListeners.add(listener);
        }
    }

    /**
     * Retrieve all cache expiration message Listeners.
     *
     * @return expiration message listeners.
     * @since 1.0.8
     */
    public static List<ExpirationMessageListener> getExpirationMessageListeners() {
        return expirationMessageListeners;
    }

    private static <T> void scanAndAdd(List<String> paths, Class<? extends T> clazz, Consumer<T> consumer) {
        if (CollectionUtils.isEmpty(paths)) {
            return;
        }
        Set<? extends Class<? extends T>> subTypesOf = ScanUtils.getSubTypesOf(clazz, paths.toArray(new String[]{}));
        if (CollectionUtils.isEmpty(subTypesOf)) {
            return;
        }
        subTypesOf.forEach((clazz0) -> add(clazz0, consumer));
    }

    private static <T> void add(Class<? extends T> clazz, Consumer<T> consumer) {
        if (clazz == null || consumer == null) {
            return;
        }
        T t;
        try {
            t = ReflectUtils.newInstance(clazz);
        } catch (Exception e) {
            Console.warn(clazz.getName() + " newInstance error : {}", e.getMessage());
            return;
        }
        consumer.accept(t);
    }
}
