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

package top.osjf.cron.hutool.lifestyle;

import cn.hutool.cron.Scheduler;
import top.osjf.cron.core.lifestyle.LifeStyle;
import top.osjf.cron.core.lifestyle.StartupProperties;

import java.util.Objects;

/**
 * The Hutool cron task {@link LifeStyle} impl.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
public class HutoolCronLifeStyle implements LifeStyle {

    private final Scheduler scheduler;

    /**
     * Creates a new {@code HutoolCronLifeStyle} by given {@link Scheduler} to
     * control the lifecycle of the task scheduler.
     *
     * @param scheduler the task scheduler.
     */
    public HutoolCronLifeStyle(Scheduler scheduler) {
        Objects.requireNonNull(scheduler, "<Scheduler> == null");
        this.scheduler = scheduler;
    }

    @Override
    public void start(StartupProperties properties) {
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
