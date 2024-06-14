package top.osjf.assembly.cache.persistence;

import top.osjf.assembly.cache.serializer.PairSerializer;
import top.osjf.assembly.util.annotation.CanNull;
import top.osjf.assembly.util.annotation.NotNull;
import top.osjf.assembly.util.data.ByteIdentify;
import top.osjf.assembly.util.data.ComparableBool;
import top.osjf.assembly.util.data.Identify;
import top.osjf.assembly.util.data.ObjectIdentify;
import top.osjf.assembly.util.lang.StringUtils;

/**
 * Generate different {@link Identify} proxies based on the data
 * type to manage persistent caching.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.1.4
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class CachePersistenceKeyIdentify<T> implements ComparableBool<CachePersistenceKeyIdentify<T>> {

    private final Identify identify;

    /**
     * The construction method of using a single data to create an identity
     * object for a key is applicable during runtime.
     *
     * @param data single data.
     */
    public CachePersistenceKeyIdentify(T data) {
        this(data, CachePersistenceThreadLocal.getKeyPairSerializerName());
    }

    /**
     * A construction method for constructing identity objects using the
     * serialized object names of individual data and keys, suitable for
     * initialization.
     *
     * @param data                  single data.
     * @param keyPairSerializerName serialized object names
     */
    public CachePersistenceKeyIdentify(T data, @CanNull String keyPairSerializerName) {
        this(data, StringUtils.isNotBlank(keyPairSerializerName) ?
                        AbstractCachePersistence.getPairSerializerByName(keyPairSerializerName)
                        : null);
    }

    /**
     * The construction method of constructing the identity object of a key
     * using a single data and specific serialized objects, with universal
     * processing.
     *
     * @param data              single data.
     * @param keyPairSerializer key PairSerializer.
     */
    public CachePersistenceKeyIdentify(T data, @CanNull PairSerializer<Object> keyPairSerializer) {
        if (data instanceof byte[]) {
            identify = new ByteIdentify((byte[]) data);
        } else {
            identify = new ObjectIdentify<>(data);
        }
        if (keyPairSerializer != null) {
            identify.setSerializeFc(keyPairSerializer::serialize);
            if (identify instanceof ByteIdentify) {
                ((ByteIdentify) identify).setDeserializeFc(keyPairSerializer::deserialize);
            }
        }
    }

    @Override
    public int hashCode() {
        return identify.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof CachePersistenceKeyIdentify)) {
            return false;
        }
        return this.hashCode() == obj.hashCode();
    }

    public Identify getIdentify() {
        return identify;
    }

    @Override
    public int compareTo(@NotNull CachePersistenceKeyIdentify<T> o) {
        return identify.compareTo(o.getIdentify());
    }
}
