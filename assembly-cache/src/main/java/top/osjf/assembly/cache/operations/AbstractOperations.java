package top.osjf.assembly.cache.operations;

import top.osjf.assembly.cache.factory.CacheExecutor;
import top.osjf.assembly.cache.serializer.PairSerializer;
import top.osjf.assembly.cache.util.AssertUtils;
import top.osjf.assembly.util.annotation.CanNull;

/**
 * This abstract class is mainly described {@link CacheTemplate} some behavioral methods.
 *
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
        AssertUtils.Operation.notNull(key, "Non null key required");
        if (keySerializer() == null && key instanceof byte[]) {
            return (byte[]) key;
        }
        return keySerializer().serialize(key);
    }

    byte[] rawValue(V value) {
        AssertUtils.Operation.notNull(value, "Non null key required");
        if (valueSerializer() == null && value instanceof byte[]) {
            return (byte[]) value;
        }
        return valueSerializer().serialize(value);
    }

    V deserializeValue(byte[] value) {
        if (valueSerializer() == null) {
            return (V) value;
        }
        return valueSerializer().deserialize(value);
    }
}
