package top.osjf.assembly.cache.config;

import top.osjf.assembly.cache.listener.ExpirationMessageListener;
import top.osjf.assembly.cache.persistence.ListeningRecovery;
import top.osjf.assembly.util.lang.ClassUtils;
import top.osjf.assembly.util.lang.ReflectUtils;
import top.osjf.assembly.util.system.SystemUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * This article is a configuration class for component caching.
 * <p>The central configuration that supports the use of this
 * caching component is managed here, mainly consisting of two parts
 * <ul>
 *     <li>The cache saving mechanism at runtime.</li>
 *     <li>Configuration related to cache persistence.</li>
 * </ul>
 *
 * <p>Part of the runtime cache configuration is saved in the
 * system cache {@link SystemUtils}, and when needed, it is
 * retrieved from the system cache. Some listeners that need to
 * be persisted are directly and statically saved in the configuration
 * class, such as {@link #listeningRecoveries} and {@link #expirationMessageListeners}.
 * <p>This configuration class is designed as a singleton to ensure
 * thread safety for variable acquisition.
 *
 * <p>Updated to 1.1.4, with clearer configuration, retaining the
 * corresponding configuration from the previous system variables,
 * and adding more convenient extensions.
 *
 * @author zpf
 * @since 1.0.0
 */
public class Configuration {

    /**
     * The default cache time when no cache time limit is provided.
     */
    private Long defaultCacheDuration;

    /**
     * The default cache time unit when no cache time unit limit is provided.
     */
    private TimeUnit defaultCacheDurationUnit;

    /**
     * Is cache persistence enabled.
     * <p>If {@code true}, cache persistence is enabled and not enabled by default.
     */
    private Boolean enablePersistence;

    /**
     * Non persistent critical cache time.
     */
    private Long nonCachePersistentCriticalDuration;
    /**
     * Non persistent critical cache time unit.
     */
    private TimeUnit nonCachePersistentCriticalDurationUnit;

    /**
     * Whether to enable asynchronous cache persistence.
     * <p>If {@code true}, asynchronous operation when enabling cache persistence,
     * default not enabled.
     */
    private Boolean enablePersistenceAsync;

    /**
     * The temporary storage path for cache persistent files.
     */
    private String persistencePath;

    /**
     * Collection of listener types for callback when cache values expire.
     */
    private List<Class<? extends ExpirationMessageListener>> expirationMessageListenerTypes;

    /**
     * Collection of notifications for cache restart and recovery types.
     */
    private List<Class<? extends ListeningRecovery>> listeningRecoveryTypes;

    /**
     * Collection of listeners for callback when cache values expire.
     */
    private final List<ExpirationMessageListener> expirationMessageListeners = new CopyOnWriteArrayList<>();

    /**
     * Collection of notifications for cache restart and recovery.
     */
    private final List<ListeningRecovery> listeningRecoveries = new CopyOnWriteArrayList<>();

    //———————————————————————————————— System properties key ——————————————————————————————————————————

    /*** The default cache time for the system variable key.*/
    public static final String defaultCacheDurationKey = "assembly.cache.default.cache.duration";

    /*** The default cache time unit for the system variable key.*/
    public static final String defaultCacheDurationUnitKey = "assembly.cache.default.cache.duration.timeUnit";

    /*** Does the system variable key enable cache persistence.*/
    public static final String enablePersistenceKey = "assembly.cache.enable.persistence";

    /*** The critical time for cache persistence of system variable key.*/
    public static final String nonCachePersistentCriticalDurationKey = "assembly.cache.persistent.critical.duration";

    /*** The critical time unit for cache persistence of system variable key.*/
    public static final String nonCachePersistentCriticalDurationUnitKey
            = "assembly.cache.persistent.critical.duration.timeUnit";

    /*** Enable cache persistence for asynchronous execution of system variable key.*/
    public static final String enablePersistenceAsyncKey = "assembly.cache.persistence.async";

    /*** Cache persistence path for system variable key.*/
    public static final String persistencePathKey = "assembly.cache.persistence.path";

    /*** The fully qualified collection of class names for cache expiration listeners of the system variable key
     * Please separate classes completely with commas.
     * @see #multipleSplitSymbols
     * @see #newInstanceMultipleSplit(String)
     * .*/
    //Please separate classes completely with commas.
    public static final String expirationMessageListenersKey = "assembly.cache.expiration.listeners";

    /*** The fully qualified set of class names for the cache recovery listener of the system variable key.
     * Please separate classes completely with commas.
     * @see #multipleSplitSymbols
     * @see #newInstanceMultipleSplit(String)
     * */
    public static final String listeningRecoveriesKey = "assembly.cache.recover.listeners";

    //———————————————————————————————— constant ——————————————————————————————————————————
    private final static String multipleSplitSymbols = ",";
    private static long defaultNonCachePersistentCriticalDurationToMille;
    private static boolean defaultCompareWithCachePersistence = false;
    private static volatile Configuration configuration;

    /**
     * Set a global {@link Configuration}.
     *
     * @param configuration global using configuration.
     */
    public static void setGlobalConfiguration(Configuration configuration) {
        if (configuration != null) {
            configuration.compareDefaultCompareWithCachePersistence();
            Configuration.configuration = configuration;
        }
    }

    /**
     * Returns a globally unique configuration object.
     *
     * @return globally unique configuration object.
     */
    public static synchronized Configuration getConfiguration() {
        if (configuration == null) {
            configuration = new Configuration();
            configuration.compareDefaultCompareWithCachePersistence();
        }
        return configuration;
    }

    /**
     * Comparison when no input expiration time, the default Settings or default
     * expiration and default not persistent value of the size of the timestamp.
     */
    public void compareDefaultCompareWithCachePersistence() {
        Configuration configuration = getConfiguration();
        defaultNonCachePersistentCriticalDurationToMille =
                configuration.getNonCachePersistentCriticalDurationUnit()
                        .toMillis(configuration.getNonCachePersistentCriticalDuration());
        defaultCompareWithCachePersistence = configuration.getDefaultCacheDurationUnit()
                .toMillis(configuration.getDefaultCacheDuration())
                >= defaultNonCachePersistentCriticalDurationToMille;
    }

    /**
     * Based on the comparison between the default cache time and the cache
     * threshold, determine whether persistence markers are needed when no
     * cache time is provided.
     * @return Persistent tags,if {@code true} need to be cache persistence.
     */
    public boolean isDefaultCompareWithCachePersistence() {
        return defaultCompareWithCachePersistence;
    }

    /**
     * Returns the conversion of the persistent cache time threshold to milliseconds.
     * @return conversion of the persistent cache time threshold to milliseconds.
     */
    public long getDefaultNonCachePersistentCriticalDurationToMille() {
        return defaultNonCachePersistentCriticalDurationToMille;
    }

    //———————————————————————————————— Set main setting ——————————————————————————————————————————

    public void setDefaultCacheDuration(Long defaultCacheDuration) {
        this.defaultCacheDuration = defaultCacheDuration;
    }

    public void setDefaultCacheDurationUnit(TimeUnit defaultCacheDurationUnit) {
        this.defaultCacheDurationUnit = defaultCacheDurationUnit;
    }

    public void setEnablePersistence(Boolean enablePersistence) {
        this.enablePersistence = enablePersistence;
    }

    public void setNonCachePersistentCriticalDuration(Long nonCachePersistentCriticalDuration) {
        this.nonCachePersistentCriticalDuration = nonCachePersistentCriticalDuration;
    }

    public void setNonCachePersistentCriticalDurationUnit(TimeUnit nonCachePersistentCriticalDurationUnit) {
        this.nonCachePersistentCriticalDurationUnit = nonCachePersistentCriticalDurationUnit;
    }

    public void setEnablePersistenceAsync(Boolean enablePersistenceAsync) {
        this.enablePersistenceAsync = enablePersistenceAsync;
    }

    public void setPersistencePath(String persistencePath) {
        this.persistencePath = persistencePath;
    }

    public void setExpirationMessageListenerTypes(
            List<Class<? extends ExpirationMessageListener>> expirationMessageListenerTypes) {
        this.expirationMessageListenerTypes = expirationMessageListenerTypes;
    }

    public void setListeningRecoveryTypes(
            List<Class<? extends ListeningRecovery>> listeningRecoveryTypes) {
        this.listeningRecoveryTypes = listeningRecoveryTypes;
    }

//———————————————————————————————— get main setting ——————————————————————————————————————————

    public Long getDefaultCacheDuration() {
        return getOrDefault(defaultCacheDuration, defaultCacheDurationKey, Long::valueOf, 10L);
    }

    public TimeUnit getDefaultCacheDurationUnit() {
        return getOrDefault(defaultCacheDurationUnit, defaultCacheDurationUnitKey, TimeUnit::valueOf, TimeUnit.SECONDS);
    }

    public boolean isEnablePersistence() {
        return getOrDefault(enablePersistence, enablePersistenceKey, Boolean::valueOf, false);
    }

    public Long getNonCachePersistentCriticalDuration() {
        return getOrDefault(nonCachePersistentCriticalDuration, nonCachePersistentCriticalDurationKey,
                Long::valueOf, 60L);
    }

    public TimeUnit getNonCachePersistentCriticalDurationUnit() {
        return getOrDefault(nonCachePersistentCriticalDurationUnit, nonCachePersistentCriticalDurationUnitKey,
                TimeUnit::valueOf, TimeUnit.SECONDS);
    }

    public boolean isEnablePersistenceAsync() {
        return getOrDefault(enablePersistenceAsync, enablePersistenceAsyncKey, Boolean::valueOf, false);
    }

    public String getPersistencePath() {
        return getOrDefault(persistencePath, persistencePathKey, String::valueOf,
                SystemUtils.getCurrentProjectPath() + "/expire/");
    }

    public List<ExpirationMessageListener> getExpirationMessageListeners() {
        expirationMessageListenerTypes.forEach(e -> addExpirationMessageListener(ReflectUtils.newInstance(e)));
        return getOrDefault(expirationMessageListeners, expirationMessageListenersKey, this::newInstanceMultipleSplit,
                Collections.emptyList());
    }

    /**
     * Add a cache expiration listener.
     * @see #addExpirationMessageListeners(List)
     * @param listener cache expiration listener.
     */
    public void addExpirationMessageListener(ExpirationMessageListener listener) {
        synchronized (expirationMessageListeners) {
            expirationMessageListeners.remove(listener);
            expirationMessageListeners.add(listener);
        }
    }

    public void addExpirationMessageListeners(List<ExpirationMessageListener> listeners) {
        synchronized (expirationMessageListeners) {
            expirationMessageListeners.removeAll(listeners);
            expirationMessageListeners.addAll(listeners);
        }
    }

    /**
     * Add a cache recovery listener.
     * @see #addListeningRecoveries(List)
     * @param listener cache recovery listener.
     */
    public void addListeningRecovery(ListeningRecovery listener) {
        synchronized (expirationMessageListeners) {
            listeningRecoveries.remove(listener);
            listeningRecoveries.add(listener);
        }
    }

    public void addListeningRecoveries(List<ListeningRecovery> listeners) {
        synchronized (expirationMessageListeners) {
            listeningRecoveries.removeAll(listeners);
            listeningRecoveries.addAll(listeners);
        }
    }
    public List<ListeningRecovery> getListeningRecoveries() {
        listeningRecoveryTypes.forEach(l -> addListeningRecovery(ReflectUtils.newInstance(l)));
        return getOrDefault(listeningRecoveries, listeningRecoveriesKey, this::newInstanceMultipleSplit,
                Collections.emptyList());
    }

    //———————————————————————————————— other ——————————————————————————————————————————

    @SuppressWarnings("unchecked")
    private <T> List<T> newInstanceMultipleSplit(String classNames) {
        List<T> ts = new ArrayList<>();
        for (String className : classNames.split(multipleSplitSymbols)) {
            T t;
            try {
                t = (T) ReflectUtils.getConstructor(ClassUtils.getClass(className)).newInstance();
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
            ts.add(t);
        }
        return ts;
    }

    private <T> T getOrDefault(T nowValue, String propertiesKey, Function<String, T> convert, T defaultValue) {
        if (nowValue != null) {
            if (nowValue instanceof Collection) {
                if (!((Collection<?>) nowValue).isEmpty()) {
                    return nowValue;
                }
            } else return nowValue;
        }
        return SystemUtils.getPropertyWithConvert(propertiesKey, convert, defaultValue);
    }
}
