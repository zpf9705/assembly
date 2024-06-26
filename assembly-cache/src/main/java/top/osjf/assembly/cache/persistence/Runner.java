package top.osjf.assembly.cache.persistence;

import top.osjf.assembly.cache.config.Configuration;
import top.osjf.assembly.cache.exceptions.OnOpenPersistenceException;
import top.osjf.assembly.util.annotation.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Cache persistence methods of operation indicators,
 * decided to take what kind of way to run.
 *
 * @author zpf
 * @since 1.0.0
 */
public abstract class Runner implements MethodRunnableCapable {

    private static final Predicate<Throwable> EXCEPTION_PREDICATE = (e) -> e instanceof OnOpenPersistenceException;

    private static volatile MethodRunnableCapable capable;

    /**
     * Get a Singleton {@code MethodRunnableCapable}
     * <ul>
     *     <li>{@link ASyncPersistenceRunner}</li>
     *     <li>{@link SyncPersistenceRunner}</li>
     * </ul>
     *
     * @return {@link MethodRunnableCapable}
     */
    public static synchronized MethodRunnableCapable getCapable() {
        if (capable == null) {
            if (Configuration.getGlobalConfiguration().isEnablePersistenceAsync()) {
                capable = new ASyncPersistenceRunner();
            } else {
                capable = new SyncPersistenceRunner();
            }
        }
        return capable;
    }

    public abstract void run(@NotNull Runnable runnable, @NotNull Consumer<String> errorLoggerConsumer);


    /**
     * Sync to run Persistence method
     */
    private static class SyncPersistenceRunner extends Runner {

        @Override
        public void run(@NotNull Runnable runnable, @NotNull Consumer<String> errorLoggerConsumer) {
            try {
                runnable.run();
            } catch (Throwable e) {
                if (!EXCEPTION_PREDICATE.test(e)) errorLoggerConsumer.accept(e.getMessage());
            }
        }
    }

    /**
     * ASync to run Persistence method
     */
    private static class ASyncPersistenceRunner extends Runner {

        @Override
        public void run(@NotNull Runnable runnable, @NotNull Consumer<String> errorLoggerConsumer) {
            CachePersistenceThreadLocal.CachePersistenceThreadData data = CachePersistenceThreadLocal.getData();
            CompletableFuture.runAsync(() -> {
                CachePersistenceThreadLocal.putData(data);
                try {
                    runnable.run();
                } finally {
                    CachePersistenceThreadLocal.putData(null);
                }
            }).whenComplete((s, e) -> {
                if (!EXCEPTION_PREDICATE.test(e)) errorLoggerConsumer.accept(e.getMessage());
            });
        }
    }
}
