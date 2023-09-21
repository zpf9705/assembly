package top.osjf.assembly.cache.listener;

import top.osjf.assembly.cache.exceptions.OperationsException;
import top.osjf.assembly.util.SerialUtils;
import top.osjf.assembly.util.annotation.NotNull;

/**
 * When the expiration callback occurs, the relevant information of the key/value is stored.
 *
 * @author zpf
 * @since 1.0.0
 */
public final class Message implements MessageCapable {

    private static final long serialVersionUID = -8830456426162230361L;

    private final byte[] key;

    private final byte[] value;

    private final Object keySerialize;

    private final Object valueSerialize;

    private Message(byte[] key, byte[] value) {
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

    /**
     * The key value deserialization.
     *
     * @param key   must not be {@literal null}
     * @param value must not be {@literal null}
     * @return {@link Message}
     */
    public static Message serial(@NotNull byte[] key, @NotNull byte[] value) {
        Message message;
        try {
            message = new Message(key, value);
        } catch (Throwable e) {
            throw new OperationsException("Message serial error with msg :[ " + e.getMessage() + " ]");
        }
        return message;
    }
}
