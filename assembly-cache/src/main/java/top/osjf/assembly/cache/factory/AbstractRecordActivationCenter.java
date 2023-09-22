package top.osjf.assembly.cache.factory;

import org.springframework.util.Assert;

import java.io.Serializable;

/**
 * Abstract function class: used to record the currently activated cache center, providing static storage and retrieval.
 *
 * @author zpf
 * @since 1.0.0
 */
@SuppressWarnings({"rawtypes", "serial"})
public abstract class AbstractRecordActivationCenter<C, K, V> implements Center<C, K, V>, Serializable {

    protected static Center center0;

    /**
     * Place it in a global cache center.
     *
     * @param center Must not be {@literal null}.
     */
    public static synchronized void setSingletonCenter(Center center) {
        Assert.notNull(center, "Center must not be null");
        if (center0 != null) {
            return;
        }
        center0 = center;
    }

    /**
     * Obtain the global cache center.
     *
     * @return Returns a unique {@link Center} entity.
     */
    public static synchronized Center getSingletonCenter() {
        return center0;
    }
}
