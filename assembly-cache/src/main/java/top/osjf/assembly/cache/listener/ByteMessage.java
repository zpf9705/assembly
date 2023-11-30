package top.osjf.assembly.cache.listener;

import top.osjf.assembly.util.annotation.NotNull;

/**
 * Resolve expire cache persistence byte array of {@code key} and {@code value}.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.8
 */
public interface ByteMessage {

    /**
     * Get byte [] type of {@code key}.
     * @return Serialize the key  of the byte array type.
     */
    @NotNull
    byte[] getByteKey();

    /**
     * Get byte [] type of {@code value}.
     * @return Serialize the value of the byte array type.
     */
    @NotNull
    byte[] getByteValue();
}
