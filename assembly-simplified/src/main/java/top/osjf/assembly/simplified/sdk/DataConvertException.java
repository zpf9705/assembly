package top.osjf.assembly.simplified.sdk;

import top.osjf.assembly.simplified.sdk.process.DefaultErrorResponse;

/**
 * {@link DefaultErrorResponse} of abnormal conversion of response data.
 *
 * @author zpf
 * @since 1.1.1
 */
public class DataConvertException extends RuntimeException {
    private static final long serialVersionUID = 2144997773083517532L;

    public DataConvertException(String message) {
        super(message);
    }
}
