package top.osjf.assembly.simplified.sdk.process;


import org.springframework.lang.Nullable;

/**
 * Request parameter acquisition interface, uniformly return {@link Object},
 * compatible with all return values.
 *
 * @author zpf
 * @since 1.1.0
 */
public interface RequestParamCapable {

    /**
     * @return Return any of {@link Object}.
     */
    @Nullable
    Object getRequestParam();
}
