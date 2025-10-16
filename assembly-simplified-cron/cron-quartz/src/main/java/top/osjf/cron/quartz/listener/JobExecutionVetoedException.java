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


package top.osjf.cron.quartz.listener;

import org.quartz.SchedulerException;

/**
 * An exception thrown when the execution of a job is rejected. This exception is usually
 * triggered by the scheduler when the {@link org.quartz.TriggerListener#vetoJobExecution}
 * method returns true,
 *
 * <p>This exception type is used to notify the system that the execution of this job has been
 * blocked, which is part of the normal control process and not an error situation.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.1
 */
public class JobExecutionVetoedException extends SchedulerException {
    private static final long serialVersionUID = -7791668488556391297L;

    /*
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     *
     * Constructors.
     *
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */

    public JobExecutionVetoedException() {
        super();
    }
}
