package top.osjf.assembly.simplified.sdk.annotation;

/**
 * Exception with invalid host address.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 2.1.1
 */
public class IncorrectHostException extends RuntimeException {

    private static final long serialVersionUID = -1221839322641243165L;

    public IncorrectHostException(String host) {
        super("[" + host + "] not a valid host address");
    }
}
