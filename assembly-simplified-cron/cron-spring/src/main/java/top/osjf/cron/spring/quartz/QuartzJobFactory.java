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

import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.spi.JobFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.NonNull;
import org.springframework.util.ClassUtils;
import top.osjf.cron.core.util.ReflectUtils;
import top.osjf.cron.quartz.MethodLevelJob;
import top.osjf.cron.quartz.MethodLevelJobFactory;

import java.lang.reflect.Method;

/**
 * Custom Quartz {@link JobFactory} retrieves objects from Spring's
 * context container based on the filled {@link Job} type, ensuring
 * unique reuse of the objects.
 *
 * <p>Convert Quartz's scheduled tasks for class structure to method level,
 * use the registered {@link JobDetail} to obtain the specific method for
 * executing the bean, and dynamically create a bean to execute the scheduled
 * task. The structure can be viewed from {@link MethodLevelJob}.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
public class QuartzJobFactory extends MethodLevelJobFactory implements ApplicationContextAware, BeanClassLoaderAware {

    private ApplicationContext applicationContext;

    private BeanDefinitionRegistry beanDefinitionRegistry;

    private ClassLoader classLoader;

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        this.beanDefinitionRegistry = (BeanDefinitionRegistry) applicationContext;
    }

    @Override
    public void setBeanClassLoader(@NonNull ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    /**
     * {@inheritDoc}
     * <p>{@code Job} instance object is searched from the Spring container.
     *
     * @param declaringClassName {@inheritDoc}
     * @param methodName         {@inheritDoc}
     * @return a {@code Job} instance gets from {@link #applicationContext}.
     */
    @Override
    protected Job getJob(String declaringClassName, String methodName) {
        final String beanName = methodName + "@" + declaringClassName;
        if (applicationContext.containsBean(beanName)) {
            return applicationContext.getBean(beanName, MethodLevelJob.class);
        }
        Class<?> beanClassName = ClassUtils.resolveClassName(declaringClassName, classLoader);
        Object targetBean = applicationContext.getBean(beanClassName);
        Method method = ReflectUtils.getMethod(beanClassName, methodName);
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(MethodLevelJob.class)
                .setScope(BeanDefinition.SCOPE_SINGLETON)
                .addConstructorArgValue(targetBean)
                .addConstructorArgValue(method);
        BeanDefinitionReaderUtils
                .registerBeanDefinition(new BeanDefinitionHolder(builder.getBeanDefinition(), beanName),
                        beanDefinitionRegistry);
        return applicationContext.getBean(beanName, MethodLevelJob.class);
    }
}
