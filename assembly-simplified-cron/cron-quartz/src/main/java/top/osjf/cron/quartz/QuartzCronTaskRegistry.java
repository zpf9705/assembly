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

package top.osjf.cron.quartz;

import top.osjf.cron.core.CronTaskRegistry;
import top.osjf.cron.core.Registrant;
import top.osjf.cron.quartz.repository.QuartzCronTaskRepository;

/**
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.3
 */
public class QuartzCronTaskRegistry implements CronTaskRegistry {

    private final QuartzCronTaskRepository cronTaskRepository;

    public QuartzCronTaskRegistry(QuartzCronTaskRepository cronTaskRepository) {
        this.cronTaskRepository = cronTaskRepository;
    }

    @Override
    public boolean supports(Registrant registrant) {
        return registrant instanceof QuartzRegistrant;
    }

    @Override
    public void register(Registrant registrant) throws Exception {
        QuartzRegistrant quartzRegistrant = (QuartzRegistrant) registrant;
        cronTaskRepository.register(quartzRegistrant.getExpression(), quartzRegistrant.getJobDetail());
    }
}
