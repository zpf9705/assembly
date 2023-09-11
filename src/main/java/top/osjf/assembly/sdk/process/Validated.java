package top.osjf.assembly.sdk.process;

import top.osjf.assembly.sdk.SdkException;

/**
 * The unified parameter validation process before the request.
 *
 * @author zpf
 * @since 1.1.0
 */
public interface Validated {

    /**
     * Method for verifying request parameters, fixed throw {@link SdkException}.
     */
    void validate() throws SdkException;
}
