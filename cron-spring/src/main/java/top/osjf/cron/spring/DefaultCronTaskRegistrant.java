/*
 * Copyright 2023-2024 the original author or authors.
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

package top.osjf.cron.spring;

/**
 * Default Impl for {@link CronTaskRegistrant}.
 *
 * <p>Reply for {@link CronTaskManager}.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 2.2.8
 */
public class DefaultCronTaskRegistrant implements CronTaskRegistrant {

    @Override
    public void register(String cronExpression, Runnable runnable) {
        CronTaskManager.registerCronTask(cronExpression, runnable);
    }
}
