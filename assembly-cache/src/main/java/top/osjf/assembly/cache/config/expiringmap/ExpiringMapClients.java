package top.osjf.assembly.cache.config.expiringmap;

import net.jodah.expiringmap.ExpirationPolicy;
import net.jodah.expiringmap.ExpiringMap;
import top.osjf.assembly.util.annotation.CanNull;
import top.osjf.assembly.util.lang.Asserts;

import java.util.concurrent.TimeUnit;

/**
 * Here is about {@link ExpiringMap} client configuration interface.
 * <p>
 * Provides the function of the configuration and obtain.
 * <p>
 * Providing optional elements allows a more specific configuration
 * of the client.
 *
 * @author zpf
 * @since 1.0.0
 */
public interface ExpiringMapClients {

    /**
     * Obtain the maximum number of additions for {@link ExpiringMap}
     *
     * @return {@literal Integer} map max save size.
     */
    Integer getMaxSize();

    /**
     * Obtain the default cache time for {@link ExpiringMap}.
     *
     * @return {@literal Long} default expire time.
     */
    Long getDefaultExpireTime();

    /**
     * Obtain the default cache time unit of {@link #getDefaultExpireTime()}.
     *
     * @return {@literal TimeUnit} default expire time unit.
     */
    TimeUnit getDefaultExpireTimeUnit();

    /**
     * For specific explanations, please refer to {@link ExpirationPolicy}.
     *
     * @return {@literal ExpirationPolicy} default ExpirationPolicy.
     */
    ExpirationPolicy getExpirationPolicy();

    /**
     * Create a new {@link ExpiringMapClientsBuilder} to build {@link ExpiringMapClients} to be used.
     *
     * @return a new {@link ExpiringMapClientsBuilder} to build {@link ExpiringMapClients}.
     */
    static ExpiringMapClientsBuilder builder() {
        return new ExpiringMapClientsBuilder();
    }

    /**
     * Create a default new {@link ExpiringMapClientsBuilder} to build {@link ExpiringMapClients}
     * <dl>
     *     <dt>max_size</dt>
     *     <dd>20*50</dd>
     *     <dt>expire_time</dt>
     *     <dd>30L</dd>
     *     <dt>expire_time_unit</dt>
     *     <dd>TimeUnit.SECONDS</dd>
     *     <dt>ExpirationPolicy</dt>
     *     <dd>ACCESSED</dd>
     * </dl>
     *
     * @return a {@link ExpiringMapClients} with defaults.
     */
    static ExpiringMapClients defaultConfiguration() {
        return builder().build();
    }

    /**
     * Simple Construction Class of {@link ExpiringMapClients}
     */
    class ExpiringMapClientsBuilder {

        @CanNull
        Integer maxSize;
        @CanNull
        Long defaultExpireTime;
        @CanNull
        TimeUnit defaultExpireTimeUnit;
        @CanNull
        ExpirationPolicy expirationPolicy;
        static final Integer DEFAULT_MAX_SIZE = 20 * 50;
        static final Long DEFAULT_EXPIRE_TIME = 30L;
        static final TimeUnit DEFAULT_EXPIRE_TIME_UNIT = TimeUnit.SECONDS;
        static final ExpirationPolicy DEFAULT_EXPIRATION_POLICY = ExpirationPolicy.CREATED;

        ExpiringMapClientsBuilder() {
        }

        /**
         * Given the map one of the biggest capacity.
         *
         * @param maxSize The maximum capacity.
         * @return {@link ExpiringMapClientsBuilder}.
         */
        public ExpiringMapClientsBuilder acquireMaxSize(Integer maxSize) {
            Asserts.isTrue(this.maxSize == null,
                    "MaxSize existing configuration values, please do not cover");
            this.maxSize = maxSize;
            return this;
        }

        /**
         * Given the map of a default cache expiration time.
         *
         * @param defaultExpireTime The default cache expiration time.
         * @return {@link ExpiringMapClientsBuilder}.
         */
        public ExpiringMapClientsBuilder acquireDefaultExpireTime(Long defaultExpireTime) {
            Asserts.isTrue(this.defaultExpireTime == null,
                    "DefaultExpireTime existing configuration values, please do not cover");
            this.defaultExpireTime = defaultExpireTime;
            return this;
        }

        /**
         * Given the map of a default cache expiration time units.
         *
         * @param defaultExpireTimeUnit The default cache expiration time units.
         * @return {@link ExpiringMapClientsBuilder}.
         */
        public ExpiringMapClientsBuilder acquireDefaultExpireTimeUnit(TimeUnit defaultExpireTimeUnit) {
            Asserts.isTrue(this.defaultExpireTimeUnit == null,
                    "DefaultExpireTimeUnit existing configuration values, please do not cover");
            this.defaultExpireTimeUnit = defaultExpireTimeUnit;
            return this;
        }

        /**
         * Given the map of a default cache expiration expired strategy.
         *
         * @param expirationPolicy The default cache expiration expired strategy.
         * @return {@link ExpiringMapClientsBuilder}.
         */
        public ExpiringMapClientsBuilder acquireDefaultExpirationPolicy(ExpirationPolicy expirationPolicy) {
            Asserts.isTrue(this.expirationPolicy == null,
                    "ExpirationPolicy existing configuration values, please do not cover");
            this.expirationPolicy = expirationPolicy;
            return this;
        }

        /**
         * Build the {@link ExpiringMapClients} with the configuration applied from this builder.
         *
         * @return a new {@link ExpiringMapClients} implementation.
         */
        public DefaultExpiringMapClients build() {
            if (this.maxSize == null || this.maxSize == 0) {
                this.maxSize = DEFAULT_MAX_SIZE;
            }
            if (this.defaultExpireTime == null || this.defaultExpireTime == 0L) {
                this.defaultExpireTime = DEFAULT_EXPIRE_TIME;
            }
            if (this.defaultExpireTimeUnit == null) {
                this.defaultExpireTimeUnit = DEFAULT_EXPIRE_TIME_UNIT;
            }
            if (this.expirationPolicy == null) {
                this.expirationPolicy = DEFAULT_EXPIRATION_POLICY;
            }
            return new DefaultExpiringMapClients(
                    this.maxSize,
                    this.defaultExpireTime,
                    this.defaultExpireTimeUnit,
                    this.expirationPolicy
            );
        }
    }
}
