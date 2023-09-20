package top.osjf.assembly.simplified.sdk;

import cn.hutool.core.convert.ConvertException;

/**
 * {@link top.osjf.assembly.simplified.sdk.process.DefaultErrorResponse} of abnormal conversion of response data.
 *
 * @author zpf
 * @since 1.1.1
 */
public class DataConvertException extends ConvertException {
    private static final long serialVersionUID = 2144997773083517532L;

    public DataConvertException(String message) {
        super(message);
    }
}
