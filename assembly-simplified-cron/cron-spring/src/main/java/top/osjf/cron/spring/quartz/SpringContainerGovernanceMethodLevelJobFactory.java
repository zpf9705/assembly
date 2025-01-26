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
import top.osjf.cron.quartz.MethodLevelJob;
import top.osjf.cron.quartz.MethodLevelJobFactory;

import java.lang.reflect.Method;

/**
 * {@code SpringContainerGovernanceMethodLevelJobFactory} is an extension about
 * {@link MethodLevelJob} that inherits the Spring framework and uses the Spring
 * container to manage and execute tasks.
 *
 * <p>Its callback method follows the {@link #newJob} convention, defining the
 * class as a bean in the container by default, and dynamically creating a
 * {@link MethodLevelJob} bean to be managed by Spring. Each time it retrieves a
 * singleton of the {@link MethodLevelJob} bean from the container, it achieves the
 * goal of globally unique use.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.3
 */
public class SpringContainerGovernanceMethodLevelJobFactory extends MethodLevelJobFactory implements ApplicationContextAware,
        BeanClassLoaderAware {

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
     * <p>{@code MethodLevelJob} instance object is searched from the Spring container.
     *
     * @param declaringClassName {@inheritDoc}
     * @param methodName         {@inheritDoc}
     * @return a {@code MethodLevelJob} instance gets from {@link #applicationContext}.
     */
    @Override
    protected MethodLevelJob getJob(String declaringClassName, String methodName) {
        final String beanName = methodName + "@" + declaringClassName;
        if (applicationContext.containsBean(beanName)) {
            return applicationContext.getBean(beanName, MethodLevelJob.class);
        }
        Class<?> beanClassName = ClassUtils.resolveClassName(declaringClassName, classLoader);
        Object targetBean = applicationContext.getBean(beanClassName);
        Method method = ClassUtils.getMethod(beanClassName, methodName);
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
