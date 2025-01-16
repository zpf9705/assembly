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


package top.osjf.cron.quartz.repository;

import org.quartz.CronExpression;
import org.quartz.CronScheduleBuilder;

import java.text.ParseException;

/**
 * {@code VisibleCronScheduleBuilder} is an extension implementation of
 * {@code CronScheduleBuilder} that visualizes the retrieval of {@code CronExpression}.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.3
 */
public class VisibleCronScheduleBuilder extends CronScheduleBuilder {
    private final CronExpression cronExpression;

    protected VisibleCronScheduleBuilder(CronExpression cronExpression) {
        super(cronExpression);
        this.cronExpression = cronExpression;
    }

    /**
     * Returns a {@code CronExpression} obj by {@code VisibleCronScheduleBuilder}.
     *
     * @return a {@code CronExpression} obj.
     */
    public CronExpression getCronExpression() {
        return cronExpression;
    }

    /**
     * Create a VisibleCronScheduleBuilder with the given cron-expression string -
     * which is presumed to be a valid cron expression (and hence only a
     * RuntimeException will be thrown if it is not).
     *
     * @param cronExpression the cron expression string to base the schedule on.
     * @return the new CronScheduleBuilder
     * @throws RuntimeException wrapping a ParseException if the expression is invalid
     * @see CronExpression
     */
    public static VisibleCronScheduleBuilder cronSchedule(String cronExpression) {
        try {
            return new VisibleCronScheduleBuilder(new CronExpression(cronExpression));
        } catch (ParseException e) {
            // all methods of construction ensure the expression is valid by
            // this point...
            throw new RuntimeException("CronExpression '" + cronExpression
                    + "' is invalid.", e);
        }
    }
}
