package top.osjf.assembly.cache.command.expiremap;

import top.osjf.assembly.cache.factory.ExpiringMapCacheExecutor;
import top.osjf.assembly.cache.command.CacheKeyCommands;
import top.osjf.assembly.util.annotation.CanNull;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * About the {@code Key} within {@link net.jodah.expiringmap.ExpiringMap} operating instructions.
 *
 * @author zpf
 * @since 1.0.0
 */
public class ExpiringMapKeyCommands implements CacheKeyCommands {

    private final ExpiringMapCacheExecutor delegate;

    public ExpiringMapKeyCommands(ExpiringMapCacheExecutor delegate) {
        this.delegate = delegate;
    }

    /*
     * (non-Javadoc)
     * @see io.github.zpf9705.expiring.command.ExpireKeyCommands#delete(byte[]...)
     */
    @CanNull
    @Override
    public Long delete(byte[]... keys) {
        return this.delegate.deleteByKeys(keys);
    }

    /*
     * (non-Javadoc)
     * @see io.github.zpf9705.expiring.command.ExpireKeyCommands#deleteType(byte[]...)
     */
    @Override
    public Map<byte[], byte[]> deleteType(byte[] key) {
        return this.delegate.deleteSimilarKey(key);
    }

    /*
     * (non-Javadoc)
     * @see io.github.zpf9705.expiring.command.ExpireKeyCommands#deleteAll()
     */
    @Override
    public Boolean deleteAll() {
        return this.delegate.reboot();
    }

    /*
     * (non-Javadoc)
     * io.github.zpf9705.expiring.command.ExpireStringCommands#get(Object)
     */
    @Override
    public byte[] get(byte[] key) {
        return this.delegate.getVal(key);
    }

    /*
     * (non-Javadoc)
     * io.github.zpf9705.expiring.command.ExpireStringCommands#getSimilarKeys(Object)
     */
    @Override
    public List<byte[]> getSimilarKeys(byte[] rawKey) {
        return this.delegate.findSimilarKeys(rawKey);
    }

    /*
     * (non-Javadoc)
     * @see io.github.zpf9705.expiring.command.ExpireKeyCommands#hasKey(byte[])
     */
    @Override
    public Boolean hasKey(byte[] key) {
        return this.delegate.containsKey(key);
    }

    /*
     * (non-Javadoc)
     * @see io.github.zpf9705.expiring.command.ExpireKeyCommands#getExpiration(byte[])
     */
    @Override
    public Long getExpiration(byte[] key) {
        return this.delegate.getExpirationWithKey(key);
    }

    /*
     * (non-Javadoc)
     * @see io.github.zpf9705.expiring.command.ExpireKeyCommands#getExpiration(byte[], TimeUnit)
     */
    @Override
    public Long getExpiration(byte[] key, TimeUnit unit) {
        return this.delegate.getExpirationWithUnit(key, unit);
    }

    /*
     * (non-Javadoc)
     * @see io.github.zpf9705.expiring.command.ExpireKeyCommands#getExpectedExpiration(byte[])
     */
    @Override
    public Long getExpectedExpiration(byte[] key) {
        return this.delegate.getExpectedExpirationWithKey(key);
    }

    /*
     * (non-Javadoc)
     * @see io.github.zpf9705.expiring.command.ExpireKeyCommands#getExpectedExpiration(byte[], TimeUnit)
     */
    @Override
    public Long getExpectedExpiration(byte[] key, TimeUnit unit) {
        return this.delegate.getExpectedExpirationWithUnit(key, unit);
    }

    /*
     * (non-Javadoc)
     * @see io.github.zpf9705.expiring.command.ExpireKeyCommands#setExpiration(byte[], Long, TimeUnit)
     */
    @Override
    public Boolean setExpiration(byte[] key, Long duration, TimeUnit timeUnit) {
        return this.delegate.setExpirationDuration(key, duration, timeUnit);
    }

    /*
     * (non-Javadoc)
     * @see io.github.zpf9705.expiring.command.ExpireKeyCommands#resetExpiration(byte[])
     */
    @Override
    public Boolean resetExpiration(byte[] key) {
        return this.delegate.resetExpirationWithKey(key);
    }
}
