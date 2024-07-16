package top.osjf.sdk.core.exception;

import top.osjf.sdk.core.client.Client;

/**
 * When {@link Client#request()} is failed,throw it within a real cause.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
public class ClientRequestFailedException extends RuntimeException {

    private static final long serialVersionUID = 4730988924158862453L;

    public ClientRequestFailedException(Throwable cause) {
        super(cause);
    }
}
