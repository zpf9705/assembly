package top.osjf.assembly.simplified.cron;

import java.util.List;

/**
 * The configuration selection of interface levels related to
 * scheduled tasks in this module.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 2.2.5
 */
public interface CronConfigurer {

    /**
     * Return the collection of listeners {@link CronConfigurer} that will
     * soon need to be registered.
     *
     * @return listeners {@link CronConfigurer} that will
     *          soon need to be registered.
     */
    List<CronListener> getWillRegisterCronListeners();
}
