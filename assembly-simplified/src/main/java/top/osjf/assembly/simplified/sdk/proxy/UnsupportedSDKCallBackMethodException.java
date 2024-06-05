package top.osjf.assembly.simplified.sdk.proxy;

/**
 * Throw an operation to indicate that the SDK proxy callback
 * does not support a processing method.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 2.2.5
 */
public class UnsupportedSDKCallBackMethodException extends UnsupportedOperationException {

    private static final long serialVersionUID = 4516818140399050740L;

    public UnsupportedSDKCallBackMethodException(String methodName) {
        super("Unsupported callback method for SDK proxy unified processing [ " + methodName + " ]");
    }
}
