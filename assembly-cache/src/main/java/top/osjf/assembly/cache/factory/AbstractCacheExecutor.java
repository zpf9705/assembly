package top.osjf.assembly.cache.factory;

import top.osjf.assembly.cache.serializer.CacheByteIdentify;
import top.osjf.assembly.cache.serializer.SerializerOperationType;
import top.osjf.assembly.util.annotation.CanNull;
import top.osjf.assembly.util.annotation.NotNull;
import top.osjf.assembly.util.data.ByteIdentify;

import java.lang.reflect.Constructor;

/**
 * Caching supports the abstract class of the Heart of Components method, introducing
 * intermediate abstraction {@link IdentifyKeyCallback} to convert byte array type
 * data to {@link ByteIdentify}.
 *
 * <p>The above approach is to achieve code simplification, clarity, and avoid byte
 * address changes (see {@link ByteIdentify} for details).
 *
 * @param <T> The type of help center.
 * @author zpf
 * @since 1.0.0
 */
public abstract class AbstractCacheExecutor<T> implements DefaultCacheExecutor {

    private final HelpCenter<T> helpCenter;

    public AbstractCacheExecutor(@NotNull HelpCenter<T> helpCenter) {
        this.helpCenter = helpCenter;
    }

    abstract class IdentifyKeyCallback<V> implements HelpCenterValueCallback<V, T> {
        private ByteIdentify keyByteIdentify;

        public IdentifyKeyCallback() {
        }

        public IdentifyKeyCallback(byte[] key) {
            this.keyByteIdentify = identifyKeyByteArray(key);
        }

        @Override
        public V doInHelpCenter(HelpCenter<T> center) {
            return inHelp(this.keyByteIdentify, getHelpCenter());
        }

        public abstract V inHelp(ByteIdentify keyByteIdentify, T helpCenter);
    }

    public T getHelpCenter() {
        return this.helpCenter.getHelpCenter();
    }

    @CanNull
    <V> V execute(HelpCenterValueCallback<V, T> callback) {
        return callback.doInHelpCenter(helpCenter);
    }

    /**
     * Convert the specific value of the key to a byte identity encapsulation object.
     *
     * @param var A byte array,must not be {@literal null}.
     * @return Be a {@link ByteIdentify}.
     * @since 1.1.4
     */
    protected ByteIdentify identifyKeyByteArray(byte[] var) {
        return identifyByteArray(var, SerializerOperationType.KEY);
    }

    /**
     * Convert the specific value of the value to a byte identity encapsulation object.
     *
     * @param var A byte array,must not be {@literal null}.
     * @return Be a {@link ByteIdentify}.
     * @since 1.1.4
     */
    protected ByteIdentify identifyValueByteArray(byte[] var) {
        return identifyByteArray(var, SerializerOperationType.VALUE);
    }

    /**
     * Transform to create specific values as byte identity encapsulated objects based
     * on the enumeration type {@link SerializerOperationType}.
     *
     * @param type The type of serialization operation.
     * @param var  A byte array,must not be {@literal null}.
     * @return Be a {@link ByteIdentify}.
     * @since 1.1.4
     */
    protected ByteIdentify identifyByteArray(byte[] var, SerializerOperationType type) {
        return new CacheByteIdentify(var, type.get());
    }

    /*** Important information storage about creating {@link ByteIdentify}. */
    protected static final class Holder {
        static Constructor<? extends ByteIdentify> CONSTRUCTOR;
        static {
            try {
                CONSTRUCTOR = CacheByteIdentify.class.getConstructor(byte[].class, String.class);
            } catch (Throwable ignored) {
            }
        }
        @SuppressWarnings("unchecked")
        public static <T extends ByteIdentify> T createByteIdentify(Object... args) {
            try {
                return (T) CONSTRUCTOR.newInstance(args);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }
    }
}
