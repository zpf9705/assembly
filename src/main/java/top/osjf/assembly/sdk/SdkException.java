package top.osjf.assembly.sdk;


/**
 * The exception thrown by the body parameter verification of SDK is usually the judgment
 * of business logic parameters, which is thrown during the request.
 *
 * @see top.osjf.assembly.sdk.process.Validated
 * @author zpf
 * @since 1.1.0
 */
public class SdkException extends IllegalArgumentException {

    public SdkException() {
        super();
    }

    public SdkException(String s) {
        super(s);
    }

    public SdkException(String message, Throwable cause) {
        super(message, cause);
    }

    public SdkException(Throwable cause) {
        super(cause);
    }
}