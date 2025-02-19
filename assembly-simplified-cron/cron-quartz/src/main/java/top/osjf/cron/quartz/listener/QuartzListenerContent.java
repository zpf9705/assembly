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
import org.quartz.JobKey;
import top.osjf.cron.core.listener.ListenerContext;
import top.osjf.cron.quartz.QuartzUtils;

/**
 * The listening context object of {@code Quartz}.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.3
 */
public class QuartzListenerContent implements ListenerContext {

    private final String id;
    private final JobExecutionContext context;

    /**
     * Creates a {@code QuartzListenerContent} by given {@code TaskExecutor}.
     * @param context the Quartz scheduler listener obj.
     */
    public QuartzListenerContent(JobExecutionContext context) {
        JobKey key = context.getJobDetail().getKey();
        this.id = QuartzUtils.getIdBySerializeJobKey(key);
        this.context = context;
    }

    @Override
    public String getID() {
        return id;
    }

    @Override
    public Object getSourceContext() {
        return context;
    }
}
