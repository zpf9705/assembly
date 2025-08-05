/*
 * Copyright 2025-? the original author or authors.
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


package top.osjf.spring.autoconfigure.filewatch.application.startup;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * File-watch application startup properties.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.1
 */
@ConfigurationProperties(prefix = "file-watch.application.startup")
public class FileWatchApplicationStartupProperties {

    /**
     * The jar file name that the listener needs to listen to changes in.
     */
    private String jarFileName = "application.jar";

    /**
     * What changes occur to the file corresponding to {@link #jarFileName} that
     * trigger the enumeration type of {@link #sortedStartupCommands}.
     */
    private TriggerKind triggerKind = TriggerKind.ALL;

    /**
     * Specify the collection of shell commands to be executed in the order
     * of starting when monitoring changes in jar files.
     */
    private List<String> sortedStartupCommands = new ArrayList<>();

    /**
     * Specify the timeout duration for execution.
     */
    private long timeout = 60;

    /**
     * Enumeration of timeout duration units for specified execution.
     */
    private TimeUnit unit = TimeUnit.SECONDS;

    public String getJarFileName() {
        return jarFileName;
    }

    public void setJarFileName(String jarFileName) {
        this.jarFileName = jarFileName;
    }

    public TriggerKind getTriggerKind() {
        return triggerKind;
    }

    public void setTriggerKind(TriggerKind triggerKind) {
        this.triggerKind = triggerKind;
    }

    public List<String> getSortedStartupCommands() {
        return sortedStartupCommands;
    }

    public void setSortedStartupCommands(List<String> sortedStartupCommands) {
        this.sortedStartupCommands = sortedStartupCommands;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public TimeUnit getUnit() {
        return unit;
    }

    public void setUnit(TimeUnit unit) {
        this.unit = unit;
    }
}
