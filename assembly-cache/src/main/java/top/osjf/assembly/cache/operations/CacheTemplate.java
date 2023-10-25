package top.osjf.assembly.cache.operations;

import top.osjf.assembly.cache.command.CacheKeyCommands;
import top.osjf.assembly.cache.factory.CacheFactory;
import top.osjf.assembly.cache.factory.CacheFactoryAccessor;
import top.osjf.assembly.cache.serializer.PairSerializer;
import top.osjf.assembly.cache.serializer.StringPairSerializer;
import top.osjf.assembly.util.annotation.CanNull;
import top.osjf.assembly.util.annotation.NotNull;
import top.osjf.assembly.util.lang.Asserts;
import top.osjf.assembly.util.lang.CollectionUtils;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Helper class that simplifies Cache data access code.
 * <p>
 * Performs automatic serialization/deserialization between the given objects
 * and the underlying binary data in the expiry store. By default, it uses
 * Generic String serialization for its objects(through {@link StringPairSerializer}).
 * For String intensive operations consider the dedicated {@link StringCacheTemplate}.
 * <p>
 * The Expiry of the template model , imitate expireTemplate encapsulation mode
 * The cache operation way to connect assembly simulation for the executor,
 * and is equipped with a variety of executor factory
 * <p>
 * When the configuration is completed of this class are thread safe operation.
 * <p>
 * <b>his is the central class in Expiry support</b>
 * @param <K> The type of key.
 * @param <V> The type of value.
 * @author zpf
 * @since 1.0.0
 **/
public class CacheTemplate<K, V> extends CacheFactoryAccessor implements CacheCommonsOperations<K, V>, Serializable {

    private static final long serialVersionUID = -8020854200126293536L;

    private PairSerializer<K> keySerialize;

    private PairSerializer<V> valueSerialize;

    private final ValueOperations<K, V> valueOperations = new DefaultValueOperations<>(this);

    private final TimeOperations<K, V> timeOperations = new DefaultTimeOperations<>(this);

    public CacheTemplate() {
        this(null);
    }

    public CacheTemplate(CacheFactory cacheFactory) {
        this(cacheFactory, null, null);
    }

    @SuppressWarnings("unchecked")
    public CacheTemplate(CacheFactory cacheFactory,
                         PairSerializer<K> keySerialize,
                         PairSerializer<V> valueSerialize) {
        super(cacheFactory);
        //if null refer to string
        @SuppressWarnings("rawtypes")
        PairSerializer defaultSerializer = new StringPairSerializer();
        if (keySerialize == null) {
            this.keySerialize = defaultSerializer;
        } else {
            this.keySerialize = keySerialize;
        }
        if (valueSerialize == null) {
            this.valueSerialize = defaultSerializer;
        } else {
            this.valueSerialize = valueSerialize;
        }
    }

    /**
     * Set the template key PairSerializer.
     *
     * @param keySerializer key Serializer.
     */
    public void setKeySerializer(PairSerializer<K> keySerializer) {
        Asserts.notNull(keySerializer,
                "Please provide a non null key serialization.");
        this.keySerialize = keySerializer;
    }

    /**
     * Set the template value PairSerializer.
     *
     * @param valueSerializer value Serializer.
     */
    public void setValueSerializer(PairSerializer<V> valueSerializer) {
        Asserts.notNull(valueSerializer,
                "Please provide a non null value serialization.");
        this.valueSerialize = valueSerializer;
    }

    @Override
    @NotNull
    public PairSerializer<K> getKeySerializer() {
        return this.keySerialize;
    }

    @Override
    @NotNull
    public PairSerializer<V> getValueSerializer() {
        return this.valueSerialize;
    }

    @Override
    public ValueOperations<K, V> opsForValue() {
        return this.valueOperations;
    }

    @Override
    public TimeOperations<K, V> opsForTime() {
        return this.timeOperations;
    }


    @Override
    @CanNull
    public <T> T execute(CacheValueCallback<T> action) {

        CacheFactory factory = getCacheFactory();

        Asserts.notNull(factory, "There are no available cache factories.");

        return action.doInExecutor(factory.executor());
    }

    @CanNull
    @Override
    public Boolean delete(K key) {
        Long result = execute((executor) -> executor.delete(
                this.rawKey(key)
        ));
        return result != null && result.intValue() == 1;
    }

    @CanNull
    @Override
    public Long delete(Collection<K> keys) {
        if (CollectionUtils.isEmpty(keys)) {
            return 0L;
        }
        return this.execute((executor) -> executor.delete(
                this.rawKeys(keys)
        ));
    }

    @Override
    public Map<K, V> deleteType(K key) {

        Map<byte[], byte[]> map = this.execute((executor) -> executor.deleteType(
                this.rawKey(key)
        ));

        if (map == null || map.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<K, V> ma = new HashMap<>();
        for (byte[] keyBytes : map.keySet()) {
            byte[] valueBytes = map.get(keyBytes);
            ma.put(this.keySerialize.deserialize(keyBytes), this.valueSerialize.deserialize(valueBytes));
        }
        return ma;
    }

    @Override
    public Boolean deleteAll() {
        return this.execute(CacheKeyCommands::deleteAll);
    }

    @Override
    public Boolean exist(K key) {
        return this.execute((executor) -> executor.hasKey(
                this.rawKey(key)
        ));
    }

    private byte[] rawKey(K key) {
        Asserts.notNull(key, "Non null key required");
        byte[] v;
        if (this.keySerialize != null) {
            v = this.keySerialize.serialize(key);
        } else {
            if (key instanceof byte[]) {
                return (byte[]) key;
            } else {
                v = null;
            }
        }
        return v;
    }

    private byte[][] rawKeys(Collection<K> keys) {
        final byte[][] rawKeys = new byte[keys.size()][];
        int i = 0;
        for (K key : keys) {
            rawKeys[i++] = rawKey(key);
        }
        return rawKeys;
    }

    private byte[] rawValue(V value) {
        Asserts.notNull(value, "Non null value required");
        byte[] v;
        if (this.valueSerialize != null) {
            v = this.valueSerialize.serialize(value);
        } else {
            if (value instanceof byte[]) {
                v = (byte[]) value;
            } else {
                v = null;
            }
        }
        return v;
    }
}
