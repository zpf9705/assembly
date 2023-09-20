package top.osjf.assembly.simplified.sdk;


/**
 * The exception thrown by the body parameter verification of SDK is usually
 * the judgment of business logic parameters, which is thrown during the request.
 *
 * @author zpf
 * @since 1.1.0
 */
public class SdkException extends IllegalArgumentException {

    private static final long serialVersionUID = -7204419580156052252L;

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