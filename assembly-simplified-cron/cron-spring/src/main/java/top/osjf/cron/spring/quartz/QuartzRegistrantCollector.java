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
import org.quartz.JobBuilder;
import org.springframework.core.env.Environment;
import top.osjf.cron.spring.AbstractRegistrantCollector;
import top.osjf.cron.spring.annotation.Cron;
import top.osjf.cron.spring.annotation.CronAnnotationAttributes;

/**
 * Quartz's implementation of {@link top.osjf.cron.spring.RegistrantCollector}.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
public class QuartzRegistrantCollector extends AbstractRegistrantCollector {

    @Override
    @SuppressWarnings("unchecked")
    public void add(Class<?> realBeanType, Object bean, Environment environment) {
        if (!realBeanType.isAnnotationPresent(Cron.class)
                || !Job.class.isAssignableFrom(realBeanType)) {
            return;
        }
        CronAnnotationAttributes cronAttribute = getCronAttribute(realBeanType);
        String[] activeProfiles = environment.getActiveProfiles();
        if (!profilesCheck(cronAttribute.getProfiles(), activeProfiles)) {
            return;
        }
        getRegistrants().add(new QuartzRegistrant(cronAttribute.getExpression(),
                JobBuilder.newJob((Class<? extends Job>) realBeanType).build()));
    }
}
