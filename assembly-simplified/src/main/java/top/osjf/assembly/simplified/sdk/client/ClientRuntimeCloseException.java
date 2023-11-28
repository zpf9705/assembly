package top.osjf.assembly.simplified.sdk.client;

/**
 * Possible issues that may arise when attempting to
 * solve {@link Client}'s {@link java.io.Closeable}
 * with {@link java.io.IOException} problem.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 2.1.0
 */
public class ClientRuntimeCloseException extends RuntimeException {
    private static final long serialVersionUID = 4730988924158862453L;

    public ClientRuntimeCloseException(Throwable cause) {
        super("Client close Io error .", cause);
    }
}
