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

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Objects;

/**
 * The unpacking object class of {@link ApplicationContext} provides convenience
 * for operating {@link ApplicationContext}.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.3
 */
public final class UnwrapApplicationContext {

    private final ApplicationContext applicationContext;

    /**
     * Constructs a new {@code UnwrapApplicationContext} with Spring {@code ApplicationContext}.
     *
     * @param applicationContext the context object of the Spring framework.
     */
    public UnwrapApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = Objects.requireNonNull(applicationContext, "applicationContext = null");
    }

    /**
     * @return The context object of the Spring framework.
     */
    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    /**
     * @return A {@code BeanDefinitionRegistry} getting by unwrap {@link #applicationContext}.
     * @throws ClassCastException if {@link #applicationContext} not an {@code BeanDefinitionRegistry}
     *                            instance.
     */
    public BeanDefinitionRegistry getBeanDefinitionRegistry() {
        return unwrap(BeanDefinitionRegistry.class);
    }

    /**
     * @return A {@code ConfigurableApplicationContext} getting by unwrap {@link #applicationContext}.
     * @throws ClassCastException if {@link #applicationContext} not an {@code ConfigurableApplicationContext}
     *                            instance.
     */
    public ConfigurableApplicationContext getConfigurableApplicationContext() {
        return unwrap(ConfigurableApplicationContext.class);
    }

    /**
     * Return the Spring context converted object based on the specified type.
     *
     * @param clazz the specified type.
     * @param <T>   the specified generic.
     * @return the Spring context converted object based on the specified type.
     */
    public <T> T unwrap(Class<T> clazz) {
        if (!clazz.isInstance(applicationContext)) {
            throw new ClassCastException(applicationContext.getClass().getName() + " cannot be cast " + clazz.getName());
        }
        return clazz.cast(applicationContext);
    }
}
