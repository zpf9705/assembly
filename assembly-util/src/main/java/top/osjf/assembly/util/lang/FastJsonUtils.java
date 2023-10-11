package top.osjf.assembly.util.lang;

import com.alibaba.fastjson.*;

import java.util.Objects;

/**
 * Fastjson utils from {@link JSON}, and this category points out the expansion function.
 *
 * @author zpf
 * @since 1.0.4
 */
public final class FastJsonUtils extends JSON {

    private FastJsonUtils() {
    }

    //**************************************** Extends ***********************************************

    /*Is it a valid JSON.*/
    public static boolean isValid(String jsonStr) {
        if (StringUtils.isBlank(jsonStr)) return false;
        return JSONValidator.from(jsonStr).validate();
    }

    /*Is it a valid JSONArray.*/
    public static boolean isArray(String jsonStr) {
        if (StringUtils.isBlank(jsonStr)) return false;
        return Objects.equals(JSONValidator.Type.Array, JSONValidator.from(jsonStr).getType());
    }

    /*Type converter.*/
    public static class TypeReferences<T> extends TypeReference<T> {
    }

    /*Parse empty entity.*/
    public static <T> T parseEmptyObject(Class<T> clazz) {
        if (clazz == null) return null;
        return parseObject("{}", clazz);
    }

    /*Parse pojo jsonObject.*/
    public static JSONObject parseObject(Object arg) {
        if (arg == null) return null;
        JSONObject obj;
        if (arg instanceof JSONObject) {
            obj = (JSONObject) arg;
        } else {
            if (arg instanceof String) {
                if (isValid((String) arg)) {
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

    /*Parse pojo object.*/
    public static <T> T parseObject(Object pojo, Class<T> clazz) {
        String jsonStr;
        if (pojo instanceof String) {
            if (isValid((String) pojo)) {
                jsonStr = (String) pojo;
            } else {
                throw new JSONException("Not a valid json String");
            }
        } else {
            String string = pojo.toString();
            if (isValid(string)) {
                jsonStr = string;
            } else {
                jsonStr = toJSONString(pojo);
            }
        }
        return parseObject(jsonStr, clazz);
    }

    /*JSON transformation entity.*/
    @SuppressWarnings("rawtypes")
    public static <T> T parseObject(String str, TypeReferences references,
                                    com.alibaba.fastjson.parser.Feature... features) {
        return parseObject(str, references.getType(), features);
    }
}
