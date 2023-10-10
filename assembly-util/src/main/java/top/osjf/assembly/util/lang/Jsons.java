package top.osjf.assembly.util.lang;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONValidator;
import com.alibaba.fastjson.TypeReference;

import java.util.Objects;

/**
 * The Json function implementation in  {@link JSON}.
 *
 * @author zpf
 * @since 1.0.3
 */
public final class Jsons extends JSON {
    private Jsons() {
    }

    //**************************************** Extends ***********************************************

    public static boolean isValid(String jsonStr) {
        if (Strings.isBlank(jsonStr)) {
            return false;
        }
        return JSONValidator.from(jsonStr).validate();
    }

    public static boolean isArray(String jsonStr) {
        return Objects.equals(JSONValidator.Type.Array, JSONValidator.from(jsonStr).getType());
    }

    public static class TypeReferences<T> extends TypeReference<T> {
    }

    @SuppressWarnings("rawtypes")
    public static <T> T parseObject(String str, TypeReferences references,
                                    com.alibaba.fastjson.parser.Feature... features) {
        return parseObject(str, references.getType(), features);
    }
}
