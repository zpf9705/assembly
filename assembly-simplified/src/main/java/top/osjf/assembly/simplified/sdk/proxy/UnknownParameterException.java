package top.osjf.assembly.simplified.sdk.proxy;

import top.osjf.assembly.simplified.sdk.SdkException;

/**
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 2.2.6
 */
public class UnknownParameterException extends SdkException {

    private static final long serialVersionUID = -7867163222873978334L;

    public UnknownParameterException(String name) {
        super("Can't find or have multiple " + name + " parameters, how can I find the appropriate parameters?");
    }
}
