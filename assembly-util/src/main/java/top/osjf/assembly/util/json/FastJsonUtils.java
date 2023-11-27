package top.osjf.assembly.util.json;

import com.alibaba.fastjson.*;
import top.osjf.assembly.util.lang.StringUtils;

import java.util.Map;

/**
 * Fastjson utils from {@link JSON}, and this category points out the expansion function.
 *
 * @author zpf
 * @since 1.0.4
 */
public final class FastJsonUtils extends JSON {

    private FastJsonUtils() {
    }

    public static class TypeReferences<T> extends TypeReference<T> {
    }

    //Standard JSON object format or JSON array format.
    public static boolean isValid(String jsonStr) {
        if (StringUtils.isBlank(jsonStr)) {
            return false;
        }
        return isValidObject(jsonStr) || isValidArray(jsonStr);
    }

    //The standard JSON object format does not include values.
    public static boolean isValidObject(String jsonStr) {
        if (StringUtils.isBlank(jsonStr)) {
            return false;
        }
        try {
            JSONObject.parseObject(jsonStr);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    //Standard JSON array format.
    public static boolean isValidArray(String jsonStr) {
        if (StringUtils.isBlank(jsonStr)) {
            return false;
        }
        try {
            JSONArray.parseArray(jsonStr);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static <T> T toObj(String str, TypeReferences<T> references) {
        return JSON.parseObject(str, references);
    }

    public static <T> T toEmptyObj(Class<T> clazz) {
        return JSON.parseObject("{}", clazz);
    }

    public static Map<String, Object> getInnerMapByJsonStr(String str) {
        if (isValidObject(str)) {
            return parseObject(str).getInnerMap();
        }
        return null;
    }

    public static JSONObject toObj(Object arg) {
        if (arg == null) return null;
        JSONObject obj;
        if (arg instanceof JSONObject) {
            obj = (JSONObject) arg;
        } else {
            if (arg instanceof String) {
                if (isValidObject((String) arg)) {
                    obj = parseObject((String) arg);
                } else {
                    throw new JSONException("Not a valid json String");
                }
            } else {
                obj = parseObject(toJSONString(arg));
            }
        }
        return obj;
    }

    public static <T> T toObject(Object pojo, Class<T> clazz) {
        String jsonStr;
        if (pojo instanceof String) {
            if (isValidObject((String) pojo)) {
                jsonStr = (String) pojo;
            } else {
                throw new JSONException("Not a valid json String");
            }
        } else {
            String string = pojo.toString();
            if (isValidObject(string)) {
                jsonStr = string;
            } else {
                jsonStr = toJSONString(pojo);
            }
        }
        return parseObject(jsonStr, clazz);
    }
}
