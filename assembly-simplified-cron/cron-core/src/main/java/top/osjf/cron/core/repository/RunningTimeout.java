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

import top.osjf.cron.core.util.AssertUtils;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * A configuration object that specifies the timeout period for
 * task scheduling execution, including the processing strategy
 * after timeout.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.1
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
        this(timeout, timeUnit, RunningTimeoutPolicy.INTERRUPT);
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

        AssertUtils.assertTrue(timeout > 0, "Timeout cannot be less than or equal to 0");
        AssertUtils.assertNotNull(timeUnit, "TimeUnit not be null");
        AssertUtils.assertNotNull(policy, "RunningTimeoutPolicy not be null");

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RunningTimeout that = (RunningTimeout) o;
        return timeout == that.timeout && timeUnit == that.timeUnit && policy == that.policy;
    }

    @Override
    public int hashCode() {
        return Objects.hash(timeout, timeUnit, policy);
    }

    @Override
    public String toString() {
        return "RunningTimeout{" +
                "timeout=" + timeout +
                ", timeUnit=" + timeUnit +
                ", policy=" + policy +
                '}';
    }
}
