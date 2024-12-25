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

package top.osjf.optimize.service_bean.context;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.lang.NonNull;

/**
 * The abstract {@link ServiceContext} service class mainly adapts to
 * the following functions:
 * <ul>
 * <li>Provide its context object through the mechanism of Spring
 * {@link ApplicationContext}.</li>
 * <li>Specify the notification event for container bean refresh
 * completion, and clear the name collection of optimized beans
 * marked by {@link ServiceContextBeanNameGenerator} in the event
 * content.</li>
 * <li>Calling the {@link #close()} method can elegantly close
 * the Spring context {@link ApplicationContext}.</li>
 * </ul>
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
public abstract class AbstractServiceContext implements ServiceContext, ApplicationContextAware,
        ApplicationListener<ContextRefreshedEvent> {

    private ApplicationContext context;

    @Override
    public void setApplicationContext(@NonNull ApplicationContext context) throws BeansException {
        this.context = context;
    }

    /**
     * {@inheritDoc}
     * <p>
     * After the container is refreshed, clear the collection of bean names marked
     * in the cache.
     *
     * @param event event raised when an {@code ApplicationContext} gets
     *              initialized or refreshed.
     */
    @Override
    public void onApplicationEvent(@NonNull ContextRefreshedEvent event) {
        ServiceContextBeanNameGenerator.clear();
    }

    /**
     * {@inheritDoc}
     * <p>
     * Close the current Spring configurable context object.
     * <p>
     * If the context instance is an instance of {@code ConfigurableApplicationContext},
     * call its close method to close the context.
     */
    @Override
    public void close() {
        if (context instanceof ConfigurableApplicationContext) {
            ((ConfigurableApplicationContext) context).close();
        }
    }

    protected ApplicationContext getApplicationContext() {
        return context;
    }
}
