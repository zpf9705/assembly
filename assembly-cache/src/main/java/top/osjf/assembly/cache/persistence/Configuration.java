package top.osjf.assembly.cache.persistence;

import com.alibaba.fastjson.JSON;
import top.osjf.assembly.util.io.ScanUtils;
import top.osjf.assembly.util.json.FastJsonUtils;
import top.osjf.assembly.util.lang.CollectionUtils;
import top.osjf.assembly.util.lang.ReflectUtils;
import top.osjf.assembly.util.system.SystemUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.stream.Collectors;

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
    public static final String chooseClient = "assembly.cache.choose.client";
    public static final String listeningRecoverySubPath = "assembly.cache.listening.recovery.path";
    public static final String listeningRecoverySubClassNames = "assembly.cache.listening.recovery.classes";
    private static final long defaultNoPersistenceExpireTimeExample = 10L;
    private static final long defaultExpireTimeExample = 20L;
    private static boolean defaultCompareWithExpirePersistence = false;
    private static final AtomicBoolean load = new AtomicBoolean(false);
    private static long defaultNoPersistenceExpireTimeToMille;
    private static final Configuration CONFIGURATION = new Configuration();
    private static final List<ListeningRecovery> listeningRecoveries = new ArrayList<>();

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
     * Get choose cache client.
     *
     * @return choose client.
     */
    public String getChooseClient() {
        return SystemUtils.getPropertyWithConvert(chooseClient, Function.identity(), null);
    }

    /**
     * Get listening recovery path with finding sub for {@link ListeningRecovery}.
     *
     * @return Recovery scanner path.
     */
    public List<String> getListeningRecoverySubPath() {
        return SystemUtils.getPropertyWithConvert(listeningRecoverySubPath, JsonArrayToListStrFunction(), null);
    }

    /**
     * Get listening recovery subclass names for {@link ListeningRecovery}.
     *
     * @return Recovery subclass names.
     */
    public List<String> getListeningRecoverySubClassNames() {
        return SystemUtils.getPropertyWithConvert(listeningRecoverySubClassNames, JsonArrayToListStrFunction(), null);
    }

    /**
     * Get listening all recoveries Within names and paths.
     *
     * @return Sets of {@link ListeningRecovery}.
     */
    public List<ListeningRecovery> getListeningRecoveries() {
        if (listeningRecoveries.isEmpty()) {
            synchronized (listeningRecoveries) {
                List<String> listeningRecoverySubClassNames = getListeningRecoverySubClassNames();
                boolean namingHave = CollectionUtils.isNotEmpty(listeningRecoverySubClassNames);
                if (namingHave) {
                    listeningRecoverySubClassNames =
                            listeningRecoverySubClassNames.stream().distinct().collect(Collectors.toList());
                    for (String listeningRecoverySubClassName : listeningRecoverySubClassNames) {
                        ListeningRecovery listeningRecovery;
                        try {
                            listeningRecovery = ReflectUtils.newInstance(listeningRecoverySubClassName);
                            listeningRecoveries.add(listeningRecovery);
                        } catch (Exception ignored) {
                        }
                    }
                }
                List<String> listeningRecoveryPaths = getListeningRecoverySubPath();
                if (CollectionUtils.isNotEmpty(listeningRecoveryPaths)) {
                    Set<Class<ListeningRecovery>> scannerResult =
                            ScanUtils.getSubTypesOf(ListeningRecovery.class, listeningRecoveryPaths.toArray(new String[]{}));
                    if (CollectionUtils.isNotEmpty(scannerResult)) {
                        for (Class<ListeningRecovery> listeningRecoveryClass : scannerResult) {
                            ListeningRecovery listeningRecovery;
                            if (namingHave && listeningRecoverySubClassNames.contains(listeningRecoveryClass.getName())) {
                                continue;
                            }
                            try {
                                listeningRecovery = ReflectUtils.newInstance(listeningRecoveryClass);
                                listeningRecoveries.add(listeningRecovery);
                            } catch (Exception ignored) {
                            }
                        }
                    }
                }
            }
        }
        return listeningRecoveries;
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

    public static <T> ListConfigurationWrapper<T, String> ofListStrConfigurationWrapper(List<T> objs,
                                                                                        Function<T, String> collection) {
        return new ListConfigurationWrapper<>(objs, collection);
    }


    public static Function<String, List<String>> JsonArrayToListStrFunction() {
        return s -> {
            if (FastJsonUtils.isValidArray(s)) {
                return FastJsonUtils.parseArray(s, String.class);
            }
            return Collections.emptyList();
        };
    }

    /**
     * Collection Objs filed to String.
     *
     * @param <T> OBJECT TYPE.
     * @param <R> COLLECTION TYPE.
     */
    public static class ListConfigurationWrapper<T, R> {

        private final List<T> objs;

        private final Function<T, R> collection;

        private ListConfigurationWrapper(List<T> objs, Function<T, R> collection) {
            this.objs = objs;
            this.collection = collection;
        }

        @Override
        public String toString() {
            if (CollectionUtils.isNotEmpty(objs)) {
                if (collection != null) {
                    List<R> collect = objs.stream().map(collection).collect(Collectors.toList());
                    return JSON.toJSONString(collect);
                } else {
                    return objs.toString();
                }
            }
            return super.toString();
        }
    }
}
