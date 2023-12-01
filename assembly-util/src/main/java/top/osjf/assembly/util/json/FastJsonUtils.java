package top.osjf.assembly.util.json;

import com.alibaba.fastjson2.*;
import top.osjf.assembly.util.lang.StringUtils;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * Fastjson utils from {@link JSON}, and this category points out the expansion function.
 *
 * @author zpf
 * @since 1.0.4
 */
public final class FastJsonUtils {

    private FastJsonUtils() {
    }

    public static <T> String toJSONString(T obj) {
        return JSON.toJSONString(obj);
    }

    public static JSONObject parseObject(String jsonStr) {
        return JSON.parseObject(jsonStr);
    }

    @SuppressWarnings("unchecked")
    public static <T> T parseObject(String jsonStr, Object requiredType) {
        if (requiredType == null) {
            return null;
        }
        T result;
        if (requiredType instanceof Class) {
            result = parseObject(jsonStr, (Class<T>) requiredType);
        } else if (requiredType instanceof Type) {
            result = parseObject(jsonStr, (Type) requiredType);
        } else {
            result = null;
        }
        return result;
    }

    public static <T> T parseObject(String jsonStr, Class<T> requiredType) {
        return JSON.parseObject(jsonStr, requiredType);
    }

    public static <T> T parseObject(String jsonStr, Type requiredType) {
        return JSON.parseObject(jsonStr, requiredType);
    }

    @SuppressWarnings("unchecked")
    public static <T> List<T> parseArray(String jsonStr, Object requiredType) {
        if (requiredType == null) {
            return null;
        }
        List<T> result;
        if (requiredType instanceof Class) {
            result = parseArray(jsonStr, (Class<T>) requiredType);
        } else if (requiredType instanceof Type) {
            result = parseArray(jsonStr, (Type) requiredType);
        } else {
            result = null;
        }
        return result;
    }

    public static <T> List<T> parseArray(String jsonStr, Class<T> requiredType) {
        return JSON.parseArray(jsonStr, requiredType);
    }

    public static <T> List<T> parseArray(String jsonStr, Type requiredType) {
        return JSON.parseArray(jsonStr, requiredType);
    }

    public static class TypeReferences<T> extends TypeReference<T> {
    }

    //Standard JSON object format or JSON array format.
    public static boolean isValidObjectOrArray(String jsonStr) {
        if (StringUtils.isBlank(jsonStr)) {
            return false;
        }
        return isValidObject(jsonStr) || isValidArray(jsonStr);
    }

    //The standard JSON object format does not include values.
    public static boolean isValidObject(String jsonStr) {
        return JSON.isValidObject(jsonStr);
    }

    //Standard JSON array format.
    public static boolean isValidArray(String jsonStr) {
        return JSON.isValidArray(jsonStr);
    }

    public static <T> T toObject(String str, TypeReferences<T> references) {
        return JSON.parseObject(str, references);
    }

    public static <T> T toEmptyObj(Object type) {
        return parseObject("{}", type);
    }

    public static <T> T toEmptyObj(Class<T> clazz) {
        return JSON.parseObject("{}", clazz);
    }

    public static <T> T toEmptyObj(Type type) {
        return JSON.parseObject("{}", type);
    }

    public static Map<String, Object> getInnerMapByJsonStr(String jsonStr) {
        if (isValidObject(jsonStr)) {
            return parseObject(jsonStr);
        }
        return null;
    }
}
