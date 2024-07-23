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

package top.osjf.cron.quartz.lifestyle;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import top.osjf.cron.core.exception.CronLifeStyleException;
import top.osjf.cron.core.lifestyle.LifeStyle;

import java.util.Objects;

/**
 * The Quartz cron task {@link LifeStyle} impl.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
public class QuartzCronLifeStyle implements LifeStyle {

    /*** the scheduled task management class of Quartz.*/
    private final Scheduler scheduler;

    /**
     * Construct for create {@link QuartzCronLifeStyle} using a {@link Scheduler}.
     *
     * @param scheduler {@link Scheduler}.
     */
    public QuartzCronLifeStyle(Scheduler scheduler) {
        Objects.requireNonNull(scheduler, "Quartz Scheduler");
        this.scheduler = scheduler;
    }

    @Override
    public void start(Object... args) throws CronLifeStyleException {
        try {
            scheduler.start();
        } catch (SchedulerException e) {
            throw new CronLifeStyleException(e);
        }
    }

    @Override
    public void stop() throws CronLifeStyleException {
        try {
            scheduler.shutdown(true);
        } catch (SchedulerException e) {
            throw new CronLifeStyleException(e);
        }
    }

    @Override
    public boolean isStarted() {
        try {
            return scheduler.isStarted();
        } catch (SchedulerException e) {
            return false;
        }
    }
}
