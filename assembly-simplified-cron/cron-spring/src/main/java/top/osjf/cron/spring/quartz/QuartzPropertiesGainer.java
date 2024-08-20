package top.osjf.cron.spring.quartz;

import java.util.Properties;

/**
 * The interface for gain quartz setting properties.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.1
 */
public interface QuartzPropertiesGainer {

    Properties getQuartzProperties();

    static QuartzPropertiesGainer of(Properties properties) {
        return new DefaultQuartzPropertiesGainer(properties);
    }

    class DefaultQuartzPropertiesGainer implements QuartzPropertiesGainer {

        private final Properties properties;

        public DefaultQuartzPropertiesGainer(Properties properties) {
            this.properties = properties;
        }

        @Override
        public Properties getQuartzProperties() {
            return properties;
        }
    }
}
