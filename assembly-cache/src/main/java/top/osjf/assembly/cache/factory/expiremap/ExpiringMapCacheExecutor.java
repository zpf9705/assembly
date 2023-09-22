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
     * Execute on {@link net.jodah.expiringmap.ExpiringMap#put(Object, Object)}.
     *
     * @param key   must not be {@literal null}
     * @param value must not be {@literal null}
     * @return Returns result value.
     */
    Boolean put(byte[] key, byte[] value);

    /**
     * Execute on {@link net.jodah.expiringmap.ExpiringMap#put(Object, Object, long, TimeUnit)}}.
     *
     * @param key      must not be {@literal null}
     * @param value    must not be {@literal null}
     * @param duration must not be {@literal null}
     * @param unit     must not be {@literal null}
     * @return Returns result value.
     */
    Boolean putDuration(byte[] key, byte[] value, Long duration, TimeUnit unit);

    /**
     * Execute on {@link net.jodah.expiringmap.ExpiringMap#putIfAbsent(Object, Object)}}.
     *
     * @param key   must not be {@literal null}
     * @param value must not be {@literal null}
     * @return Returns result value.
     */
    Boolean putIfAbsent(byte[] key, byte[] value);

    /**
     * Execute on {@link net.jodah.expiringmap.ExpiringMap#put(Object, Object, long, TimeUnit)}}.
     *
     * @param key      must not be {@literal null}
     * @param value    must not be {@literal null}
     * @param duration must not be {@literal null}
     * @param unit     must not be {@literal null}
     * @return Returns result value.
     */
    Boolean putIfAbsentDuration(byte[] key, byte[] value, Long duration, TimeUnit unit);

    /**
     * Execute on {@link net.jodah.expiringmap.ExpiringMap#get(Object)}.
     *
     * @param key must not be {@literal null}
     * @return Returns result value.
     */
    byte[] getVal(byte[] key);

    /**
     * Execute on {@link net.jodah.expiringmap.ExpiringMap#get(Object)}.
     *
     * @param key must not be {@literal null}
     * @return Returns result value.
     */
    List<byte[]> getKeysByKeys(byte[] key);

    /**
     * Execute on {@link net.jodah.expiringmap.ExpiringMap#replace(Object, Object)}.
     *
     * @param key      must not be {@literal null}
     * @param newValue must not be {@literal null}
     * @return Returns result value.
     */
    byte[] replace(byte[] key, byte[] newValue);

    /**
     * Execute on {@link net.jodah.expiringmap.ExpiringMap#remove(Object)}.
     *
     * @param keys must not be {@literal null}
     * @return Returns result value.
     */
    @CanNull
    Long deleteReturnSuccessNum(byte[]... keys);

    /**
     * Execute on {@link net.jodah.expiringmap.ExpiringMap#remove(Object)}}.
     *
     * @param key must not be {@literal null}
     * @return Returns result value.
     */
    Map<byte[], byte[]> deleteSimilarKey(byte[] key);

    /**
     * Execute on {@link net.jodah.expiringmap.ExpiringMap#clear()}.
     *
     * @return Returns result value.
     */
    Boolean reboot();

    /**
     * Execute on {@link net.jodah.expiringmap.ExpiringMap#containsKey(Object)}.
     *
     * @param key must not be {@literal null}
     * @return Returns result value.
     */
    Boolean containsKey(byte[] key);

    /**
     * Execute on {@link net.jodah.expiringmap.ExpiringMap#containsValue(Object)}}.
     *
     * @param value must not be {@literal null}
     * @return contains result
     */
    Boolean containsValue(byte[] value);

    /**
     * Execute on {@link net.jodah.expiringmap.ExpiringMap#getExpiration(Object)}.
     *
     * @param key must not be {@literal null}
     * @return Returns result value.
     */
    Long getExpirationWithKey(byte[] key);

    /**
     * Execute on {@link net.jodah.expiringmap.ExpiringMap#getExpiration(Object)}}.
     *
     * @param key  must not be {@literal null}
     * @param unit must not be {@literal null}
     * @return Returns result value.
     */
    Long getExpirationWithUnit(byte[] key, TimeUnit unit);

    /**
     * Execute on {@link net.jodah.expiringmap.ExpiringMap#getExpectedExpiration(Object)}}.
     *
     * @param key must not be {@literal null}
     * @return Returns result value.
     */
    Long getExpectedExpirationWithKey(byte[] key);

    /**
     * Execute on {@link net.jodah.expiringmap.ExpiringMap#getExpectedExpiration(Object)}}.
     *
     * @param key  must not be {@literal null}
     * @param unit must not be {@literal null}
     * @return Returns result value.
     */
    Long getExpectedExpirationWithUnit(byte[] key, TimeUnit unit);

    /**
     * Execute on {@link net.jodah.expiringmap.ExpiringMap#setExpiration(Object, long, TimeUnit)}.
     *
     * @param key      must not be {@literal null}
     * @param duration must not be {@literal null}
     * @param timeUnit must not be {@literal null}
     * @return Returns result value.
     */
    Boolean setExpirationDuration(byte[] key, Long duration, TimeUnit timeUnit);

    /**
     * Execute on {@link net.jodah.expiringmap.ExpiringMap#resetExpiration(Object)}.
     *
     * @param key must not be {@literal null}
     * @return Returns result value.
     */
    Boolean resetExpirationWithKey(byte[] key);
}
