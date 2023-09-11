package top.osjf.assembly.sdk;

import copy.cn.hutool.v_5819.core.util.ArrayUtil;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import top.osjf.assembly.sdk.client.Request;

import java.util.Collection;
import java.util.Objects;


/**
 * SDK encapsulation specific assertion validator.
 * <p>
 * Mainly aimed at verifying necessary parameters before{@link Request#validate()} operation
 *
 * @author zpf
 * @since 1.1.0
 */
public final class SdkAssert {

    private static final Integer ASSERT_UTILS_THROW_EX_CODE = 58845;

    /*
     * (non-Javadoc)
     * @see io.github.zpf9705.expiring.util.AssertUtils.Operation
     */
    public static void isTrue(boolean expression, @NonNull String message) {
        if (!expression) {
            throw new SdkException(ASSERT_UTILS_THROW_EX_CODE, message);
        }
    }

    /*
     * (non-Javadoc)
     * @see io.github.zpf9705.expiring.util.AssertUtils.Operation
     */
    public static void isFalse(boolean expression, @NonNull String message) {
        if (expression) {
            throw new SdkException(ASSERT_UTILS_THROW_EX_CODE, message);
        }
    }

    /*
     * (non-Javadoc)
     * @see io.github.zpf9705.expiring.util.AssertUtils.Operation
     */
    public static void isNull(@Nullable Object object, @NonNull String message) {
        if (Objects.nonNull(object)) {
            throw new SdkException(ASSERT_UTILS_THROW_EX_CODE, message);
        }
    }

    /*
     * (non-Javadoc)
     * @see io.github.zpf9705.expiring.util.AssertUtils.Operation
     */
    public static void notNull(@Nullable Object object, @NonNull String message) {
        if (Objects.isNull(object)) {
            throw new SdkException(ASSERT_UTILS_THROW_EX_CODE, message);
        }
    }

    /*
     * (non-Javadoc)
     * @see io.github.zpf9705.expiring.util.AssertUtils.Operation
     */
    public static void hasText(@Nullable String text, @NonNull String message) {
        if (!StringUtils.hasText(text)) {
            throw new SdkException(ASSERT_UTILS_THROW_EX_CODE, message);
        }
    }

    /*
     * (non-Javadoc)
     * @see io.github.zpf9705.expiring.util.AssertUtils.Operation
     */
    public static void notEmpty(@Nullable Collection<?> collection, @NonNull String message) {
        if (CollectionUtils.isEmpty(collection)) {
            throw new SdkException(ASSERT_UTILS_THROW_EX_CODE, message);
        }
    }

    /*
     * (non-Javadoc)
     * @see io.github.zpf9705.expiring.util.AssertUtils.Operation
     */
    public static void notEmpty(@Nullable Object[] array, @NonNull String message) {
        if (ArrayUtil.isEmpty(array)) {
            throw new SdkException(ASSERT_UTILS_THROW_EX_CODE, message);
        }
    }

    /*
     * (non-Javadoc)
     * @see io.github.zpf9705.expiring.util.AssertUtils.Operation
     */
    public static void throwException(String message) {
        throw new SdkException(ASSERT_UTILS_THROW_EX_CODE, message);
    }

    /*
     * (non-Javadoc)
     * @see io.github.zpf9705.expiring.util.AssertUtils.Operation
     */
    public static void throwException(int code, String message) {
        throw new SdkException(code, message);
    }
}
