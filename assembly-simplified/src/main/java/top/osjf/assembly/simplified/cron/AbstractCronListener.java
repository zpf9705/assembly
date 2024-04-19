package top.osjf.assembly.simplified.cron;

import cn.hutool.cron.TaskExecutor;
import top.osjf.assembly.util.annotation.NotNull;

/**
 * For abstract classes implemented by exception methods, the signals
 * for startup and success can be selectively rewritten.
 * @see CronListener
 * @author zpf
 * @since 1.1.0
 */
public abstract class AbstractCronListener implements CronListener {

    @Override
    public void onStart(TaskExecutor executor) {
        onStartSetSchedulerId(executor.getCronTask().getId());
    }

    @Override
    public void onSucceeded(TaskExecutor executor) {
        onSucceededSetSchedulerId(executor.getCronTask().getId());
    }

    @Override
    public void onFailed(TaskExecutor executor, Throwable exception) {
        onFailedSetSchedulerId(executor.getCronTask().getId(), exception);
    }

    /**
     * Triggered when a scheduled task is initiated, passing a unique scheduled ID.
     * @param schedulerId a unique scheduled ID.
     */
    public void onStartSetSchedulerId(@NotNull String schedulerId) {

    }

    /**
     * Triggered when a timed task is completed, passing a unique timed ID.
     * @param schedulerId a unique scheduled ID.
     */
    public void onSucceededSetSchedulerId(@NotNull String schedulerId) {

    }

    /**
     * Triggered when a scheduled task is abnormal, passing a unique scheduled ID.
     * @param schedulerId a unique scheduled ID.
     * @param exception Abnormal encapsulation.
     */
    public void onFailedSetSchedulerId(@NotNull String schedulerId, Throwable exception) {

    }
}
