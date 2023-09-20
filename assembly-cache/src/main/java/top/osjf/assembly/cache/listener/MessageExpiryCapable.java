package top.osjf.assembly.cache.listener;

import top.osjf.assembly.util.annotation.NotNull;

/**
 * Overdue message keys for interface
 *
 * @author zpf
 * @since 3.0.1
 */
public interface MessageExpiryCapable {

    /**
     * Get byte [] type of {@code key}
     *
     * @return No {@literal null}
     */
    @NotNull
    byte[] getByteKey();

    /**
     * Get byte [] type of {@code value}
     *
     * @return No {@literal null}
     */
    @NotNull
    byte[] getByteValue();

    /**
     * Get object type of {@code key}
     *
     * @return No {@literal null}
     */
    @NotNull
    Object getKey();

    /**
     * Get object type of {@code value}
     *
     * @return No {@literal null}
     */
    @NotNull
    Object getValue();
}
