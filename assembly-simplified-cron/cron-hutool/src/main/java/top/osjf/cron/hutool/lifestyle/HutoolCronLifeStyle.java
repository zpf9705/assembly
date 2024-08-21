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

import cn.hutool.cron.CronUtil;
import top.osjf.cron.core.exception.CronLifeStyleException;
import top.osjf.cron.core.lifestyle.LifeStyle;
import top.osjf.cron.core.lifestyle.StartupProperties;

/**
 * The Hutool cron task {@link LifeStyle} impl.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
public class HutoolCronLifeStyle implements LifeStyle {

    @Override
    public void start(StartupProperties properties) throws CronLifeStyleException {
        doLifeStyle(() -> {
            HutoolCronStartupArgs startupArgs = HutoolCronStartupArgs.of(properties.getStartUpProperties());
            CronUtil.setMatchSecond(startupArgs.isMatchSecond());
            CronUtil.start(startupArgs.isDaemon());
        });
    }

    @Override
    public void stop() throws CronLifeStyleException {
        doLifeStyle(CronUtil::stop);
    }

    @Override
    public boolean isStarted() {
        return CronUtil.getScheduler().isStarted();
    }
}
