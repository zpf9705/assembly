package top.osjf.assembly.util;

/**
 * UtilException represents an exception that occurred while one of the util classes is operating.
 *
 * @author zpf
 * @since 1.0.0
 */
public class UtilException extends RuntimeException {

    private static final long serialVersionUID = -904283781179953076L;

    public UtilException() {
    }

    public UtilException(String message) {
        super(message);
    }

    public UtilException(String message, Throwable cause) {
        super(message, cause);
    }

    public UtilException(Throwable cause) {
        super(cause);
    }

    public UtilException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
