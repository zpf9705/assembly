package top.osjf.sdk.core.exception;


/**
 * The exception thrown by the body parameter verification of SDK is usually
 * the judgment of business logic parameters, which is thrown during the request.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
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