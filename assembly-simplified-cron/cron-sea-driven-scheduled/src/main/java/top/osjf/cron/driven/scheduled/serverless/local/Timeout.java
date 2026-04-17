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


package top.osjf.cron.driven.scheduled.serverless.local;

import top.osjf.cron.core.util.AssertUtils;

import java.util.concurrent.TimeUnit;

/**
 * A timeout value wrapper that holds a duration and its time unit.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.2
 */
public class Timeout {

    private final long duration;

    private final TimeUnit timeUnit;

    /**
     * Constructs a MillTimeout with given duration and time unit.
     * @param duration the time duration
     * @param timeUnit the time unit
     */
    public Timeout(long duration, TimeUnit timeUnit) {
        AssertUtils.assertTrue( duration >= 0, "Duration must be greater than 0");
        AssertUtils.assertNotNull( timeUnit, "TimeUnit can not be null");
        this.duration = duration;
        this.timeUnit = timeUnit;
    }

    public long getDuration() {
        return duration;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }
}
