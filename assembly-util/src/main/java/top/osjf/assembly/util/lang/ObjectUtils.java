package top.osjf.assembly.util.lang;

import java.lang.reflect.Field;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * The object tool class mainly relies on {@link org.apache.commons.lang3.ObjectUtils}
 * for implementation, and this class appears as an extension.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.4
 */
public final class ObjectUtils extends org.apache.commons.lang3.ObjectUtils {

    private ObjectUtils() {
    }

    /**
     * Verify whether the specified field of an object is empty, and if not specified,
     * verify all fields.
     * <p>Include at least one field that can be verified, otherwise parameter
     * exceptions will be thrown.
     * @param object     The object that needs to be judged.
     * @param fieldNames The set of attributes that need to be verified,
     *                   if not specified, verify all fields.
     * @return If true is returned, the validation passes without a field specified as empty.
     */
    public static boolean notNullAnyFields(Object object, String... fieldNames) {
        Objects.requireNonNull(object, "Check object must not be null");
        final Predicate<Field> notNullPre = (field) -> isNotEmpty(ReflectUtils.getFieldValue(object, field));
        Field[] fields = ReflectUtils.getFields(object.getClass(), field -> {
            if (ArrayUtils.isEmpty(fieldNames)) {
                return true;
            }
            return ArrayUtils.contains(fieldNames, field.getName());
        });
        if (ArrayUtils.isEmpty(fields)) {
            throw new IllegalArgumentException("There are no fields to verify.");
        }
        for (Field field : fields) {
            if (!notNullPre.test(field)) {
                return false;
            }
        }
        return true;
    }
}
