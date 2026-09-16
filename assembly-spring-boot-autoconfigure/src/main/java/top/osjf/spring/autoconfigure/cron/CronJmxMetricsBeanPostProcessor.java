/*
 * Copyright 2026-? the original author or authors.
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


package top.osjf.spring.autoconfigure.cron;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import top.osjf.commons.lang.NotNull;
import top.osjf.cron.core.jmx.JMXMetricsCronTaskRepository;
import top.osjf.cron.core.repository.CronTaskRepository;

/**
 * Bean post‑processor for auto‑wrapping {@link CronTaskRepository} with JMX metrics capability.
 *
 * <p>Works at {@link #postProcessBeforeInitialization} phase: the target bean has been instantiated
 * and property‑injected, but its initialization callback has not yet been executed.
 * This avoids duplicate initialization of the raw delegate repository.
 *
 * <p>Wrapping condition:
 * <ul>
 * <li>Bean instance is {@link CronTaskRepository};</li>
 * <li>Not already a JMX‑metrics wrapped instance {@link JMXMetricsCronTaskRepository}, prevent repeated wrapping;
 * </li>
 * <li>No Micrometer {@code MeterRegistry} present in application environment.</li>
 * </ul>
 * When conditions satisfied, returns {@link SpringAdapterJMXMetricsCronTaskRepository},
 * which bridges component lifecycle to Spring {@link org.springframework.beans.factory.InitializingBean}
 * and {@link org.springframework.beans.factory.DisposableBean}.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.2
 */
class CronJmxMetricsBeanPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(@NotNull Object bean, @NotNull String beanName) throws BeansException {
        if (bean instanceof CronTaskRepository && !(bean instanceof JMXMetricsCronTaskRepository)) {
            return new SpringAdapterJMXMetricsCronTaskRepository((CronTaskRepository) bean);
        }
        return bean;
    }

    /**
     * Spring lifecycle adapter for {@link JMXMetricsCronTaskRepository}.
     *
     * <p>The base {@link JMXMetricsCronTaskRepository} defines custom lifecycle methods
     * {@code initialize()} and {@code stop()}, but does not implement Spring lifecycle interfaces.
     * This subclass bridges those custom methods to Spring standard {@link InitializingBean}
     * and {@link DisposableBean}.
     *
     * <p>Lifecycle sequence:
     * <ul>
     * <li>{@link #afterPropertiesSet()} triggers {@link #initialize()}, which registers JMX MBean
     * and initializes the underlying delegate repository.</li>
     * <li>{@link #destroy()} triggers {@link #stop()}, which unregisters JMX MBean and
     * shuts down the underlying delegate repository.</li>
     * </ul>
     *
     * <p>Used together with {@link CronJmxMetricsBeanPostProcessor}, which produces instances
     * of this adapter to avoid duplicate initialization of the raw delegate repository
     */
    private static class SpringAdapterJMXMetricsCronTaskRepository
            extends JMXMetricsCronTaskRepository implements InitializingBean, DisposableBean {

        public SpringAdapterJMXMetricsCronTaskRepository(CronTaskRepository delegate) {
            super(delegate);
        }

        @Override
        public void afterPropertiesSet() throws Exception {
            initialize();
        }

        @Override
        public void destroy() {
            stop();
        }
    }
}
