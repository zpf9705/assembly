package top.osjf.assembly.simplified.support;

import top.osjf.assembly.util.lang.ArrayUtils;

import java.util.Arrays;
import java.util.LinkedHashSet;

/**
 * The relevant general method tool class for {@link org.springframework.context.annotation.Bean}.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 2.2.5
 */
public abstract class BeanPropertyUtils {

    /**
     * Following the processing specification of {@link org.springframework.context.annotation.Bean},
     * the first name should be the main name of the bean.
     *
     * @param names Define a collection of names.
     * @return The primary name of the bean.
     */
    public static String getBeanName(String[] names) {
        if (ArrayUtils.isNotEmpty(names)) {
            return names[0];
        }
        return null;
    }

    /**
     * According to the definition specification of {@link org.springframework.context.annotation.Bean},
     * the non empty element array after removing the first element is
     * called an alias element array.
     *
     * @param names Define a collection of names.
     * @return alias name array.
     */
    public static String[] getAlisaNames(String[] names) {
        if (ArrayUtils.isNotEmpty(names)) {
            if (names.length == 1) {
                return null;
            }
            return new LinkedHashSet<>(Arrays.asList(
                    ArrayUtils.remove(names, 0))).toArray(new String[]{});
        }
        return null;
    }
}
