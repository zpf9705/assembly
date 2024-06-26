package top.osjf.assembly.simplified.sdk.process;

import top.osjf.assembly.simplified.sdk.client.PreProcessingResponseHandler;
import top.osjf.assembly.simplified.sdk.client.ResponseConvert;

import java.io.Serializable;

/**
 * Define response nodes for SDK.
 *
 * <p>It is mainly used to receive the return value information of the API,
 * defining the method {@link #isSuccess()} to determine whether the request
 * is successful, including the method of obtaining the response message
 * {@link #getMessage()}, other transformation methods and processing methods,
 * defined in {@link top.osjf.assembly.simplified.sdk.client.Client}.
 *
 * <p>After the request, the corresponding response body will be transformed
 * according to the rewritten {@link PreProcessingResponseHandler} and
 * {@link ResponseConvert}.</p>
 *
 * @author zpf
 * @since 1.1.0
 */
public interface Response extends ErrorResponse, Serializable {

    /**
     * Returns the success identifier of the request, displayed as a Boolean value.
     * @return displayed as a Boolean value，if {@code true} represents
     *          success, otherwise it fails.
     */
    boolean isSuccess();

    /**
     * Returns information carried by the end of the return request.
     * @return information carried by the end of the return request.
     */
    String getMessage();
}
