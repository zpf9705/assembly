package top.osjf.assembly.cache.factory;

import top.osjf.assembly.cache.factory.expiremap.ExpireMapCenter;
import top.osjf.assembly.util.ByteContain;

/**
 * Core cache to help implement the interface , establish the cache help center.
 * <p>
 * Example:
 * <ul>
 *     <li>{@link ExpireMapCenter}</li>
 *     <li>...</li>
 * </ul>
 *
 * @author zpf
 * @since 1.0.0
 */
@FunctionalInterface
public interface HelpCenter<T> {

    T getHelpCenter();

    /**
     * Obtain a thread safe storage class for recording unique byte value ranges.
     *
     * @return {@link ByteContain}.
     */
    default ByteContain getContain() {
        return RecordActivationCenter.contain;
    }
}
