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

import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ReflectUtil;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.spi.JobFactory;
import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.NonNull;

/**
 * Custom Quartz {@link JobFactory} retrieves objects from Spring's
 * context container based on the filled {@link Job} type, ensuring
 * unique reuse of the objects.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
public class QuartzJobFactory implements JobFactory, ApplicationContextAware {

    private ApplicationContext applicationContext;

    private BeanDefinitionRegistry beanDefinitionRegistry;

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {

        this.applicationContext = applicationContext;

        this.beanDefinitionRegistry = (BeanDefinitionRegistry) applicationContext;
    }

    @Override
    public Job newJob(TriggerFiredBundle bundle, Scheduler scheduler) {
        JobDetail jobDetail = bundle.getJobDetail();
        if (MethodJob.class.isAssignableFrom(jobDetail.getJobClass())) {
            return getMethodJobBean(jobDetail);
        }
        return applicationContext.getBean(jobDetail.getJobClass());
    }

    DynamicMethodJob getMethodJobBean(JobDetail jobDetail) {

        JobKey key = jobDetail.getKey();

        String beanName = key.toString();

        if (applicationContext.containsBean(beanName)) {

            return applicationContext.getBean(beanName, DynamicMethodJob.class);
        }

        return registerAndInitialize(key, beanName);
    }

    DynamicMethodJob registerAndInitialize(JobKey key, String beanName) {

        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(DynamicMethodJob.class)
                .setScope(BeanDefinition.SCOPE_SINGLETON)
                .addConstructorArgValue(getRunnable(key.getGroup(), key.getName()));

        BeanDefinitionReaderUtils.registerBeanDefinition(new BeanDefinitionHolder(builder.getBeanDefinition(),
                beanName), beanDefinitionRegistry);

        return applicationContext.getBean(beanName, DynamicMethodJob.class);
    }

    Runnable getRunnable(String beanClassName, String methodName) {

        return () -> ReflectUtil.invoke(applicationContext.getBean(ClassUtil.loadClass(beanClassName)), methodName);
    }
}
