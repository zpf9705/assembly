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

import java.util.Properties;

/**
 * The Hutool cron task Startup Args.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
public class HutoolCronStartupArgs {

    /**
     * Set whether to support second matching.
     * <p>This method is used to define whether to use the second matching mode.
     * If it is true, the first digit in the timed task expression is seconds,
     * otherwise it is minutes, and the default is minutes.
     */
    private boolean isMatchSecond = true;

    /**
     * Whether to start as a daemon thread.
     * <p>If true, the scheduled task executed immediately after calling the
     * {@link HutoolCronLifeStyle#stop()} method will end.
     * Otherwise, it will wait for the execution to complete before ending.
     */
    private boolean isDaemon = false;

    /**
     * Creates a new instance of {@link HutoolCronStartupArgs} initialized with properties from the
     * given {@link Properties} object.
     * This method parses the provided properties to set the initial state of the startup arguments
     * for Hutool cron jobs.
     *
     * @param properties A {@link Properties} object containing the initial values for the startup arguments.
     *                   The properties should include "isMatchSecond" and "isDaemon" keys, with their corresponding
     *                   boolean values.If a property is not found, default values will be used: "isMatchSecond"
     *                   defaults to {@code true},and "isDaemon" defaults to {@code true}.
     * @return A new {@link HutoolCronStartupArgs} instance initialized with the properties from the given
     * {@link Properties} object.
     * @throws ClassCastException If the values for "isMatchSecond" or "isDaemon" cannot be cast to boolean.
     */
    public static HutoolCronStartupArgs of(Properties properties) {
        HutoolCronStartupArgs startupArgs = new HutoolCronStartupArgs();
        startupArgs.isMatchSecond = (boolean) properties.getOrDefault("isMatchSecond", true);
        startupArgs.isDaemon = (boolean) properties.getOrDefault("isDaemon", true);
        return startupArgs;
    }

    public boolean isMatchSecond() {
        return isMatchSecond;
    }

    public boolean isDaemon() {
        return isDaemon;
    }
}
