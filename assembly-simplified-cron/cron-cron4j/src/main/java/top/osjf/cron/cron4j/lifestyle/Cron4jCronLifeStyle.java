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

package top.osjf.cron.cron4j.lifestyle;

import it.sauronsoftware.cron4j.Scheduler;
import top.osjf.cron.core.annotation.NotNull;
import top.osjf.cron.core.exception.CronLifeStyleException;
import top.osjf.cron.core.lifestyle.LifeStyle;

/**
 * The Cron4j cron task {@link LifeStyle} impl.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
public class Cron4jCronLifeStyle implements LifeStyle {

    private final Scheduler scheduler;

    public Cron4jCronLifeStyle(@NotNull Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    @Override
    public void start(Object... args) throws CronLifeStyleException {
        Cron4jCronStartupArgs startupArgs = Cron4jCronStartupArgs.of(args);
        scheduler.setDaemon(startupArgs.isDaemon());
        scheduler.setTimeZone(startupArgs.getTimezone());
        doLifeStyle(scheduler::start);
    }

    @Override
    public void restart() throws CronLifeStyleException {
        doLifeStyle(() -> {
            if (scheduler.isStarted()) {
                stop();
            }
            start();
        });
    }

    @Override
    public void stop() throws CronLifeStyleException {
        doLifeStyle(scheduler::stop);
    }

    /**
     * Execute the default method of {@link LifeStyle}.
     *
     * @param runnable Running body.
     * @throws CronLifeStyleException The scheduled task
     *                                execution body stop abnormally.
     */
    public void doLifeStyle(Runnable runnable) throws CronLifeStyleException {
        try {
            runnable.run();
        } catch (Throwable e) {
            throw new CronLifeStyleException(e);
        }
    }
}
