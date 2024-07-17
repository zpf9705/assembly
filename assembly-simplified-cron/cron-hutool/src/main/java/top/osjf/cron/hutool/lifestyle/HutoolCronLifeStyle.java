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
import top.osjf.cron.core.lifestyle.StartupArgs;

/**
 * The Hutool cron task {@link LifeStyle} impl.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
public class HutoolCronLifeStyle implements LifeStyle {

    @Override
    public void start(StartupArgs startupArgs) throws CronLifeStyleException {
        doLifeStyle(() -> {
            boolean isMatchSecond = true;
            boolean isDaemon = false;
            if (startupArgs instanceof HutoolCronStartupArgs) {
                isMatchSecond = ((HutoolCronStartupArgs) startupArgs).isMatchSecond();
                isDaemon = ((HutoolCronStartupArgs) startupArgs).isDaemon();
            }
            CronUtil.setMatchSecond(isMatchSecond);
            CronUtil.start(isDaemon);
        });
    }

    @Override
    public void restart() throws CronLifeStyleException {
        doLifeStyle(CronUtil::restart);
    }

    @Override
    public void stop() throws CronLifeStyleException {
        doLifeStyle(CronUtil::stop);
    }

    public void doLifeStyle(Runnable runnable) throws CronLifeStyleException {
        try {
            runnable.run();
        } catch (Throwable e) {
            throw new CronLifeStyleException(e);
        }
    }
}
