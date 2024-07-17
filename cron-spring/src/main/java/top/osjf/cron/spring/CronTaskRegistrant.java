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
 * The executor of the dynamic registration of scheduled tasks, developers
 * can call the method of this interface to register a scheduled task to
 * the current scheduled task center management at runtime.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
public interface CronTaskRegistrant {

    /**
     * Build a scheduled task that can be registered for central
     * processing using Cron standardized expressions and an
     * executable runtime {@link Runnable}.
     *
     * @param cronExpression Expression in {@code Cron} format.
     * @param runnable       Timed execution of the runtime.
     */
    void register(String cronExpression, Runnable runnable);
}
