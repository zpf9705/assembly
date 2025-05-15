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


package top.osjf.cron.datasource.driven.scheduled;

import java.util.Arrays;

/**
 * Enum class representing the current running status of a scheduled task.
 * This enum provides two statuses: ACTIVE (active) and PAUSED (paused).
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.4
 */
public enum Status {

    /**
     * Indicates that the scheduled task is currently in an active state,
     * meaning the task is running or about to run.
     */
    ACTIVE,

    /**
     * Indicates that the scheduled task is currently in a paused state,
     * meaning the task will not run until its status is changed to active.
     */
    PAUSED;

    /**
     * Checks if the given status string is a valid status.
     *
     * @param status The status string to check.
     * @return {@code true} if the status string is valid (i.e., matches one of the constants in the enum);
     * {@code false} otherwise.
     */
    public static boolean isStatus(String status) {
        return Arrays.stream(Status.values()).anyMatch(s -> s.name().equals(status));
    }

    /**
     * Checks if the given status string indicates an active state.
     *
     * @param status The status string to check.
     * @return true if the status string indicates an active state (i.e., matches {@link #ACTIVE});
     * false otherwise.
     */
    public static boolean isActive(String status) {
        return isStatus(status) && Status.ACTIVE.name().equals(status);
    }
}
