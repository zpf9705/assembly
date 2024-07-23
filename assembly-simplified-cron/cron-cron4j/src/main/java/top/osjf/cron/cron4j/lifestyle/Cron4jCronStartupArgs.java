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

import top.osjf.cron.core.util.ArrayUtils;

import java.util.Map;
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
     * Analyze startup parameters and construct startup parameter objects.
     *
     * <p>Compatible with two situations:
     * <ul>
     *     <li>If only one parameter is passed in and of type {@link Map}, then
     *     retrieve the value from this type.</li>
     *     <li>If there is a value that matches {@link #daemon} first and is
     *     greater than 1 value, then match {@link #timezone}.</li>
     * </ul>
     *
     * @param args the array parameters.
     * @return Analyze the results of parameter objects.
     */
    public static Cron4jCronStartupArgs of(Object[] args) {
        Cron4jCronStartupArgs startupArgs = new Cron4jCronStartupArgs();
        if (ArrayUtils.isEmpty(args)) return startupArgs;
        if (args.length == 1) {
            Object arg = args[0];
            if (arg instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> metadata = (Map<String, Object>) arg;
                startupArgs.daemon = !metadata.containsKey("daemon") ||
                        (boolean) metadata.get("daemon");
                startupArgs.timezone = !metadata.containsKey("timezone") ?
                        (TimeZone) metadata.get("timezone") : TimeZone.getDefault();
            } else {
                startupArgs.daemon = (boolean) arg;
            }
        } else {
            startupArgs.timezone = (TimeZone) args[0];
            startupArgs.daemon = (boolean) args[1];
        }
        return startupArgs;
    }

    public boolean isDaemon() {
        return daemon;
    }

    public TimeZone getTimezone() {
        return timezone;
    }
}
