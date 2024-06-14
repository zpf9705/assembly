package top.osjf.assembly.cache.autoconfigure;

import net.jodah.expiringmap.ExpirationPolicy;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import top.osjf.assembly.cache.config.Configuration;

/**
 * Configuration properties for assembly-cache.
 *
 * @author zpf
 * @since 1.0.0
 */
@ConfigurationProperties(prefix = "assembly.cache")
public class CacheProperties {

    /**
     * Cached public global configuration.
     */
    @NestedConfigurationProperty
    private Configuration globeConfiguration = new Configuration();

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

    public Configuration getGlobeConfiguration() {
        return globeConfiguration;
    }

    public void setGlobeConfiguration(Configuration globeConfiguration) {
        this.globeConfiguration = globeConfiguration;
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
         * <p>The default is {@code ExpirationPolicy.CREATED}.</p>
         */
        private ExpirationPolicy expirationPolicy = ExpirationPolicy.CREATED;

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
    }

    /**
     * The underlying support type for caching.
     */
    public enum Client {
        EXPIRE_MAP
    }
}
