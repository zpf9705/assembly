/*
 * Copyright 2024-? the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package top.osjf.cron.spring.quartz;

import java.util.Properties;

/**
 * Quartz property retriever interface.
 * <p>Used to obtain the configuration properties required for Quartz job scheduler.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.1
 */
public interface QuartzPropertiesGainer {

    /**
     * Get the configuration properties of Quartz job scheduler.
     *
     * <p>The implementation of this method should return a 'Properties' object
     * containing the required configuration properties for Quartz.
     *
     * <p>These properties will be used to configure the behavior of the Quartz job scheduler.
     *
     * @return The configuration properties of the  Quartz job scheduler.
     */
    Properties getQuartzProperties();

    /**
     * The factory method is used to create a QuartzProperties Gainer instance using a given Properties object.
     *
     * <p>This method provides a convenient way to quickly instantiated a QuartzProperties Gainer,
     * The 'Properties' object directly serves as the configuration source for Quartz.
     *
     * @param properties The configuration properties of Quartz.
     * @return Use the QuartzProperties Gainer instance implemented with the given Properties.
     */
    static QuartzPropertiesGainer of(Properties properties) {
        return new DefaultQuartzPropertiesGainer(properties);
    }

    /**
     * The default implementation of Quartz property retriever.
     * <p>
     * This class implements the QuartzProperties Gainer interface and encapsulates a Properties object,
     * Used to store and provide configuration properties for Quartz job scheduler.
     */
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
