package top.osjf.assembly.simplified.scope;

import top.osjf.assembly.util.annotation.NotNull;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 2.2.5
 */
public class ThreadScopedExecutor extends ThreadPoolExecutor {

    private ThreadScoped threadScoped;

    public void setThreadScoped(ThreadScoped threadScoped) {
        this.threadScoped = threadScoped;
    }

    public ThreadScoped getThreadScoped() {
        return threadScoped;
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        threadScoped.run();
    }

    /**
     * Creates a new {@code ThreadPoolExecutor} with the given initial
     * parameters and default thread factory and rejected execution handler.
     * It may be more convenient to use one of the {@link Executors} factory
     * methods instead of this general purpose constructor.
     *
     * @param corePoolSize    the number of threads to keep in the pool, even
     *                        if they are idle, unless {@code allowCoreThreadTimeOut} is set
     * @param maximumPoolSize the maximum number of threads to allow in the
     *                        pool
     * @param keepAliveTime   when the number of threads is greater than
     *                        the core, this is the maximum time that excess idle threads
     *                        will wait for new tasks before terminating.
     * @param unit            the time unit for the {@code keepAliveTime} argument
     *                        tasks submitted by the {@code execute} method.
     * @param queueCapacity   Queue capacity. An unbounded capacity does not increase the pool and therefore
     *                        ignores the "max-size" property.
     * @throws IllegalArgumentException if one of the following holds:<br>
     *                                  {@code corePoolSize < 0}<br>
     *                                  {@code keepAliveTime < 0}<br>
     *                                  {@code maximumPoolSize <= 0}<br>
     *                                  {@code maximumPoolSize < corePoolSize}
     * @throws NullPointerException     if {@code workQueue} is null
     */
    public ThreadScopedExecutor(int corePoolSize,
                                int maximumPoolSize,
                                long keepAliveTime,
                                @NotNull TimeUnit unit,
                                int queueCapacity) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, new ArrayBlockingQueue<>(queueCapacity));
    }
}
