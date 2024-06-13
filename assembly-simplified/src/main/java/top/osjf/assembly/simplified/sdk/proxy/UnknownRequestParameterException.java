package top.osjf.assembly.simplified.sdk.proxy;

/**
 * Throw an unknown parameter exception for a request parameter.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 2.2.6
 */
public class UnknownRequestParameterException extends UnknownParameterException {

    private static final long serialVersionUID = -7867163222873978334L;

    public UnknownRequestParameterException() {
        super("request");
    }
}
