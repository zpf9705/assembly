package top.osjf.assembly.cache.persistence;

import top.osjf.assembly.cache.serializer.PairSerializer;
import top.osjf.assembly.util.annotation.NotNull;
import top.osjf.assembly.util.data.ByteIdentify;
import top.osjf.assembly.util.data.ComparableBool;
import top.osjf.assembly.util.data.Identify;
import top.osjf.assembly.util.data.ObjectIdentify;

import java.util.Objects;

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

    public CachePersistenceKeyIdentify(T data) {
        PairSerializer<Object> pairSerializer = AbstractCachePersistence.getPairSerializerByName(
                CachePersistenceThreadLocal.getKeyPairSerializerName());
        Objects.requireNonNull(pairSerializer, "keyPairSerializer");
        if (data instanceof byte[]) {
            identify = new ByteIdentify((byte[]) data);
        } else {
            identify = new ObjectIdentify<>(data);
        }
        identify.setSerializeFc(pairSerializer::serialize);
        if (identify instanceof ByteIdentify) {
            ((ByteIdentify) identify).setDeserializeFc(pairSerializer::deserialize);
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
