package top.osjf.assembly.sdk.process;

import java.io.Serializable;

/**
 * Defining a response node mainly determines whether the response was successful,
 * whether the response status was returned, including error information about the failure.
 *
 * @author zpf
 * @since 1.1.0
 */
public interface Response extends Serializable {

    /**
     * Verify this request whether successful.
     *
     * @return True Success False Failure.
     */
    boolean isSuccess();

    /**
     * Get Request return message.
     *
     * @return Status information.
     */
    String getMessage();
}
