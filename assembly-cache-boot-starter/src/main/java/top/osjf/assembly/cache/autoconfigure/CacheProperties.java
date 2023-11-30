package top.osjf.assembly.cache.autoconfigure;

import net.jodah.expiringmap.ExpirationListener;
import net.jodah.expiringmap.ExpirationPolicy;
import org.springframework.boot.context.properties.ConfigurationProperties;
import top.osjf.assembly.cache.persistence.ByteCachePersistence;
import top.osjf.assembly.cache.persistence.CachePersistenceReduction;
import top.osjf.assembly.cache.persistence.Configuration;
import top.osjf.assembly.cache.persistence.ListeningRecovery;
import top.osjf.assembly.util.lang.CollectionUtils;
import top.osjf.assembly.util.system.SystemUtils;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * Configuration properties for assembly-cache.
 *
 * @author zpf
 * @since 1.0.0
 */
@ConfigurationProperties(prefix = "assembly.cache")
public class CacheProperties {

    /**
     * Whether to open the cache persistence.
     *
     * <p>If setup this is {@code true} and will enable cache persistence and save the cache information
     * to the disk path where {@link #persistencePath} is located. </p>
     */
    private Boolean openPersistence = false;

    /**
     * Whether to run cache persistence async.
     *
     * <p>If setup this is {@code true} and will be executed asynchronously during cache persistence.</p>
     */
    private Boolean persistenceAsync = false;

    /**
     * Persistence Renew factory class.
     *
     * <p>The default is {@link ByteCachePersistence}.</p>
     */
    private Class<? extends CachePersistenceReduction> persistenceReductionClass = ByteCachePersistence.class;

    /**
     * Attention : If you offer the persistent path.<br>
     *
     * <p>Will automatically on your path to create persistent file.<br>
     *
     * <p>If not we will create in the root of your project directory.<br>
     */
    private String persistencePath = SystemUtils.getCurrentProjectPath() + "/expire/";

    /**
     * Cache persistence recovery implements a collection of class
     * objects provided by {@link ListeningRecovery} locks, similar
     * in function to {@link #listeningRecoverySubPath}.
     *
     * @since 1.0.8
     */
    private List<Class<? extends ListeningRecovery>> listeningRecoverySubClasses = new ArrayList<>();

    /**
     * Monitor the path information of cache recovery and implement the class collection
     * path of {@link ListeningRecovery}.
     */
    private List<String> listeningRecoverySubPath;

    /**
     * No persistence time the most value (that is less than all of this time are not given persistent).
     *
     * <p>The default is {@code 20}.</p>
     */
    private Long noPersistenceOfExpireTime = 20L;

    /**
     * No persistence unit of time the most value.
     *
     * <p>The default is {@code TimeUnit.SECONDS}.</p>
     */
    private TimeUnit noPersistenceOfExpireTimeUnit = TimeUnit.SECONDS;

    /**
     * Set a {@code defaultExpireTime} for default cache time.
     *
     * <p>The default is {@code 30L}.</p>
     */
    private Long defaultExpireTime = 30L;

    /**
     * Set a {@code defaultExpireTimeUnit} for default cache time unit.
     *
     * <p>The default is {@code TimeUnit.SECONDS}.</p>
     */
    private TimeUnit defaultExpireTimeUnit = TimeUnit.SECONDS;

    /**
     * Set a {@code client} for help source.
     *
     * <p>The default is {@link  Client#EXPIRE_MAP}.</p>
     */
    private Client client = Client.EXPIRE_MAP;

    /**
     * Expiry implement for {@link net.jodah.expiringmap.ExpiringMap}.
     */
    private ExpiringMap expiringMap = new ExpiringMap();

    public Boolean getOpenPersistence() {
        return openPersistence;
    }

    public void setOpenPersistence(Boolean openPersistence) {
        this.openPersistence = openPersistence;
    }

    public Boolean getPersistenceAsync() {
        return persistenceAsync;
    }

    public void setPersistenceAsync(Boolean persistenceAsync) {
        this.persistenceAsync = persistenceAsync;
    }

    public Class<? extends CachePersistenceReduction> getPersistenceReductionClass() {
        return persistenceReductionClass;
    }

    public void setPersistenceReductionClass(Class<? extends CachePersistenceReduction> persistenceReductionClass) {
        this.persistenceReductionClass = persistenceReductionClass;
    }

    public String getPersistencePath() {
        return persistencePath;
    }

    public void setPersistencePath(String persistencePath) {
        this.persistencePath = persistencePath;
    }

    public Long getNoPersistenceOfExpireTime() {
        return noPersistenceOfExpireTime;
    }

    public void setNoPersistenceOfExpireTime(Long noPersistenceOfExpireTime) {
        this.noPersistenceOfExpireTime = noPersistenceOfExpireTime;
    }

    public TimeUnit getNoPersistenceOfExpireTimeUnit() {
        return noPersistenceOfExpireTimeUnit;
    }

    public void setNoPersistenceOfExpireTimeUnit(TimeUnit noPersistenceOfExpireTimeUnit) {
        this.noPersistenceOfExpireTimeUnit = noPersistenceOfExpireTimeUnit;
    }

    public List<Class<? extends ListeningRecovery>> getListeningRecoverySubClasses() {
        return listeningRecoverySubClasses;
    }

