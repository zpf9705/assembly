package top.osjf.assembly.simplified.sdk;

import org.apache.http.entity.ContentType;
import top.osjf.assembly.util.lang.Maps;

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
        if (Maps.isEmpty(headers)) {
            return;
        }
        //if no Content-Type
        if (!headers.containsKey(named)) {
            //default to JSON Content-Type
            headers.put(named, ContentType.APPLICATION_JSON.getMimeType());
        }
    }

    public static Object[] toLoggerArray(Object... args) {
        return args;
    }
}
