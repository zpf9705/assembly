package top.osjf.assembly.simplified.sdk.process;

import top.osjf.assembly.util.annotation.CanNull;

/**
 * Request parameter acquisition interface, uniformly
 * return {@link Object},compatible with all return values.
 *
 * @author zpf
 * @since 1.1.0
 */
public interface RequestParamCapable {

    /**
     * @return Return any of {@link Object}.
     */
    @CanNull
    Object getRequestParam();
}
