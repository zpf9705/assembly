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

import java.util.Properties;
import java.util.TimeZone;

/**
 * The Cron4j cron task Startup Args.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
public class Cron4jCronStartupArgs {

    /**
     * The daemon flag. If true the scheduler and its spawned threads acts like
     * daemons.
     */
    private boolean daemon = false;

    /**
     * The time zone applied by the scheduler.
     */
    private TimeZone timezone = TimeZone.getDefault();

    /**
     * Creates a new instance of {@link Cron4jCronStartupArgs} initialized with the specified properties.
     *
     * <p>This static factory method creates a new {@link Cron4jCronStartupArgs} object and initializes
     * its fields based on the given
     * {@link Properties} object. The following properties are recognized and used to configure the startup
     * arguments:</p>
     *
     * <ul>
     *     <li><strong>daemon</strong> (optional, boolean): Indicates whether the cron job should run as a
     *     daemon process. If not specified,
     *     defaults to <code>false</code>.</li>
     *     <li><strong>timezone</strong> (optional, {@link TimeZone}): Specifies the time zone in which the
     *     cron job should operate. If not
     *     specified, defaults to the system's default time zone.</li>
     * </ul>
     *
     * @param properties The {@link Properties} object containing the configuration settings for the
     *                   cron job startup.
     * @return A newly created and initialized {@link Cron4jCronStartupArgs} instance.
     * @throws ClassCastException   If the value for the "daemon" property cannot be cast to a {@code boolean}.
     * @throws NullPointerException If the value for the "timezone" property is null and cannot be defaulted
     *                              to the system's default time zone.(Note: This should not happen with the current
     *                              implementation, but is included for completeness.)
     */
    public static Cron4jCronStartupArgs of(Properties properties) {
        Cron4jCronStartupArgs startupArgs = new Cron4jCronStartupArgs();
        startupArgs.daemon = (boolean) properties.getOrDefault("daemon", false);
        startupArgs.timezone = (TimeZone) properties.getOrDefault("timezone", TimeZone.getDefault());
        return startupArgs;
    }

    public boolean isDaemon() {
        return daemon;
    }

    public TimeZone getTimezone() {
        return timezone;
    }
}
