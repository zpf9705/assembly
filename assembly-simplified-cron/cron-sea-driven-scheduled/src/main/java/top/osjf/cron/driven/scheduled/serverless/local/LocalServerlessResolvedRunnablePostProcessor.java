/*
 * Copyright 2026-? the original author or authors.
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


package top.osjf.cron.driven.scheduled.serverless.local;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.osjf.cron.core.lang.Nullable;
import top.osjf.cron.core.util.AssertUtils;
import top.osjf.cron.core.util.StringUtils;
import top.osjf.cron.datasource.driven.scheduled.ResolvedRunnablePostProcessor;
import top.osjf.cron.datasource.driven.scheduled.TaskElement;
import top.osjf.cron.driven.scheduled.serverless.DefaultTaskParameterRegistry;
import top.osjf.cron.driven.scheduled.serverless.ParameterHelp;
import top.osjf.cron.driven.scheduled.serverless.TaskParameter;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Local serverless architecture Runnable post-processor {@link ResolvedRunnablePostProcessor}.
 * Responsible for binding function JAR files and execution timeout for each task,
 * then wrapping and generating a serverless task Runnable that can run locally.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.2
 */
public class LocalServerlessResolvedRunnablePostProcessor
        extends DefaultTaskParameterRegistry implements ResolvedRunnablePostProcessor {

    /**
     * Task ID -> Function execution JAR file mapping
     * Used for caching task-specific JAR files for dynamic loading during runtime
     */
    private final ConcurrentHashMap<String, File> taskFunctionJarFileMapping = new ConcurrentHashMap<>();

    /**
     * Task ID -> Task execution timeout mapping
     * Each task can be configured with an independent timeout control
     */
    private final ConcurrentHashMap<String, Timeout> taskProcessTimeoutMapping = new ConcurrentHashMap<>();

    /**
     * Default global function JAR file
     * Used when a task does not specify a dedicated JAR file
     */
    private File defaultFunctionJarFile;

    /**
     * Default task execution timeout (30 minutes)
     * Used when a task does not specify an independent timeout
     */
    private Timeout defaultProcessTimeout = new Timeout(30, TimeUnit.MINUTES);

    /**
     * Add a dedicated function JAR file for the specified task
     * Automatically validates file existence, readability, and valid JAR format
     *
     * @param taskId task ID
     * @param functionJarFile function JAR file required for task execution
     */
    public void addTaskFunctionJarFile(String taskId, File functionJarFile) {
        AssertUtils.assertNotBlank(taskId, "TaskId cannot be blank");
        AssertUtils.assertNotNull(functionJarFile, "FunctionJarFile cannot be null");
        AssertUtils.assertTrue(functionJarFile.exists(), "Jar file does not exist: " + functionJarFile);
        AssertUtils.assertTrue(functionJarFile.isFile(), "Path is not a valid file: " + functionJarFile);
        AssertUtils.assertTrue(functionJarFile.canRead(), "Jar file cannot be read: " + functionJarFile);

        // Verify if it is a valid Jar file (suffix+file header)
        AssertUtils.assertTrue(isValidJarFile(functionJarFile),
                "Invalid JAR file, only standard JAR files are supported: " + functionJarFile.getName());

        taskFunctionJarFileMapping.put(taskId, functionJarFile);
    }

    /**
     * Verify if the file is a valid standard JAR file
     * Validation: .jar suffix + ZIP/JAR magic number 50 4B 03 04
     *
     * @param file file to validate
     * @return true=valid JAR, false=invalid file
     */
    private boolean isValidJarFile(File file) {
        if (!file.getName().toLowerCase().endsWith(".jar")) {
            return false;
        }
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] header = new byte[4];
            if (fis.read(header) < 4) {
                return false;
            }
            // JAR / ZIP magic number
            return header[0] == 0x50 && header[1] == 0x4B && header[2] == 0x03 && header[3] == 0x04;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Set independent execution timeout for the specified task
     *
     * @param taskId task ID
     * @param processTimeout task execution timeout object
     */
    public void addTaskProcessTimeout(String taskId, Timeout processTimeout) {
        AssertUtils.assertNotBlank(taskId, "TaskId cannot be blank");
        AssertUtils.assertNotNull(processTimeout, "ProcessTimeout cannot be null");
        taskProcessTimeoutMapping.put(taskId, processTimeout);
    }

    /**
     * Set global default function JAR file
     * All tasks without a dedicated JAR will use this default JAR for execution
     * @param defaultFunctionJarFile the default function JAR file
     */
    public void setDefaultFunctionJarFile(File defaultFunctionJarFile) {
        AssertUtils.assertNotNull(defaultFunctionJarFile, "DefaultFunctionJarFile can not be null");
        this.defaultFunctionJarFile = defaultFunctionJarFile;
    }

    /**
     * Set global default task execution timeout
     * @param defaultProcessTimeout the default timeout object
     */
    public void setDefaultProcessTimeout(Timeout defaultProcessTimeout) {
        AssertUtils.assertNotNull(defaultProcessTimeout, "DefaultProcessTimeout can not be null");
        this.defaultProcessTimeout = defaultProcessTimeout;
    }

    /**
     * Get the required executable JAR file for the task (non-null)
     * First try task-specific JAR, then fall back to global default JAR
     *
     * @param taskId unique task identifier
     * @return valid executable JAR file
     * @throws IllegalArgumentException if no valid executable JAR file
     */
    private File getRequiredFunctionJarFile(String taskId) {
        File functionJarFile = taskFunctionJarFileMapping.getOrDefault(taskId, defaultFunctionJarFile);
        AssertUtils.assertNotNull(functionJarFile, "Task "+ taskId
                +" has no executable function jar file");
        return functionJarFile;
    }


    /**
     * Get task execution timeout
     * Priority: task-specific config > global default
     *
     * @param taskId unique task identifier
     * @return task timeout object
     */
    private Timeout getProcessTimeout(String taskId) {
        return taskProcessTimeoutMapping.getOrDefault(taskId, defaultProcessTimeout);
    }


    /**
     * Post-process: wrap the original Runnable, inject JAR file and timeout config
     * Return a Runnable that can run directly in the local serverless environment
     *
     * @param resolvedRunnable original resolved Runnable
     * @param taskElement      task metadata
     * @return wrapped local serverless Runnable
     */
    @Override
    public Runnable postProcessResolvedRunnable(Runnable resolvedRunnable, TaskElement taskElement) {
        String id = taskElement.getId();
        return new LocalServerlessRunnable(resolvedRunnable, taskElement,
                getRequiredFunctionJarFile(id), getProcessTimeout(id), getTaskParameter(id));
    }

    private static class LocalServerlessRunnable implements Runnable {

        private static final Logger LOGGER = LoggerFactory.getLogger(LocalServerlessRunnable.class);

        @SuppressWarnings("unused") private final Runnable resolvedRunnable;

        private final TaskElement taskElement;

        private final File functionJarFile;

        private final Timeout processTimeout;

        private final TaskParameter taskParameter;

        public LocalServerlessRunnable(Runnable resolvedRunnable, TaskElement taskElement, File functionJarFile,
                                       Timeout processTimeout, TaskParameter taskParameter) {
            this.resolvedRunnable = resolvedRunnable;
            this.taskElement = taskElement;
            this.functionJarFile = functionJarFile;
            this.processTimeout = processTimeout;
            this.taskParameter = taskParameter;
        }

        @Override
        public void run() {

            String parameter = ParameterHelp.resolveJarStartupParameter(taskParameter);

            String applicationStartupCommand = buildStartupCommand(parameter);

            try {
                LOGGER.info("Task function [{} - {}] && parameter [{}] start execute command [{}].",
                        taskElement.getId(), taskElement.getTaskName(), taskParameter, applicationStartupCommand);

                Process process = new ProcessBuilder(wrapWithBashShell(Collections.singletonList(applicationStartupCommand)))
                        .start();
                if (process.waitFor(processTimeout.getDuration(), processTimeout.getTimeUnit())) {
                    if (process.exitValue() == 0) {
                        LOGGER.info("Task function [{} - {}] && parameter [{}] && command [{}] has been successfully executed.",
                                taskElement.getId(), taskElement.getTaskName(), taskParameter, applicationStartupCommand);
                    }
                    else {
                        String errorOutput = copyToString(process.getErrorStream());
                        LOGGER.error("Failed to execute task function [{} - {}] && parameter [{}] && command [{}], error message [{}]",
                                taskElement.getId(), taskElement.getTaskName(), taskParameter,
                                applicationStartupCommand, errorOutput);
                    }
                }
                else {
                    process.destroyForcibly().waitFor();
                    LOGGER.error("Task function [{} - {}] && parameter [{}] && command [{}] execution timeout",
                            taskElement.getId(), taskElement.getTaskName(), taskParameter, applicationStartupCommand);
                }
            }
            catch (IOException ex) {
                LOGGER.error("IO error occurred during the execution of task function [{} - {}]/parameter [{}] && command [{}]",
                        taskElement.getId(), taskElement.getTaskName(), taskParameter, applicationStartupCommand, ex);
            }
            catch (InterruptedException ex) {
                Thread.currentThread().interrupt(); // Restore interrupt status.
                LOGGER.error("Interrupted occurred during the execution of task function [{} - {}]/parameter [{}] && command [{}]",
                        taskElement.getId(), taskElement.getTaskName(), taskParameter, applicationStartupCommand, ex);
            }
            finally {
                // Clear local parameters...
                if (getLocalTaskParameter() != null) {
                    setLocalTaskParameter(null);
                }
            }
        }

        /**
         * Build Java - jar xxx. jar -- param=xxx command
         * @param parameterStr the parameter.
         * @return java startup command.
         */
        private String buildStartupCommand(@Nullable String parameterStr) {
            StringBuilder sb = new StringBuilder();
            sb.append("java -jar ").append(functionJarFile.getPath());

            if (!StringUtils.isBlank(parameterStr)) {
                sb.append(" ").append(parameterStr);
            }

            return sb.toString();
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
}
