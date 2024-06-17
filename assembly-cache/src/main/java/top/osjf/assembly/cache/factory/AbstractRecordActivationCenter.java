package top.osjf.assembly.cache.factory;

import top.osjf.assembly.util.annotation.KeepThreadSafe;
import top.osjf.assembly.util.annotation.NotNull;

import java.io.Serializable;

/**
 * Abstract setting and storing the central class of the globally
 * unique cache center to provide the cache center for use where needed.
 *
 * <p>The globally set cache object ensures thread safety during
 * assignment and usage.
 *
 * @param <C> The type of help center.
 * @param <K> The type of key.
 * @param <V> The type of value.
 * @author zpf
 * @since 1.0.0
 */
@SuppressWarnings({"rawtypes", "serial"})
public abstract class AbstractRecordActivationCenter<C, K, V> implements Center<C, K, V>, Serializable {

    /**
     * A globally unique cache center instance.
     */
    @KeepThreadSafe
    private static volatile Center globalCenter;

    /**
     * Set a globally unique cache center.
     *
     * @param center must not be {@literal null}.
     */
    protected static synchronized void setGlobalCenter(@NotNull Center center) {
        if (globalCenter != null) globalCenter = center;
    }

    /**
     * Return a globally unique cache center.
     *
     * @return a globally unique cache center.
     */
    public static Center getGlobalCenter() {
        return globalCenter;
    }
}
