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

import java.util.concurrent.TimeUnit;

/**
 * A configuration object that specifies the timeout period for
 * task scheduling execution, including the processing strategy
 * after timeout.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.2
 */
public class RunningTimeout {

    /**
     * longest waiting time for a single trigger run timeout.
     */
    private final long timeout;

    /**
     * longest waiting time for a single trigger run timeout unit.
     */
    private final TimeUnit timeUnit;

    /**
     * the strategy after running timeout.
     */
    private final RunningTimeoutPolicy policy;

    /**
     * Construct an empty {@link RunningTimeout} with a default maximum runtime
     * of 1 hour, otherwise interrupt the operation.
     */
    public RunningTimeout() {
        this(1, TimeUnit.HOURS);
    }

    /**
     * Construct a {@link RunningTimeout} that interrupts runtime when it exceeds
     * the given maximum runtime and unit.
     *
     * @param timeout  longest waiting time for a single trigger run timeout.
     * @param timeUnit longest waiting time for a single trigger run timeout unit.
     */
    public RunningTimeout(long timeout, TimeUnit timeUnit) {
        this(timeout, timeUnit, RunningTimeoutPolicy.BREAK);
    }

    /**
     * Construct a {@link RunningTimeout} that interrupts runtime when it exceeds
     * the given maximum runtime and unit and timeout handling strategy.
     *
     * @param timeout  longest waiting time for a single trigger run timeout.
     * @param timeUnit longest waiting time for a single trigger run timeout unit.
     * @param policy   timeout handling strategy enumeration value.
     */
    public RunningTimeout(long timeout, TimeUnit timeUnit, RunningTimeoutPolicy policy) {
        this.timeout = timeout;
        this.timeUnit = timeUnit;
        this.policy = policy;
    }

    /**
     * @return longest waiting time for a single trigger run timeout.
     */
    public long getTimeout() {
        return timeout;
    }

    /**
     * @return longest waiting time for a single trigger run timeout unit.
     */
    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    /**
     * @return the strategy after running timeout.
     */
    public RunningTimeoutPolicy getPolicy() {
        return policy;
    }
}
