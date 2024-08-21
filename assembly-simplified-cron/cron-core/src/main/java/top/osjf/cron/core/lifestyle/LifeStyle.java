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

package top.osjf.cron.core.lifestyle;

import top.osjf.cron.core.exception.CronLifeStyleException;

/**
 * The lifecycle definition interface for scheduled task execution
 * line pools or managed execution components.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
public interface LifeStyle {

    /**
     * Activate scheduled task execution.
     *
     * @param metadata Start the parameter.
     * @throws CronLifeStyleException The scheduled task
     *                                execution body started abnormally.
     */
    void start(StartupMetadata metadata) throws CronLifeStyleException;

    /**
     * Close the scheduled task to run the managed component.
     *
     * @throws CronLifeStyleException The scheduled task
     *                                execution body stop abnormally.
     */
    void stop() throws CronLifeStyleException;

    /**
     * Tests if this scheduler is started.
     *
     * @return true if the scheduler is started, false if it is stopped.
     */
    boolean isStarted();

    /**
     * Execute the default method of {@link LifeStyle}.
     *
     * @param runnable Running body.
     * @throws CronLifeStyleException The scheduled task
     *                                execution body stop abnormally.
     */
    default void doLifeStyle(Runnable runnable) throws CronLifeStyleException {
        try {
            runnable.run();
        } catch (Throwable e) {
            throw new CronLifeStyleException(e);
        }
    }
}
