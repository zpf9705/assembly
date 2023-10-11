package top.osjf.assembly.util.lang;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONValidator;
import com.alibaba.fastjson.TypeReference;

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
        if (StringUtils.isBlank(jsonStr)) {
            return false;
        }
        return JSONValidator.from(jsonStr).validate();
    }

    /*Is it a valid JSONArray.*/
    public static boolean isArray(String jsonStr) {
        return Objects.equals(JSONValidator.Type.Array, JSONValidator.from(jsonStr).getType());
    }

    /*Type converter.*/
    public static class TypeReferences<T> extends TypeReference<T> {
    }

    /*Parse empty entity.*/
    public static <T> T parseEmptyObject(Class<T> clazz) {
        return parseObject("{}", clazz);
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
