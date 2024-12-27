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

import org.quartz.JobDetail;
import top.osjf.cron.core.ExpressionRegistrant;

/**
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.3
 */
public class QuartzRegistrant extends ExpressionRegistrant {

    private final JobDetail jobDetail;

    public QuartzRegistrant(String cronExpression, JobDetail jobDetail) {
        super(cronExpression);
        this.jobDetail = jobDetail;
    }

    public JobDetail getJobDetail() {
        return jobDetail;
    }
}
