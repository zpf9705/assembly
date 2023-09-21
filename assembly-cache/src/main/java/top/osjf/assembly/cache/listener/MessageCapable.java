package top.osjf.assembly.cache.listener;

import top.osjf.assembly.util.annotation.NotNull;

import java.io.Serializable;

/**
 * Overdue message keys for interface
 *
 * @author zpf
 * @since 1.0.0
 */
public interface MessageCapable extends Serializable {

    /**
     * Get byte [] type of {@code key}.
     *
     * @return Not be {@literal null}.
     */
    @NotNull
    byte[] getByteKey();

    /**
     * Get byte [] type of {@code value}.
     *
     * @return Not be {@literal null}.
     */
    @NotNull
    byte[] getByteValue();

    /**
     * Get object type of {@code key}.
     *
     * @return Not be {@literal null}.
     */
    @NotNull
    Object getKey();

    /**
     * Get object type of {@code value}.
     *
     * @return Not be {@literal null}.
     */
    @NotNull
    Object getValue();
}
