/*
 * Copyright 2025-? the original author or authors.
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


package top.osjf.cron.quartz.repository;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import top.osjf.cron.core.lang.NotNull;

/**
 * {@code RunnableJob} is an implementation class that encapsulates a
 * single {@link Runnable} execution of {@link Job}.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.2
 */
public class RunnableJob implements Job {

    @NotNull private final Runnable runnable;

    /**
     * Creates a {@code RunnableJob} by given {@code Runnable}.
     * @param runnable the given {@code Runnable}.
     */
    public RunnableJob(@NotNull Runnable runnable) {
        this.runnable = runnable;
    }


    @Override
    public void execute(JobExecutionContext context) {
        runnable.run();
    }
}
