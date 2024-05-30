package top.osjf.assembly.simplified.scope;

import top.osjf.assembly.util.annotation.NotNull;

/**
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 2.2.5
 */
@FunctionalInterface
public interface ThreadScopedDestructionRegistry {

    /**
     * Register the given callback as to be executed after thread completion.
     *
     * @param callback     the callback to be executed for destruction.
     * @param afterExecute The runtime after the thread has completed execution.
     */
    void registerDestructionCallback(@NotNull Runnable callback,
                                     @NotNull Runnable afterExecute);
}
