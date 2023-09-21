package top.osjf.assembly.cache.persistence;

import top.osjf.assembly.util.annotation.NotNull;

import java.util.function.Consumer;

/**
 * Method runnable choose for {@link Runnable}
 *
 * @author zpf
 * @since 1.0.0
 */
public interface MethodRunnableCapable {

    /**
     * Runnable with a {@code persistence method}
     *
     * @param runnable            {@link Runnable}
     * @param errorLoggerConsumer Persistence method error logger
     */
    void run(@NotNull Runnable runnable, @NotNull Consumer<String> errorLoggerConsumer);
}
