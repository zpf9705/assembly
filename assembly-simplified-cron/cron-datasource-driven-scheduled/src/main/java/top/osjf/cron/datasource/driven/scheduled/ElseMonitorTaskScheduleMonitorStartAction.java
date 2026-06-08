/*
 * Copyright 2026-? the original author or authors.
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


package top.osjf.cron.datasource.driven.scheduled;

/**
 * Abstract class for additional monitor task schedule startup action.
 *
 * <p>Implements the monitor startup action interface of data-source-driven scheduled tasks.
 * Thread polling is disabled by default, and subclasses need to implement the specific
 * business logic for monitor startup.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.2
 */
public abstract class ElseMonitorTaskScheduleMonitorStartAction
        implements AbstractDatasourceDrivenScheduled.TaskScheduleMonitorStartAction {
    /**
     * Disable polling threads for monitoring task execution.
     * @return the {@code Boolean} flag of disable polling threads for monitoring task execution.
     */
    @Override
    public boolean useThreadPolling() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public abstract void elseMonitorStartAction();
}
