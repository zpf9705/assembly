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


package top.osjf.cron.cron4j.listener;

import it.sauronsoftware.cron4j.SchedulerListener;
import it.sauronsoftware.cron4j.TaskExecutor;
import top.osjf.cron.core.listener.CronListenerCollector;
import top.osjf.cron.core.listener.DefaultCronListenerCollector;
import top.osjf.cron.core.listener.ListenerContextTypeProvider;

/**
 * The default Cron4j task listener implementation class extends {@link CronListenerCollector}
 * to implement broadcast mode for {@link top.osjf.cron.core.listener.CronListener}.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.3
 */
@ListenerContextTypeProvider(Cron4jListenerContent.class)
public class SchedulerListenerImpl extends DefaultCronListenerCollector implements SchedulerListener {

    @Override
    public void taskLaunching(TaskExecutor executor) {
        doStartListener(executor);
    }

    @Override
    public void taskSucceeded(TaskExecutor executor) {
        doSuccessListener(executor);
    }

    @Override
    public void taskFailed(TaskExecutor executor, Throwable exception) {
        doFailedListener(executor, exception);
    }
}
