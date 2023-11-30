package top.osjf.assembly.cache.listener;

import top.osjf.assembly.util.annotation.NotNull;

/**
 * Notify expire object of {@code key} and {@code value}.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.8
 */
public interface ObjectMessage {

    /**
     * Get object type of {@code key}.
     * @return Deserialize the key of the byte array type.
     */
    @NotNull
    Object getKey();

    /**
     * Get object type of {@code value}.
     * @return Deserialize the value of the byte array type.
     */
    @NotNull
    Object getValue();
}
