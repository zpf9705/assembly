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


package top.osjf.filewatch.application.startup;

import top.osjf.filewatch.FileWatchPath;
import top.osjf.filewatch.TriggerKind;
import top.osjf.filewatch.TriggerKindProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * The information class entity launched by the jar package configuration includes
 * the jar package file name and execution instructions for system file listening,
 * inheriting {@code BindWaitConfiguration} and ensuring the stability of the jar
 * package transmission process.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.1
 */
public class StartupJarElement extends TriggerKindProvider {

    private static final long serialVersionUID = 8028606232869488345L;

    /**
     * The jar file name that the listener needs to listen to changes in.
     */
    private String jarFileName;

    /**
     * What changes occur to the file corresponding to {@link StartupJarElement#jarFileName} that
     * trigger the enumeration type of {@link #sortedStartupCommands}.
     * <p>The range of this configuration selection {@link TriggerKind} must be bound within the
     * {@link FileWatchPath#getKinds()} ()} configuration of the listening address {@link FileWatchPath#getPath()},
     * otherwise it will be considered an invalid configuration.
     */
    private TriggerKind[] triggerKinds
            = {TriggerKind.ENTRY_CREATE, TriggerKind.ENTRY_MODIFY, TriggerKind.ENTRY_DELETE};

    /**
     * Specify the collection of shell commands to be executed in the order
     * of starting when monitoring changes in jar files.
     */
    private List<String> sortedStartupCommands = new ArrayList<>();

    /**
     * Specify the timeout duration for execution.
     */
    private Long timeout = 60L;

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

    public TriggerKind[] getTriggerKinds() {
        return triggerKinds;
    }

    public void setTriggerKinds(TriggerKind[] triggerKinds) {
        this.triggerKinds = triggerKinds;
    }

    public List<String> getSortedStartupCommands() {
        return sortedStartupCommands;
    }

    public void setSortedStartupCommands(List<String> sortedStartupCommands) {
        this.sortedStartupCommands = sortedStartupCommands;
    }

    public Long getTimeout() {
        return timeout;
    }

    public void setTimeout(Long timeout) {
        this.timeout = timeout;
    }

    public TimeUnit getUnit() {
        return unit;
    }

    public void setUnit(TimeUnit unit) {
        this.unit = unit;
    }

    @Override
    public String getPathContext() {
        return jarFileName;
    }
}
