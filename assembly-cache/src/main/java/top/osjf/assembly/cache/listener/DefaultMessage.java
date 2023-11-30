package top.osjf.assembly.cache.listener;

import top.osjf.assembly.util.annotation.NotNull;
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

    public DefaultMessage(byte[] key, byte[] value) {
        this.key = key;
        this.value = value;
        this.keySerialize = SerialUtils.deserialize(key);
        this.valueSerialize = SerialUtils.deserialize(value);
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
