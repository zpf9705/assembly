package top.osjf.sdk.core.exception;

/**
 * Throw an unknown parameter "exception" of the specified type.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
public abstract class UnknownParameterException extends SdkException {

    private static final long serialVersionUID = -7867163222873978334L;

    public UnknownParameterException(String name) {
        super("Can't find or have multiple " + name + " parameters, how can I find the appropriate parameters?");
    }
}
