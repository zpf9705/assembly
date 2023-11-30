package top.osjf.assembly.cache.factory;

import top.osjf.assembly.util.lang.Asserts;

import java.io.Serializable;

/**
 * Abstract function class: used to record the currently activated cache center, providing static storage and retrieval.
 * @param <C> The type of help center.
 * @param <K> The type of key.
 * @param <V> The type of value.
 * @author zpf
 * @since 1.0.0
 */
@SuppressWarnings({"rawtypes", "serial"})
public abstract class AbstractRecordActivationCenter<C, K, V> implements Center<C, K, V>, Serializable {

    protected static Center center;

    /**
     * Place it in a global cache center.
     *
     * @param value Must not be {@literal null}.
     */
    public static synchronized void setSingletonCenter(Center value) {
        if (center != null) {
            return;
        }
        Asserts.notNull(value, "Center set value must not be null");
        center = value;
    }

    /**
     * Obtain the global cache center.
     *
     * @return Returns a unique {@link Center} entity.
     */
    public static synchronized Center getSingletonCenter() {
        return center;
    }
}
