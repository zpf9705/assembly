package top.osjf.assembly.cron;

import copy.cn.hutool.v_5819.cron.TaskExecutor;

/**
 * For abstract classes implemented by exception methods, the signals for startup and success can be selectively rewritten.
 *
 * @author zpf
 * @since 1.1.0
 */
public abstract class AbstractExceptionCronListener implements CronListener {

    @Override
    public void onStart(TaskExecutor executor) {

    }

    @Override
    public void onSucceeded(TaskExecutor executor) {

    }

    @Override
    public abstract void onFailed(TaskExecutor executor, Throwable exception);
}
