package top.osjf.assembly.cache.config;

import top.osjf.assembly.cache.listener.ExpirationMessageListener;
import top.osjf.assembly.cache.persistence.AbstractPersistenceFileManager;
import top.osjf.assembly.cache.persistence.ListeningRecovery;
import top.osjf.assembly.util.lang.ClassUtils;
import top.osjf.assembly.util.lang.ReflectUtils;
import top.osjf.assembly.util.lang.StringUtils;
import top.osjf.assembly.util.system.SystemUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
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
     *
     * @return Persistent tags,if {@code true} need to be cache persistence.
     */
    public boolean isDefaultCompareWithCachePersistence() {
        return defaultCompareWithCachePersistence;
    }

    /**
     * Returns the conversion of the persistent cache time threshold to milliseconds.
     *
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
        if (StringUtils.isNotBlank(persistencePath)) {
            AbstractPersistenceFileManager.checkDirectory(persistencePath);
        }
        this.persistencePath = persistencePath;
    }

//———————————————————————————————— get main setting ——————————————————————————————————————————

    public Long getDefaultCacheDuration() {
        return getOrPropertyUpdate(defaultCacheDuration, defaultCacheDurationKey, Long::valueOf, 10L,
                this::setDefaultCacheDuration);
    }

    public TimeUnit getDefaultCacheDurationUnit() {
        return getOrPropertyUpdate(defaultCacheDurationUnit, defaultCacheDurationUnitKey, TimeUnit::valueOf,
                TimeUnit.SECONDS, this::setDefaultCacheDurationUnit);
    }

    public boolean isEnablePersistence() {
        return getOrPropertyUpdate(enablePersistence, enablePersistenceKey, Boolean::valueOf, false,
                this::setEnablePersistence);
    }

    public Long getNonCachePersistentCriticalDuration() {
        return getOrPropertyUpdate(nonCachePersistentCriticalDuration, nonCachePersistentCriticalDurationKey,
                Long::valueOf, 60L, this::setNonCachePersistentCriticalDuration);
    }

    public TimeUnit getNonCachePersistentCriticalDurationUnit() {
        return getOrPropertyUpdate(nonCachePersistentCriticalDurationUnit, nonCachePersistentCriticalDurationUnitKey,
                TimeUnit::valueOf, TimeUnit.SECONDS, this::setNonCachePersistentCriticalDurationUnit);
    }

    public boolean isEnablePersistenceAsync() {
        return getOrPropertyUpdate(enablePersistenceAsync, enablePersistenceAsyncKey, Boolean::valueOf,
                false, this::setEnablePersistenceAsync);
    }

    public String getPersistencePath() {
        return getOrPropertyUpdate(persistencePath, persistencePathKey, String::valueOf,
                SystemUtils.getCurrentProjectPath() + "/expire/",
                this::setPersistencePath);
    }

    /**
     * Add a cache expiration listener.
     *
     * @param listener cache expiration listener.
     * @see #addExpirationMessageListeners(List)
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
     * Returns an immutable {@link ExpirationMessageListener} set.
     *
     * @return immutable {@link ExpirationMessageListener} set.
     */
    public List<ExpirationMessageListener> unmodifiableExpirationMessageListeners() {
        return Collections.unmodifiableList(
                getOrPropertyUpdate(expirationMessageListeners, expirationMessageListenersKey,
                        this::newInstanceMultipleSplit,
                        Collections.emptyList(), expirationMessageListeners::addAll));
    }

    /**
     * Add a cache recovery listener.
     *
     * @param listener cache recovery listener.
     * @see #addListeningRecoveries(List)
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

    /**
     * Returns an immutable {@link ListeningRecovery} set.
     *
     * @return immutable {@link ListeningRecovery} set.
     */
    public List<ListeningRecovery> unmodifiableListeningRecoveries() {
        return Collections.unmodifiableList(getOrPropertyUpdate(listeningRecoveries, listeningRecoveriesKey,
                this::newInstanceMultipleSplit,
                Collections.emptyList(), listeningRecoveries::addAll));
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

    private <T> T getOrPropertyUpdate(T nowValue, String propertiesKey, Function<String, T> convert, T defaultValue,
                                      Consumer<T> propertyGetUpdate) {
        if (nowValue != null) {
            if (nowValue instanceof Collection) {
                if (!((Collection<?>) nowValue).isEmpty()) {
                    return nowValue;
                }
            } else return nowValue;
        }
        T propertyValue = SystemUtils.getPropertyWithConvert(propertiesKey, convert, defaultValue);
        propertyGetUpdate.accept(propertyValue);
        return propertyValue;
    }
}
