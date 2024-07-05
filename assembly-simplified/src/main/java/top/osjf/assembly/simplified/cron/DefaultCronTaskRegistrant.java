package top.osjf.assembly.simplified.cron;

/**
 * Default Impl for {@link CronTaskRegistrant}.
 *
 * <p>Reply for {@link CronTaskManager}.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 2.2.8
 */
public class DefaultCronTaskRegistrant implements CronTaskRegistrant {

    @Override
    public void register(String cronExpression, Runnable runnable) {
        CronTaskManager.registerCronTask(cronExpression, runnable);
    }
}
