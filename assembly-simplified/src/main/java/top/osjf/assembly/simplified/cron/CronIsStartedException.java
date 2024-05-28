package top.osjf.assembly.simplified.cron;

import cn.hutool.cron.CronException;

/**
 * Throw an exception if the scheduled task has already been initiated.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 2.2.5
 */
public class CronIsStartedException extends CronException {

    private static final long serialVersionUID = 7007036399239003429L;

    public CronIsStartedException() {
        super("Scheduler has been started, please stop it first!");
    }
}
