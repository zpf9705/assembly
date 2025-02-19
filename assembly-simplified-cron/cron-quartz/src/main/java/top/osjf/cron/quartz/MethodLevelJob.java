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

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import top.osjf.cron.core.repository.CronMethodRunnable;

import java.lang.reflect.Method;

/**
 * Method level execution {@link Job}, including the target and method
 * to build a new {@link CronMethodRunnable}, directly executes this
 * task using {@link CronMethodRunnable#run()}.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.3
 */
public class MethodLevelJob implements Job {
    private final CronMethodRunnable cronMethodRunnable;

    /**
     * Creates a {@code MethodLevelJob} by given target object and
     * {@code Method} instance.
     *
     * @param target the given target object.
     * @param method the given {@code Method} instance.
     */
    public MethodLevelJob(Object target, Method method) {
        this.cronMethodRunnable = new CronMethodRunnable(target, method);
    }

    /**
     * Return a {@code CronMethodRunnable} building by target
     * object and {@code Method} instance.
     *
     * @return a {@code CronMethodRunnable} instance.
     */
    public CronMethodRunnable getCronMethodRunnable() {
        return cronMethodRunnable;
    }

    @Override
    public void execute(JobExecutionContext context) {
        cronMethodRunnable.run();
    }
}
