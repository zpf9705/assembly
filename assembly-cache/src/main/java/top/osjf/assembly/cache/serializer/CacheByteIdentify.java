package top.osjf.assembly.cache.serializer;

import top.osjf.assembly.cache.persistence.AbstractCachePersistence;
import top.osjf.assembly.util.data.ByteIdentify;

/**
 * Carry {@link PairSerializer} to deserialize the value when the cache expires.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.1.4
 */
@SuppressWarnings("rawtypes")
public class CacheByteIdentify extends ByteIdentify {

    private static final long serialVersionUID = 1227919046430145466L;

    private final String pairSerializerName;

    @SuppressWarnings("unchecked")
    public CacheByteIdentify(byte[] var, String pairSerializerName) {
        super(var);
        this.pairSerializerName = pairSerializerName;
        PairSerializer pairSerializer = getPairSerializer();
        setSerializeFc(pairSerializer::serialize);
        setDeserializeFc(pairSerializer::deserialize);
    }

    public PairSerializer getPairSerializer() {
        return AbstractCachePersistence.getPairSerializerByName(pairSerializerName);
    }
}
