package top.osjf.assembly.simplified.cron;

/**
 * The executor of the dynamic registration of scheduled tasks, developers
 * can call the method of this interface to register a scheduled task to
 * the current scheduled task center management at runtime.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 2.2.8
 */
public interface CronTaskRegistrant {

    /**
     * Build a scheduled task that can be registered for central
     * processing using Cron standardized expressions and an
     * executable runtime {@link Runnable}.
     *
     * @param cronExpression Expression in {@code Cron} format.
     * @param runnable       Timed execution of the runtime.
     */
    void register(String cronExpression, Runnable runnable);
}
