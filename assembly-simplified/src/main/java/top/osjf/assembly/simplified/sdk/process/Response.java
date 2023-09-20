package top.osjf.assembly.simplified.sdk.process;

import java.io.Serializable;

/**
 * Define response nodes for SDK.
 * <p>
 * It is mainly used to receive the return value information of the API, defining the method
 * {@link #isSuccess()} to determine whether the request is successful, including the method
 * of obtaining the response message {@link #getMessage()}, other transformation methods and
 * processing methods, defined in {@link top.osjf.assembly.simplified.sdk.client.Client}.
 * <p>
 * After the request, the corresponding response body will be transformed according to the
 * rewritten {@link top.osjf.assembly.simplified.sdk.client.PreProcessingResponseHandler} and
 * {@link top.osjf.assembly.simplified.sdk.client.ResponseConvert}.</p>
 *
 * @author zpf
 * @since 1.1.0
 */
public interface Response extends ErrorResponse, Serializable {

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
