package top.osjf.assembly.cache.operations;

import top.osjf.assembly.cache.factory.CacheExecutor;
import top.osjf.assembly.util.serial.SerialUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * The default value operations implementation class for {@link ValueOperations}.
 * @param <K> The type of key.
 * @param <V> The type of value.
 * @author zpf
 * @since 1.0.0
 */
public class DefaultValueOperations<K, V> extends AbstractOperations<K, V> implements ValueOperations<K, V> {

    public DefaultValueOperations(CacheTemplate<K, V> template) {
        super(template);
    }

    /*
     * (non-Javadoc)
     * @see io.github.zpf9705.expiring.core.ValueOperations#set(Object, Object)
     */
    public void set(K key, V value) {

        final byte[] rawValue = this.rawValue(value);
        this.execute(new AbstractOperations<K, V>.ValueDeserializingCacheCallback(key) {
            @Override
            protected byte[] inExecutor(byte[] rawKey, CacheExecutor executor) {
                executor.set(rawKey, rawValue);
                return null;
            }
        });
    }

    /*
     * (non-Javadoc)
     * @see io.github.zpf9705.expiring.core.ValueOperations#set(Object, Object, Long, TimeUnit)
     */
    public void set(K key, V value, Long duration, TimeUnit unit) {

        final byte[] rawValue = this.rawValue(value);
        this.execute(new AbstractOperations<K, V>.ValueDeserializingCacheCallback(key) {
            @Override
            protected byte[] inExecutor(byte[] rawKey, CacheExecutor executor) {
                executor.setE(rawKey, rawValue, duration, unit);
                return null;
            }
        });
    }

    /*
     * (non-Javadoc)
     * @see io.github.zpf9705.expiring.core.ValueOperations#setIfAbsent(Object, Object)
     */
    @Override
    public Boolean setIfAbsent(K key, V value) {
        byte[] rawKey = this.rawKey(key);
        byte[] rawValue = this.rawValue(value);
        return this.execute((executor) -> executor.setNX(rawKey, rawValue));
    }

    /*
     * (non-Javadoc)
     * @see io.github.zpf9705.expiring.core.ValueOperations#setIfAbsent(Object, Object, Long, TimeUnit)
     */
    @Override
    public Boolean setIfAbsent(K key, V value, Long duration, TimeUnit unit) {

        byte[] rawKey = this.rawKey(key);
        byte[] rawValue = this.rawValue(value);
        return this.execute((executor) -> executor.setEX(rawKey, rawValue, duration, unit));
    }

    /*
     * (non-Javadoc)
     * @see io.github.zpf9705.expiring.core.ValueOperations#get(Object)
     */
    public V get(K key) {

        return this.execute(new ValueDeserializingCacheCallback(key) {
            @Override
            protected byte[] inExecutor(byte[] rawKey, CacheExecutor executor) {
                return executor.get(rawKey);
            }
        });
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<K> getSimilarKeys(K key) {

        byte[] rawKey = this.rawKey(key);
        List<byte[]> execute = this.execute((executor) -> executor.getSimilarKeys(rawKey));
        return (List<K>) SerialUtils.deserializeAny(execute);
    }

    /*
     * (non-Javadoc)
     * @see io.github.zpf9705.expiring.core.ValueOperations#getAndSet(Object, Object)
     */
    @Override
    public V getAndSet(K key, V newValue) {

        final byte[] rawValue = this.rawValue(newValue);
        return this.execute(new ValueDeserializingCacheCallback(key) {
            @Override
            protected byte[] inExecutor(byte[] rawKey, CacheExecutor executor) {
                return executor.getAndSet(rawKey, rawValue);
            }
        });
    }
}
