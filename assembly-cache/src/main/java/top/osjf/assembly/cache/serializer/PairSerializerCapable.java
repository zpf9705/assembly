package top.osjf.assembly.cache.serializer;

/**
 * Retrieve serialization and deserialization methods for caching key values.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.1.0
 */
public interface PairSerializerCapable<K, V> {

    /**
     * Return a {@code key} about {@link PairSerializer}.
     * @return {@code key} about {@link PairSerializer}
     */
    PairSerializer<K> getKeyPairSerializer();

    /**
     * Return a {@code value} about {@link PairSerializer}.
     * @return {@code value} about {@link PairSerializer}
     */
    PairSerializer<V> getValuePairSerializer();
}
