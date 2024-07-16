package top.osjf.sdk.core.exception;

/**
 * Throw an unknown parameter exception for a response parameter.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
public class UnknownResponseParameterException extends UnknownParameterException {

    private static final long serialVersionUID = -7867163222873978334L;

    public UnknownResponseParameterException() {
        super("response");
    }
}
