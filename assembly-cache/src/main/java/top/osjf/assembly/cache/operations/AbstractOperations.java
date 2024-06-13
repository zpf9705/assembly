package top.osjf.assembly.cache.operations;

import top.osjf.assembly.cache.factory.CacheExecutor;
import top.osjf.assembly.cache.serializer.PairSerializer;
import top.osjf.assembly.util.annotation.CanNull;
import top.osjf.assembly.util.lang.Asserts;

/**
 * In order to be compatible with all data types related to the Java language,
 * this abstract class uniformly converts data into byte arrays and passes
 * them to the cache center.
 *
 * <p>Introducing intermediate abstraction {@link ValueDeserializingCacheCallback},
 * as not all methods require serialization of {@code Value} values, only {@code Key}
 * serialization is listed here.
 *
 * <p>If there is a serialization requirement for {@code Value}, {@link #rawValue(Object)}
 * can be called separately.
 * @param <K> The type of key.
 * @param <V> The type of value.
 * @author zpf
 * @since 1.0.0
 **/
public abstract class AbstractOperations<K, V> {

    abstract class ValueDeserializingCacheCallback implements CacheValueCallback<V> {

        private final K key;

        public ValueDeserializingCacheCallback(K key) {
            this.key = key;
        }

        @Override
        public V doInExecutor(CacheExecutor executor) {
            /*
             * How to have a special transformation of key/value demand, can be operated in this department
             */
            byte[] bytes = inExecutor(rawKey(this.key), executor);
            return deserializeValue(bytes);
        }

        @CanNull
        protected abstract byte[] inExecutor(byte[] rawKey, CacheExecutor helper);
    }

    final CacheTemplate<K, V> template;

    AbstractOperations(CacheTemplate<K, V> template) {
        this.template = template;
    }

    @CanNull
    <T> T execute(CacheValueCallback<T> callback) {
        return template.execute(callback);
    }

    PairSerializer<K> keySerializer() {
        return this.template.getKeySerializer();
    }

    PairSerializer<V> valueSerializer() {
        return this.template.getValueSerializer();
    }

    public CacheCommonsOperations<K, V> getCommonsOperations() {
        return this.template;
    }

    byte[] rawKey(K key) {
        Asserts.notNull(key, "Non null key required");
        if (keySerializer() == null && key instanceof byte[]) {
            return (byte[]) key;
        }
        return keySerializer().serialize(key);
    }

    byte[] rawValue(V value) {
        Asserts.notNull(value, "Non null key required");
        if (valueSerializer() == null && value instanceof byte[]) {
            return (byte[]) value;
        }
        return valueSerializer().serialize(value);
    }

    K deserializeKey(byte[] key) {
        if (keySerializer() == null) {
            return (K) key;
        }
        return keySerializer().deserialize(key);
    }

    V deserializeValue(byte[] value) {
        if (valueSerializer() == null) {
            return (V) value;
        }
        return valueSerializer().deserialize(value);
    }
}
