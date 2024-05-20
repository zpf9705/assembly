package top.osjf.assembly.simplified.cron;

import cn.hutool.cron.TaskExecutor;
import cn.hutool.cron.listener.TaskListener;

/**
 * The monitoring interface for timed task start, end, and exception
 * mainly relies on {@link TaskListener},which can be registered through
 * {@link CronRegister#addListener(CronListener...)} and used for task
 * monitoring during startup.
 *
 * @author zpf
 * @since 1.1.0
 */
public interface CronListener extends TaskListener {

    /**
     * Triggered when a scheduled task starts.
     *
     * @param executor Job executor.
     */
    void onStart(TaskExecutor executor);

    /**
     * Triggered when the task successfully ends.
     *
     * @param executor Job executor.
     */
    void onSucceeded(TaskExecutor executor);

    /**
     * Triggered when the task fails to start.
     *
     * @param executor Job executor.
     * @param exception The specific exception captured by the
     *                  listener when a scheduled task is abnormal.
     */
    void onFailed(TaskExecutor executor, Throwable exception);
}
