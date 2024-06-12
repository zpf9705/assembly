package top.osjf.assembly.cache.listener;

import top.osjf.assembly.cache.serializer.CacheByteIdentify;
import top.osjf.assembly.util.annotation.NotNull;
import top.osjf.assembly.util.data.ByteIdentify;
import top.osjf.assembly.util.serial.SerialUtils;

import java.io.Serializable;

/**
 * When the expiration callback occurs, the relevant
 * information of the key/value is stored.
 *
 * @author zpf
 * @since 1.0.8
 */
public final class DefaultMessage implements ByteMessage, ObjectMessage, Serializable {

    private static final long serialVersionUID = 4530419948323277941L;

    private final byte[] key;

    private final byte[] value;

    private final Object keySerialize;

    private final Object valueSerialize;

    public DefaultMessage(ByteIdentify key, ByteIdentify value) {
        this.key = key.getData();
        this.value = value.getData();
        if (key instanceof CacheByteIdentify) {
            this.keySerialize = ((CacheByteIdentify) key).getPairSerializer().deserialize(this.key);
        } else this.keySerialize = SerialUtils.deserialize(this.key);
        if (key instanceof CacheByteIdentify) {
            this.valueSerialize = ((CacheByteIdentify) key).getPairSerializer().deserialize(this.value);
        } else this.valueSerialize = SerialUtils.deserialize(this.value);

    }

    @Override
    @NotNull
    public byte[] getByteKey() {
        return key;
    }

    @Override
    @NotNull
    public byte[] getByteValue() {
        return value;
    }

    @Override
    @NotNull
    public Object getKey() {
        return keySerialize;
    }

    @Override
    @NotNull
    public Object getValue() {
        return valueSerialize;
    }
}
