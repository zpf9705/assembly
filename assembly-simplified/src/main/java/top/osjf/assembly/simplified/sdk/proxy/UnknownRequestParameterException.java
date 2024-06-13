package top.osjf.assembly.simplified.sdk.proxy;

/**
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 2.2.6
 */
public class UnknownRequestParameterException extends RuntimeException {

    private static final long serialVersionUID = -7867163222873978334L;

    public UnknownRequestParameterException() {
        super("Can't find or have multiple request parameters, how can I find the appropriate parameters?");
    }
}
