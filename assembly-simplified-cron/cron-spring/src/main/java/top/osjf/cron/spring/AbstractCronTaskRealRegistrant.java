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

package top.osjf.cron.spring;

import top.osjf.cron.core.repository.CronTaskRepository;

/**
 * Abstract {@link CronTaskRealRegistrant} provides some common constructs
 * and methods for subclasses.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
@SuppressWarnings("rawtypes")
public abstract class AbstractCronTaskRealRegistrant implements CronTaskRealRegistrant {

    private final CronTaskRepository cronTaskRepository;

    /**
     * Construct within {@link CronTaskRepository} to be a {@link CronTaskRealRegistrant}.
     *
     * @param cronTaskRepository Scheduled task resource operation class.
     */
    public AbstractCronTaskRealRegistrant(CronTaskRepository cronTaskRepository) {
        this.cronTaskRepository = cronTaskRepository;
    }

    /**
     * Return the timed task resource operation interface.
     *
     * @param <T> Specific types of resources.
     * @return timed task resource operation interface.
     */
    @SuppressWarnings("unchecked")
    public <T extends CronTaskRepository> T getCronTaskRepository() {
        return (T) cronTaskRepository;
    }
}
