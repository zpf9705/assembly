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

    private final byte[] bytesKey;

    private final byte[] bytesValue;

    private final Object objKey;

    private final Object objValue;

    public DefaultMessage(ByteIdentify key, ByteIdentify value) {
        bytesKey = key.getData();
        bytesValue = value.getData();
        objKey = ifDeserialize(key);
        objValue = ifDeserialize(value);
    }

    /**
     * Based on the type of {@link ByteIdentify}, clarify whether
     * there is a specified deserialization type available for use,
     * and use {@link SerialUtils#deserialize(byte[])} by default.
     *
     * @param identify Identity of type byte [].
     * @return Deserialized object.
     */
    Object ifDeserialize(ByteIdentify identify) {
        byte[] data = identify.getData();
        if (identify instanceof CacheByteIdentify)
            return ((CacheByteIdentify) identify).getPairSerializer().deserialize(data);
        return SerialUtils.deserialize(data);
    }

    @Override
    @NotNull
    public byte[] getByteKey() {
        return bytesKey;
    }

    @Override
    @NotNull
    public byte[] getByteValue() {
        return bytesValue;
    }

    @Override
    @NotNull
    public Object getKey() {
        return objKey;
    }

    @Override
    @NotNull
    public Object getValue() {
        return objValue;
    }
}