    public void setListeningRecoverySubClasses(List<Class<? extends ListeningRecovery>> listeningRecoverySubClasses) {
        this.listeningRecoverySubClasses = listeningRecoverySubClasses;
    }

    public List<String> getListeningRecoverySubPath() {
        return listeningRecoverySubPath;
    }

    public void setListeningRecoverySubPath(List<String> listeningRecoverySubPath) {
        this.listeningRecoverySubPath = listeningRecoverySubPath;
    }

    public Long getDefaultExpireTime() {
        return defaultExpireTime;
    }

    public void setDefaultExpireTime(Long defaultExpireTime) {
        this.defaultExpireTime = defaultExpireTime;
    }

    public TimeUnit getDefaultExpireTimeUnit() {
        return defaultExpireTimeUnit;
    }

    public void setDefaultExpireTimeUnit(TimeUnit defaultExpireTimeUnit) {
        this.defaultExpireTimeUnit = defaultExpireTimeUnit;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public ExpiringMap getExpiringMap() {
        return expiringMap;
    }

    public void setExpiringMap(ExpiringMap expiringMap) {
        this.expiringMap = expiringMap;
    }

    public static class ExpiringMap {

        /**
         * Set a {@code maxsize} for map.
         *
         * <p>The default is {@code 500}.</p>
         */
        private Integer maxSize = 500;

        /**
         * Set a {@code expirationPolicy} for map.
         *
         * <p>The default is {@code ExpirationPolicy.ACCESSED}.</p>
         */
        private ExpirationPolicy expirationPolicy = ExpirationPolicy.ACCESSED;

        /**
         * Set {@code expiration Listeners} for map.
         * <p>The function is similar to {@link #expirationListenerScanPackages}.
         *
         * @since 1.0.8
         */
        @SuppressWarnings("rawtypes")
        private List<Class<? extends ExpirationListener>> expirationListenerClasses = new ArrayList<>();

        /**
         * Set a {@code listening packages} for map.
         *
         * <p>If it is null, the default is to use springboot to start the package path where the main class is located.
         */
        private List<String> expirationListenerScanPackages;

        public Integer getMaxSize() {
            return maxSize;
        }

        public void setMaxSize(Integer maxSize) {
            this.maxSize = maxSize;
        }

        public ExpirationPolicy getExpirationPolicy() {
            return expirationPolicy;
        }

        public void setExpirationPolicy(ExpirationPolicy expirationPolicy) {
            this.expirationPolicy = expirationPolicy;
        }

        @SuppressWarnings("rawtypes")
        public List<Class<? extends ExpirationListener>> getExpirationListenerClasses() {
            return expirationListenerClasses;
        }

        @SuppressWarnings("rawtypes")
        public void setExpirationListenerClasses(List<Class<? extends ExpirationListener>> expirationListenerClasses) {
            this.expirationListenerClasses = expirationListenerClasses;
        }

        public List<String> getExpirationListenerScanPackages() {
            return expirationListenerScanPackages;
        }

        public void setExpirationListenerScanPackages(List<String> expirationListenerScanPackages) {
            this.expirationListenerScanPackages = expirationListenerScanPackages;
        }
    }

    /**
     * The underlying support type for caching.
     */
    public enum Client {
        EXPIRE_MAP
    }

    @PostConstruct
    public void initForPersistenceConfiguration() {
        if (CollectionUtils.isEmpty(listeningRecoverySubClasses)) {
            if (CollectionUtils.isEmpty(listeningRecoverySubPath)) {
                listeningRecoverySubPath = SourceEnvironmentPostProcessor.findSpringbootPrimarySourcesPackages();
            }
        }
        if (CollectionUtils.isEmpty(expiringMap.expirationListenerClasses)) {
            if (CollectionUtils.isEmpty(expiringMap.expirationListenerScanPackages)) {
                expiringMap.expirationListenerScanPackages
                        = SourceEnvironmentPostProcessor.findSpringbootPrimarySourcesPackages();
            }
        }
        SystemUtils.setProperty(Configuration.open_persistence, this.openPersistence);
        SystemUtils.setProperty(Configuration.persistenceRunAsync, this.persistenceAsync);
        SystemUtils.setProperty(Configuration.persistence_path, this.persistencePath);
        SystemUtils.setProperty(Configuration.defaultExpireTime, this.defaultExpireTime);
        SystemUtils.setProperty(Configuration.defaultExpireTimeUnit, this.defaultExpireTimeUnit);
        SystemUtils.setProperty(Configuration.noPersistenceOfExpireTime, this.noPersistenceOfExpireTime);
        SystemUtils.setProperty(Configuration.noPersistenceOfExpireTimeUnit, this.noPersistenceOfExpireTimeUnit);
        SystemUtils.setProperty(Configuration.chooseClient, this.client.name());
        if (CollectionUtils.isNotEmpty(listeningRecoverySubPath)) {
            SystemUtils.setProperty(Configuration.listeningRecoverySubPath,
                    Configuration.ofListStrConfigurationWrapper(listeningRecoverySubPath, Function.identity()));
        }
        if (CollectionUtils.isNotEmpty(listeningRecoverySubClasses)) {
            SystemUtils.setProperty(Configuration.listeningRecoverySubClassNames,
                    Configuration.ofListStrConfigurationWrapper(listeningRecoverySubClasses, Class::getName));
        }
    }
}
