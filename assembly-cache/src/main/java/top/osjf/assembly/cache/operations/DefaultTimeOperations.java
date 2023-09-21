package top.osjf.assembly.cache.operations;

import java.util.concurrent.TimeUnit;

/**
 * The default value operations implementation class for {@link TimeOperations}.
 *
 * @author zpf
 * @since 1.0.0
 */
public class DefaultTimeOperations<K, V> extends AbstractOperations<K, V> implements TimeOperations<K, V> {

    DefaultTimeOperations(CacheTemplate<K, V> expireTemplate) {
        super(expireTemplate);
    }

    /*
     * (non-Javadoc)
     * @see io.github.zpf9705.expiring.core.ExpirationOperations#getExpiration(Object)
     */
    @Override
    public Long getExpiration(K key) {
        byte[] rawKey = this.rawKey(key);
        return this.execute((executor) -> executor.getExpiration(rawKey));
    }

    /*
     * (non-Javadoc)
     * @see io.github.zpf9705.expiring.core.ExpirationOperations#getExpectedExpiration(Object, TimeUnit)
     */
    @Override
    public Long getExpiration(K key, TimeUnit unit) {
        byte[] rawKey = this.rawKey(key);
        return this.execute((executor) -> executor.getExpiration(rawKey, unit));
    }

    /*
     * (non-Javadoc)
     * @see io.github.zpf9705.expiring.core.ExpirationOperations#getExpectedExpiration(Object)
     */
    @Override
    public Long getExpectedExpiration(K key) {
        byte[] rawKey = this.rawKey(key);
        return this.execute((executor) -> executor.getExpectedExpiration(rawKey));
    }

    /*
     * (non-Javadoc)
     * @see io.github.zpf9705.expiring.core.ExpirationOperations#getExpectedExpiration(Object, TimeUnit)
     */
    @Override
    public Long getExpectedExpiration(K key, TimeUnit unit) {
        byte[] rawKey = this.rawKey(key);
        return this.execute((executor) -> executor.getExpectedExpiration(rawKey, unit));
    }

    /*
     * (non-Javadoc)
     * @see io.github.zpf9705.expiring.core.ExpirationOperations#setExpiration(Object, Long, TimeUnit)
     */
    @Override
    public Boolean setExpiration(K key, Long duration, TimeUnit unit) {
        byte[] rawKey = this.rawKey(key);
        return this.execute((executor) -> executor.setExpiration(rawKey, duration, unit));
    }

    /*
     * (non-Javadoc)
     * @see io.github.zpf9705.expiring.core.ExpirationOperations#resetExpiration(java.lang.Object)
     */
    @Override
    public Boolean resetExpiration(K key) {
        byte[] rawKey = this.rawKey(key);
        return this.execute((executor) -> executor.resetExpiration(rawKey));
    }
}
