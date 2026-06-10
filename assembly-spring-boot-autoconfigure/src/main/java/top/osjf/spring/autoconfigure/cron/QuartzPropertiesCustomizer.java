package top.osjf.spring.autoconfigure.cron;

import top.osjf.cron.core.lifecycle.InitializeProperties;

/**
 * Callback interface that can be implemented by beans wishing to customize Quartz's
 * {@link InitializeProperties Quartz Properties} before it is used.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.1
 */
public interface QuartzPropertiesCustomizer {

    /**
     * Customize the Quartz Properties.
     * @param properties the {@link InitializeProperties} to customize.
     */
    void customize(InitializeProperties properties);
}
