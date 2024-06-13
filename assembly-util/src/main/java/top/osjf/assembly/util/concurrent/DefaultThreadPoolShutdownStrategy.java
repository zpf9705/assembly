package top.osjf.assembly.util.concurrent;

import top.osjf.assembly.util.annotation.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Default impl for {@link ThreadPoolShutdownStrategy}.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.1.3
 */
public class DefaultThreadPoolShutdownStrategy implements ThreadPoolShutdownStrategy {

    private final ExecutorService executorService;

    public DefaultThreadPoolShutdownStrategy(@NotNull ExecutorService executorService) {
        this.executorService = executorService;
    }

    @Override
    public List<Runnable> shutdown(long timeout, TimeUnit unit) {
        if (executorService.isShutdown()) {
            return Collections.emptyList();
        }
        executorService.shutdown();
        boolean done;
        try {
            done = executorService.awaitTermination(timeout, unit);
        } catch (InterruptedException e) {
            done = false;
        }
        if (!done) {
            return executorService.shutdownNow();
        }
        return Collections.emptyList();
    }
}
