package top.osjf.assembly.sdk;

import copy.cn.hutool.v_5819.core.convert.ConvertException;

/**
 * {@link top.osjf.assembly.sdk.process.DefaultErrorResponse} of abnormal conversion of response data.
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
