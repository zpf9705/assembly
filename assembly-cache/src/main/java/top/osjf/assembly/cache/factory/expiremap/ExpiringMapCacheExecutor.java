package top.osjf.assembly.cache.factory.expiremap;

import top.osjf.assembly.cache.factory.CacheExecutor;
import top.osjf.assembly.util.annotation.CanNull;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * {@link CacheExecutor} for {@link net.jodah.expiringmap.ExpiringMap}.
 *
 * @author zpf
 * @since 1.0.0
 */
public interface ExpiringMapCacheExecutor extends CacheExecutor {

    /**
     * Proxy for {@link net.jodah.expiringmap.ExpiringMap#put(Object, Object)}
     *
     * @param key   must not be {@literal null}
     * @param value must not be {@literal null}
     * @return set result
     */
    Boolean put(byte[] key, byte[] value);

    /**
     * Proxy for {@link net.jodah.expiringmap.ExpiringMap#put(Object, Object, long, TimeUnit)}}
     *
     * @param key      must not be {@literal null}
     * @param value    must not be {@literal null}
     * @param duration must not be {@literal null}
     * @param unit     must not be {@literal null}
     * @return set result
     */
    Boolean putDuration(byte[] key, byte[] value, Long duration, TimeUnit unit);

    /**
     * Proxy for {@link net.jodah.expiringmap.ExpiringMap#putIfAbsent(Object, Object)}}
     *
     * @param key   must not be {@literal null}
     * @param value must not be {@literal null}
     * @return set result
     */
    Boolean putIfAbsent(byte[] key, byte[] value);

    /**
     * Proxy for {@link net.jodah.expiringmap.ExpiringMap#put(Object, Object, long, TimeUnit)}}
     *
     * @param key      must not be {@literal null}
     * @param value    must not be {@literal null}
     * @param duration must not be {@literal null}
     * @param unit     must not be {@literal null}
     * @return set result
     */
    Boolean putIfAbsentDuration(byte[] key, byte[] value, Long duration, TimeUnit unit);

    /**
     * Proxy for {@link net.jodah.expiringmap.ExpiringMap#get(Object)}
     *
     * @param key must not be {@literal null}
     * @return key in value
     */
    byte[] getVal(byte[] key);

    /**
     * Proxy for {@link net.jodah.expiringmap.ExpiringMap#get(Object)}
     *
     * @param key must not be {@literal null}
     * @return Similar keys with a key
     */
    List<byte[]> getKeysByKeys(byte[] key);

    /**
     * Proxy for {@link net.jodah.expiringmap.ExpiringMap#replace(Object, Object)}
     *
     * @param key      must not be {@literal null}
     * @param newValue must not be {@literal null}
     * @return The old value is replaced
     */
    byte[] replace(byte[] key, byte[] newValue);

    /**
     * Proxy for {@link net.jodah.expiringmap.ExpiringMap#remove(Object)}
     *
     * @param keys must not be {@literal null}
     * @return deleted number
     */
    @CanNull
    Long deleteReturnSuccessNum(byte[]... keys);

    /**
     * Proxy for {@link net.jodah.expiringmap.ExpiringMap#remove(Object)}}
     *
     * @param key must not be {@literal null}
     * @return removed key/value
     */
    Map<byte[], byte[]> deleteSimilarKey(byte[] key);

    /**
     * Proxy for {@link net.jodah.expiringmap.ExpiringMap#clear()}
     *
     * @return clear result
     */
    Boolean reboot();

    /**
     * Proxy for {@link net.jodah.expiringmap.ExpiringMap#containsKey(Object)}
     *
     * @param key must not be {@literal null}
     * @return contains result
     */
    Boolean containsKey(byte[] key);

    /**
     * Proxy for {@link net.jodah.expiringmap.ExpiringMap#containsValue(Object)}}
     *
     * @param value must not be {@literal null}
     * @return contains result
     */
    Boolean containsValue(byte[] value);

    /**
     * Proxy for {@link net.jodah.expiringmap.ExpiringMap#getExpiration(Object)}
     *
     * @param key must not be {@literal null}
     * @return Set the time with key
     */
    Long getExpirationWithKey(byte[] key);

    /**
     * Proxy for {@link net.jodah.expiringmap.ExpiringMap#getExpiration(Object)}}
     *
     * @param key  must not be {@literal null}
     * @param unit must not be {@literal null}
     * @return Set the time with unit
     */
    Long getExpirationWithUnit(byte[] key, TimeUnit unit);

    /**
     * Proxy for {@link net.jodah.expiringmap.ExpiringMap#getExpectedExpiration(Object)}}
     *
     * @param key must not be {@literal null}
     * @return For the rest with key
     */
    Long getExpectedExpirationWithKey(byte[] key);

    /**
     * Proxy for {@link net.jodah.expiringmap.ExpiringMap#getExpectedExpiration(Object)}}
     *
     * @param key  must not be {@literal null}
     * @param unit must not be {@literal null}
     * @return For the rest with unit
     */
    Long getExpectedExpirationWithUnit(byte[] key, TimeUnit unit);

    /**
     * Proxy for {@link net.jodah.expiringmap.ExpiringMap#setExpiration(Object, long, TimeUnit)}
     *
     * @param key      must not be {@literal null}
     * @param duration must not be {@literal null}
     * @param timeUnit must not be {@literal null}
     * @return replace duration result
     */
    Boolean setExpirationDuration(byte[] key, Long duration, TimeUnit timeUnit);

    /**
     * Proxy for {@link net.jodah.expiringmap.ExpiringMap#resetExpiration(Object)}
     *
     * @param key must not be {@literal null}
     * @return rest result
     */
    Boolean resetExpirationWithKey(byte[] key);
}
