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


package top.osjf.cron.core.repository;

import top.osjf.cron.core.exception.CronInternalException;

/**
 * Provides operations to manage registered scheduled tasks, including updating
 * the cron expression and removing tasks.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.1
 */
public interface ModifiableRepository extends Repository {

    /**
     * Update the cron expression for registered scheduled tasks.
     *
     * <p>This method takes the unique identifier of the task and a new cron expression
     * as input parameters, and updates the execution time of the task based on the new
     * cron expression.
     *
     * @param id            the Unique ID of the registered task.
     * @param newExpression a valid new cron expression.
     * @throws CronInternalException    if an unsupported or incorrect related exception occurs
     *                                  within the scheduling architecture.
     */
    void update(String id, String newExpression) throws CronInternalException;

    /**
     * Delete registered scheduled tasks.
     *
     * <p>This method receives the unique identifier of the task as an input parameter
     * and deletes the corresponding scheduled task based on that identifier.
     *
     * @param id the Unique ID of the registered task.
     * @throws CronInternalException    if an unsupported or incorrect related exception occurs
     *                                  within the scheduling architecture.
     */
    void remove(String id) throws CronInternalException;

    /**
     * Remove all registered cron tasks within the current repository and release all scheduling
     * resources.
     * @throws CronInternalException    if an unsupported or incorrect related exception occurs
     *                                  within the scheduling architecture.
     * @since 3.0.2
     */
    void removeAll() throws CronInternalException;
}
