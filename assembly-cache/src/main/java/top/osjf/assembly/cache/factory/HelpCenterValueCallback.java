package top.osjf.assembly.cache.factory;

import top.osjf.assembly.util.annotation.CanNull;

/**
 * The callback interface for the return value when using {@link HelpCenter} for operations.
 *
 * @author zpf
 * @since 1.0.0
 */
public interface HelpCenterValueCallback<V, T> {

    @CanNull
    V doInHelpCenter(HelpCenter<T> center);
}
