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


package top.osjf.cron.quartz.listener;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;
import top.osjf.cron.core.listener.CronListenerCollector;
import top.osjf.cron.core.listener.ListenerContextTypeProvider;

/**
 * The default Quartz task listener implementation class extends {@link CronListenerCollector}
 * to implement broadcast mode for {@link top.osjf.cron.core.listener.CronListener}.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.3
 */
@ListenerContextTypeProvider(QuartzListenerContent.class)
public class JobListenerImpl extends CronListenerCollector implements JobListener {
    @Override
    public String getName() {
        return this.getClass().getName();
    }

    @Override
    public void jobToBeExecuted(JobExecutionContext context) {
        doStartListener(context);
    }

    @Override
    public void jobExecutionVetoed(JobExecutionContext context) {
        // do noting....
    }

    @Override
    public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
        if (jobException != null) {
            doFailedListener(context, jobException);
        } else {
            doSuccessListener(context);
        }
    }
}
