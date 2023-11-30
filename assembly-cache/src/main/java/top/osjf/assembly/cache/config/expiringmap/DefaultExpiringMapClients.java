package top.osjf.assembly.cache.config.expiringmap;

import net.jodah.expiringmap.ExpirationPolicy;

import java.util.concurrent.TimeUnit;

/**
 * Default implementation for {@link ExpiringMapClients}.
 *
 * @author zpf
 * @since 1.0.0
 */
public class DefaultExpiringMapClients implements ExpiringMapClients {

    private final Integer maxSize;
    private final Long defaultExpireTime;
    private final TimeUnit defaultExpireTimeUnit;
    private final ExpirationPolicy expirationPolicy;

    public DefaultExpiringMapClients(Integer maxSize,
                                     Long defaultExpireTime,
                                     TimeUnit defaultExpireTimeUnit,
                                     ExpirationPolicy expirationPolicy
    ) {
        this.maxSize = maxSize;
        this.defaultExpireTime = defaultExpireTime;
        this.defaultExpireTimeUnit = defaultExpireTimeUnit;
        this.expirationPolicy = expirationPolicy;
    }

    /*
     * (non-Javadoc)
     * @see io.github.zpf9705.expiring.connection.expiremap.ExpireMapClientConfiguration#getMaxSize()
     */
    @Override
    public Integer getMaxSize() {
        return this.maxSize;
    }

    /*
     * (non-Javadoc)
     * @see io.github.zpf9705.expiring.connection.expiremap.ExpireMapClientConfiguration#getDefaultExpireTime()
     */
    @Override
    public Long getDefaultExpireTime() {
        return this.defaultExpireTime;
    }

    /*
     * (non-Javadoc)
     * @see io.github.zpf9705.expiring.connection.expiremap.ExpireMapClientConfiguration#getDefaultExpireTimeUnit()
     */
    @Override
    public TimeUnit getDefaultExpireTimeUnit() {
        return this.defaultExpireTimeUnit;
    }

    /*
     * (non-Javadoc)
     * @see io.github.zpf9705.expiring.connection.expiremap.ExpireMapClientConfiguration#getExpirationPolicy()
     */
    @Override
    public ExpirationPolicy getExpirationPolicy() {
        return this.expirationPolicy;
    }
}
