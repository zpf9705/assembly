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
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.springframework.core.env.Environment;
import top.osjf.cron.core.repository.CronTaskRepository;
import top.osjf.cron.spring.AbstractCronTaskRegistrant;
import top.osjf.cron.spring.annotation.Cron;
import top.osjf.cron.spring.annotation.CronAnnotationAttributes;

/**
 * Quartz of scheduled task registration actors.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
public class QuartzCronTaskRegistrant extends AbstractCronTaskRegistrant {

    public QuartzCronTaskRegistrant(CronTaskRepository<JobKey, JobDetail> cronTaskRepository) {
        super(cronTaskRepository);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void register(Class<?> realBeanType, Object bean, Environment environment) throws Exception {
        if (!realBeanType.isAnnotationPresent(Cron.class)
                || !Job.class.isAssignableFrom(realBeanType)) {
            return;
        }
        CronAnnotationAttributes cronAttribute = getCronAttribute(realBeanType);
        String[] activeProfiles = environment.getActiveProfiles();
        if (!profilesCheck(cronAttribute.getProfiles(), activeProfiles)) {
            return;
        }
        CronTaskRepository<JobKey, JobDetail> cronTaskRepository = getCronTaskRepository();
        cronTaskRepository.register(cronAttribute.getExpression(),
                JobBuilder.newJob((Class<? extends Job>) realBeanType).build());
    }
}
