package top.osjf.assembly.simplified.cron;

import cn.hutool.cron.listener.TaskListener;

/**
 * The monitoring interface for timed task start, end, and exception mainly relies on {@link TaskListener},
 * which can be registered through {@link CronRegister#addListener(CronListener...)} and used for task
 * monitoring during startup.
 *
 * @author zpf
 * @since 1.1.0
 */
public interface CronListener extends TaskListener {
}
