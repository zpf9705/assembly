package top.osjf.assembly.cache.factory;

import org.springframework.util.Assert;
import top.osjf.assembly.util.ByteContain;

import java.io.Serializable;

/**
 * This abstract class is used to record the currently activated central class
 * and provide it to the corresponding implementation method of the central class.
 *
 * @author zpf
 * @since 1.0.0
 */
@SuppressWarnings({"rawtypes", "serial"})
public abstract class AbstractRecordActivationCenter<C, K, V> implements Center<C, K, V>, Serializable {

    protected static final ByteContain contain = new ByteContain(16);

    protected static Center center0;

    /**
     * Placing a single instance cache.
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
     * Obtain the currently activated center.
     *
     * @return Returns a unique {@link Center} entity.
     */
    public static synchronized Center getSingletonCenter() {
        return center0;
    }
}
