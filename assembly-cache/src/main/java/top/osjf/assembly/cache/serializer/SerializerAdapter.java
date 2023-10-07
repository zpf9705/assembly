package top.osjf.assembly.cache.serializer;

import cn.hutool.core.convert.Convert;
import top.osjf.assembly.util.serial.SerialUtils;

/**
 * Key/value pairs serialized adapter , direct implementation {@link PairSerializer}.
 *
 * @author zpf
 * @since 1.0.0
 **/
public class SerializerAdapter<T> implements PairSerializer<T> {

    private final Class<T> type;

    public SerializerAdapter(Class<T> type) {
        this.type = type;
    }

    @Override
    public byte[] serialize(T t) {
        return SerialUtils.serialize(t);
    }

    @Override
    public T deserialize(byte[] bytes) {
        T t = null;
        Object deserialize = SerialUtils.deserialize(bytes);
        if (deserialize != null) {
            t = Convert.convert(this.type, deserialize);
        }
        return t;
    }

    @Override
    public Class<T> serializerType() {
        return this.type;
    }
}
