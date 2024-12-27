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
import top.osjf.cron.core.lifestyle.LifeStyle;
import top.osjf.cron.core.lifestyle.StartupProperties;

import java.util.Objects;

/**
 * The Cron4j cron task {@link LifeStyle} impl.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
public class Cron4jCronLifeStyle implements LifeStyle {

    /*** scheduler management*/
    private final Scheduler scheduler;

    /*** The construction method of scheduler management class {@link Scheduler}
     * @param scheduler scheduler management.
     **/
    public Cron4jCronLifeStyle(Scheduler scheduler) {
        Objects.requireNonNull(scheduler, "Cron4j Scheduler");
        this.scheduler = scheduler;
    }

    @Override
    public void start(StartupProperties properties) {
        Cron4jCronStartupArgs startupArgs = Cron4jCronStartupArgs.of(properties.asProperties());
        scheduler.setDaemon(startupArgs.isDaemon());
        scheduler.setTimeZone(startupArgs.getTimezone());
        scheduler.start();
    }

    @Override
    public void stop() {
        scheduler.stop();
    }

    @Override
    public boolean isStarted() {
        return scheduler.isStarted();
    }
}
