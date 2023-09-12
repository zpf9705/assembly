package top.osjf.assembly.sdk;

import copy.cn.hutool.v_5819.core.collection.CollectionUtil;
import copy.cn.hutool.v_5819.http.ContentType;

import java.util.Map;

/**
 * About sdk helper tool class.
 *
 * @author zpf
 * @since 1.1.0
 */
public abstract class SdkUtils {

    public static final String named = "Content-Type";

    public static void checkContentType(Map<String, String> headers) {
        if (CollectionUtil.isEmpty(headers)) {
            return;
        }
        //if no Content-Type
        if (!headers.containsKey(named)) {
            //default to JSON Content-Type
            headers.put(named, ContentType.JSON.getValue());
        }
    }
}
