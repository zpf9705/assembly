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

package top.osjf.cron.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;
import top.osjf.cron.hutool.lifestyle.HutoolCronLifeStyle;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TimeZone;

/**
 * Cron schedule properties.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.1
 */
@ConfigurationProperties(prefix = "spring.schedule.cron")
public class CronProperties {

    /**
     * Type of client to use. By default, auto-detected according to the classpath.
     */
    private ClientType clientType;

    private final Hutool hutool = new Hutool();

    private final Quartz quartz = new Quartz();

    private final Cron4j cron4j = new Cron4j();

    public ClientType getClientType() {
        return clientType;
    }

    public void setClientType(ClientType clientType) {
        this.clientType = clientType;
    }

    public Hutool getHutool() {
        return hutool;
    }

    public Quartz getQuartz() {
        return quartz;
    }

    public Cron4j getCron4j() {
        return cron4j;
    }

    private Map<String, Object> withClientToMetadata(ClientType clientType) {
        if (this.clientType != null) clientType = this.clientType;
        switch (clientType) {
            case HUTOOL:
                return hutool.toMetadata();
            case CRON4J:
                return cron4j.toMetadata();
            default:
                return null;
        }
    }

    /**
     * Type of Cron client to use.
     */
    public enum ClientType {

        /**
         * Use the hutool cron client.
         */
        HUTOOL,

        /**
         * Use the quartz cron client.
         */
        QUARTZ,

        /**
         * Use the cron4j cron client.
         */
        CRON4J
    }

    public interface MetadataConvert {

        Map<String, Object> toMetadata();
    }

    /**
     * Hutool client properties.
     */
    public static class Hutool implements MetadataConvert {

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

        public boolean isMatchSecond() {
            return isMatchSecond;
        }

        public void setMatchSecond(boolean matchSecond) {
            isMatchSecond = matchSecond;
        }

        public boolean isDaemon() {
            return isDaemon;
        }

        public void setDaemon(boolean daemon) {
            isDaemon = daemon;
        }

        @Override
        public Map<String, Object> toMetadata() {
            Map<String, Object> metadata = new LinkedHashMap<>();
            metadata.put("isMatchSecond", isMatchSecond);
            metadata.put("isDaemon", isDaemon);
            return metadata;
        }
    }

    /**
     * Quartz client properties.
     */
    public static class Quartz {

        /**
         * Additional Quartz Scheduler properties.
         */
        private final Map<String, String> properties = new HashMap<>();

        public Map<String, String> getProperties() {
            return properties;
        }
    }

    /**
     * Cron4j client properties.
     */
    public static class Cron4j implements MetadataConvert {

        /**
         * The daemon flag. If true the scheduler and its spawned threads acts like
         * daemons.
         */
        private boolean daemon = false;

        /**
         * The time zone applied by the scheduler.
         */
        private TimeZone timezone = TimeZone.getDefault();

        public boolean isDaemon() {
            return daemon;
        }

        public void setDaemon(boolean daemon) {
            this.daemon = daemon;
        }

        public TimeZone getTimezone() {
            return timezone;
        }

        public void setTimezone(TimeZone timezone) {
            this.timezone = timezone;
        }

        @Override
        public Map<String, Object> toMetadata() {
            Map<String, Object> metadata = new LinkedHashMap<>();
            metadata.put("timezone", timezone);
            metadata.put("daemon", daemon);
            return metadata;
        }
    }
}
