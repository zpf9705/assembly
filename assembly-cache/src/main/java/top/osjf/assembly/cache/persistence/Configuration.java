package top.osjf.assembly.cache.persistence;

import top.osjf.assembly.util.lang.StringUtils;
import top.osjf.assembly.util.system.SystemUtils;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
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
    public static final String chooseClient = "assembly.cache.choose.client";
    public static final String listeningRecoverySubPath = "assembly.cache.listening.recovery.path";
    private static final long defaultNoPersistenceExpireTimeExample = 10L;
    private static final long defaultExpireTimeExample = 20L;
    private static boolean defaultCompareWithExpirePersistence = false;
    private static final AtomicBoolean load = new AtomicBoolean(false);
    private static long defaultNoPersistenceExpireTimeToMille;
    private static final Configuration CONFIGURATION = new Configuration();

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
     * @return choose client.
     */
    public String[] getListeningRecoverySubPath() {
        return SystemUtils.getPropertyWithConvert(listeningRecoverySubPath, Configuration::toStringArrayToConvertArray,
                null);
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
     * Convert the string converted through the array {@link #toString()} method to the original array,
     * but not the same array object.
     *
     * @param toString Array string after toString.
     * @return Convert Array.
     */
    private static String[] toStringArrayToConvertArray(String toString) {
        if (StringUtils.isBlank(toString)) {
            return new String[0];
        }
        if (!(toString.startsWith("[") && toString.endsWith("]"))) {
            throw new IllegalArgumentException("Non compliant array: the conversion string does not contain " +
                    "'[' at the beginning or ']' at the end");
        }
        toString = toString.replace("[", "").replace("]", "");
        String[] split = toString.split(",");
        if (split.length == 0) {
            throw new IllegalArgumentException("Non compliant array: does not contain commas");
        }
        return split;
    }
}
