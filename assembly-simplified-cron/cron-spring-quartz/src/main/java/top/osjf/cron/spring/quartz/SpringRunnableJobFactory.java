/*
 * Copyright 2025-? the original author or authors.
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

import org.quartz.spi.JobFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import top.osjf.cron.core.lang.NotNull;
import top.osjf.cron.quartz.repository.RunnableJob;
import top.osjf.cron.quartz.repository.RunnableJobFactory;

/**
 * The implementation class of {@link JobFactory} inherits {@link RunnableJobFactory}
 * and mainly implements the use of Spring framework containers to manage {@link RunnableJob}
 * singleton.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.1
 */
public class SpringRunnableJobFactory
        extends RunnableJobFactory implements BeanDefinitionRegistryPostProcessor, ApplicationContextAware {

    private ApplicationContext applicationContext;

    private BeanDefinitionRegistry registry;

    @Override
    protected RunnableJob newJobInternal(String id, Runnable runnable) {
        if (applicationContext.containsBean(id)) {
            return applicationContext.getBean(id, RunnableJob.class);
        }
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(RunnableJob.class)
                .setScope(BeanDefinition.SCOPE_SINGLETON)
                .addConstructorArgValue(runnable);
        BeanDefinitionReaderUtils
                .registerBeanDefinition(new BeanDefinitionHolder(builder.getBeanDefinition(), id), registry);
        return applicationContext.getBean(id, RunnableJob.class);
    }

    @Override
    public void postProcessBeanDefinitionRegistry(@NotNull BeanDefinitionRegistry registry) throws BeansException {
        this.registry = registry;
    }

    @Override
    public void postProcessBeanFactory(@NotNull ConfigurableListableBeanFactory beanFactory) throws BeansException {
    }

    @Override
    public void setApplicationContext(@NotNull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
