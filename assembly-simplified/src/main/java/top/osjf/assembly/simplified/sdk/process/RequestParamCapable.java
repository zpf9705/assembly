package top.osjf.assembly.simplified.sdk.process;

import top.osjf.assembly.util.annotation.CanNull;

/**
 * Get request parameters of the specified type.
 * @param <T> Param type.
 * @author zpf
 * @since 1.1.0
 */
@FunctionalInterface
public interface RequestParamCapable<T> {

    /**
     * @return Return any of {@link Object}.
     */
    @CanNull
    T getRequestParam();
}
