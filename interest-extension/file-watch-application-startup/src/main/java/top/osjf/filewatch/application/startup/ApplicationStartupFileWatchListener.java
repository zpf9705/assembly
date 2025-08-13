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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.osjf.filewatch.AmapleWatchEvent;
import top.osjf.filewatch.AmpleFileWatchListener;
import top.osjf.filewatch.TriggerKind;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Listener for monitoring JAR file changes and triggering application startup commands.
 *
 * <p>When the target JAR file is modified/created/deleted (configurable via {@link TriggerKind}),
 * this listener executes predefined shell commands to restart the application.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.1
 */
public class ApplicationStartupFileWatchListener extends AmpleFileWatchListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationStartupFileWatchListener.class);

    private Map<Path, Map<Path, StartupJarElement>> startupJarElementMap;

    /**
     * Constructs a new {@code ApplicationStartupFileWatchListener}.
     * @param elements the specify file-watch application startup properties.
     */
    public ApplicationStartupFileWatchListener(List<StartupJarElement> elements) {
        initStartupJarElementMap(elements);
    }

    void initStartupJarElementMap(List<StartupJarElement> elements) {
        startupJarElementMap = elements.stream()
                .collect(Collectors.groupingByConcurrent(startupJarElement -> Paths.get(startupJarElement.getBindPath()),
                        Collectors.toMap(startupJarElement -> Paths.get(startupJarElement.getJarFileName()),
                                Function.identity())));
    }

    /**
     * {@inheritDoc}
     * Determines if this listener supports the given watch event.
     */
    @Override
    public boolean supportsInternal(AmapleWatchEvent watchEvent) {
        Path parent = watchEvent.getParent();
        Path jarFilePath = watchEvent.context();

        // First, find the main listening address, and then configure it according to the jar file.
        Map<Path, StartupJarElement> jarGroup = startupJarElementMap.get(parent);
        StartupJarElement jarElement;
        if (jarGroup == null || (jarElement = jarGroup.get(jarFilePath)) == null) {
            return false;
        }

        // Verify if the binding listening address is consistent.
        boolean bindPathEq = parent.equals(Paths.get(jarElement.getBindPath()));

        // Verify whether the trigger type set in the current subclass file is
        // within the range of the parent listening trigger.
        boolean triggerKindEq = Arrays.asList(jarElement.getTriggerKinds()).contains(watchEvent.getTriggerKind());

        return bindPathEq && triggerKindEq;
    }

    /**
     * {@inheritDoc}
     * Handles the file watch event by executing configured startup commands.
     */
    @Override
    public void onWatchEventInternal(AmapleWatchEvent watchEvent) {
        Path parent = watchEvent.getParent();
        Path jarFilePath = watchEvent.context();

        StartupJarElement jarElement = startupJarElementMap.get(parent).get(jarFilePath);

        List<String> sortedStartupCommands = wrapWithBashShell(jarElement.getSortedStartupCommands());
        try {
            Process process = new ProcessBuilder(sortedStartupCommands).start();
            if (process.waitFor(jarElement.getTimeout(), jarElement.getUnit())) {
                // Read error stream before checking exit value
                String errorOutput = copyToString(process.getErrorStream());
                if (process.exitValue() == 0) {
                    LOGGER.info("Successfully launched {} via commands: {}", jarFilePath, sortedStartupCommands);
                }
                else {
                    LOGGER.info("Failed to start jar application {}, returning receipt error message {}.",
                            jarFilePath, errorOutput);
                }
            }
            else {
                process.destroyForcibly().waitFor(); //Force termination upon timeout.
                LOGGER.error("Command {} execution timeout", sortedStartupCommands);
            }
        }
        catch (IOException ex) {
            LOGGER.error("IO error executing commands {}", sortedStartupCommands, ex);
        }
        catch (InterruptedException ex) {
            Thread.currentThread().interrupt(); // Restore interrupt status.
            LOGGER.error("Command execution interrupted for {}", jarFilePath, ex);
        }
    }

    /**
     * Wrap the original command list in Bash shell execution format.
     * @param commands commands Original command list
     * @return The complete list of shell commands after packaging, in the
     * format of ["/bin/bash", "- c", "Command1&&Command2"]
     */
    private static List<String> wrapWithBashShell(List<String> commands) {
        if (commands == null || commands.isEmpty()) {
            return commands;
        }
        List<String> shellCommands = new ArrayList<>();
        shellCommands.add("/bin/bash");
        shellCommands.add("-c");
        shellCommands.add(String.join(" && ", commands));
        return shellCommands;
    }

    /**
     * Copy the contents of the given InputStream into a String.
     * <p>Leaves the stream open when done.
     *
     * @param in the InputStream to copy from (may be {@code null} or empty)
     * @return the String that has been copied to (possibly empty)
     * @throws IOException in case of I/O errors
     */
    private static String copyToString(InputStream in) throws IOException {
        if (in == null) {
            return "";
        }

        StringBuilder out = new StringBuilder(4096);
        InputStreamReader reader = new InputStreamReader(in, StandardCharsets.UTF_8);
        char[] buffer = new char[4096];
        int charsRead;
        while ((charsRead = reader.read(buffer)) != -1) {
            out.append(buffer, 0, charsRead);
        }
        return out.toString();
    }
}
