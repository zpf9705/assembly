package top.osjf.assembly.cache.core.serializer;

import top.osjf.assembly.cache.util.AssertUtils;
import top.osjf.assembly.cache.util.TypeUtils;
import top.osjf.assembly.util.SerialUtils;

/**
 * Key/value pairs serialized adapter , Direct implementation {@link ExpiringSerializer}
 *
 * @author zpf
 * @since 1.1.0
 **/
public class ExpiringSerializerAdapter<T> implements ExpiringSerializer<T> {

    private final Class<T> type;

    public ExpiringSerializerAdapter(Class<T> type) {
        this.type = type;
    }

    @Override
    public byte[] serialize(T t) {
        byte[] serialize = SerialUtils.serialize(t);
        AssertUtils.Operation.notNull(serialize,
                "serialize failed ! t serialize is not null " + t);
        return serialize;
    }

    @Override
    public T deserialize(byte[] bytes) {
        T t = null;
        Object deserialize = SerialUtils.deserialize(bytes);
        if (deserialize != null) {
            t = TypeUtils.convert(deserialize, this.type);
        }
        return t;
    }

    @Override
    public Class<T> serializerType() {
        return this.type;
    }
}
