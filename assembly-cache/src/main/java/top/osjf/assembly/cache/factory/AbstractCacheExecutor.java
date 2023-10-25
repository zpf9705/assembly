package top.osjf.assembly.cache.factory;

import top.osjf.assembly.util.data.ByteIdentify;
import top.osjf.assembly.util.annotation.CanNull;
import top.osjf.assembly.util.annotation.NotNull;

/**
 * Caching supports the abstract class of the Heart of Components method, introducing
 * intermediate abstraction {@link IdentifyKeyCallback} to convert byte array type
 * data to {@link ByteIdentify}.
 *
 * <p>The above approach is to achieve code simplification, clarity, and avoid byte
 * address changes (see {@link ByteIdentify} for details).
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
            this.keyByteIdentify = identifyByteArray(key);
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
     * Convert the byte array into an encapsulated object {} to help with subsequent search methods.
     *
     * @param var A byte array,must not be {@literal null}.
     * @return Be a {@link ByteIdentify}.
     */
    public ByteIdentify identifyByteArray(byte[] var) {
        return new ByteIdentify(var);
    }
}
