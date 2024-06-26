package top.osjf.assembly.simplified.sdk.process;

import top.osjf.assembly.simplified.sdk.SdkException;

/**
 * Create throw types for request class parameter exceptions based on
 * {@link RequestParam} and
 * {@link RequestParameter}.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 2.2.6
 */
public class RequestCreateException extends SdkException {

    private static final long serialVersionUID = -7492000953956318499L;

    public RequestCreateException(Throwable cause) {
        super("Request class parameter creation failed.", cause);
    }
}
