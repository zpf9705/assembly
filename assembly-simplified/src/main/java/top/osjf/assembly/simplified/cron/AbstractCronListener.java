package top.osjf.assembly.simplified.cron;

import cn.hutool.cron.TaskExecutor;
import org.springframework.lang.NonNull;

/**
 * For abstract classes implemented by exception methods, the signals for startup and success can be selectively rewritten.
 *
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

    public void onStartSetSchedulerId(@NonNull String SchedulerId) {

    }

    public void onSucceededSetSchedulerId(@NonNull String SchedulerId) {

    }

    public void onFailedSetSchedulerId(@NonNull String SchedulerId, Throwable exception) {

    }
}
