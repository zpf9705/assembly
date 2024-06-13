package top.osjf.assembly.util.concurrent;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Thread pool {@link java.util.concurrent.ExecutorService} closes
 * policy specification interface.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.1.3
 */
public interface ThreadPoolShutdownStrategy {

    /**
     * This method specifies a waiting time, intended to close the
     * thread pool after completing the remaining tasks within a
     * specified time range.
     *
     * <p>If the specified time is exceeded,the thread pool is
     * directly closed and the task collection is returned.
     *
     * @param timeout Time dimension.
     * @param unit    Time unit dimension.
     * @return Return the remaining unfinished tasks.
     */
    List<Runnable> shutdown(long timeout, TimeUnit unit);
}
