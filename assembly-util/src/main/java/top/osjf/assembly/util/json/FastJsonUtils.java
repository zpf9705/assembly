package top.osjf.assembly.util.json;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;

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

    public static <T> T toObj(String str, TypeReferences<T> references) {
        return JSON.parseObject(str, references);
    }

    public static <T> T toEmptyObj(Class<T> clazz) {
        return JSON.parseObject("{}", clazz);
    }

    public static Map<String,Object> getInnerMapByJsonStr(String str){
        if (isValid(str)){
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

    public static <T> T toObject(Object pojo, Class<T> clazz) {
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
}
