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


package top.osjf.cron.cron4j.repository;

import it.sauronsoftware.cron4j.InvalidPatternException;
import it.sauronsoftware.cron4j.Task;
import it.sauronsoftware.cron4j.TaskExecutionContext;
import top.osjf.cron.core.repository.CronTaskRepository;

/**
 * {@code Runnable} type controllable scheduled task wrapper implementation class.
 * <p>
 * Based on encapsulating native {@link Runnable} into a framework-unified {@link Task}
 * task object, some functions (such as {@link #canBeStopped()}, {@link #canBePaused()}
 * and so on) will be overridden to achieve controllable effects on the task's running
 * process or other aspects.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.2
 */
class ControllableRunnableTask extends Task {

    /**
     * The wrapped runnable object.
     */
    private final Runnable runnable;

    private final CronTaskRepository repository;

    /**
     * Builds the task.
     *
     * @param runnable
     *            The wrapped Runnable object.
     * @param repository
     *            The repository object.
     * @throws InvalidPatternException
     *             If the supplied pattern is not valid.
     */
    public ControllableRunnableTask(Runnable runnable, CronTaskRepository repository) throws InvalidPatternException {
        this.runnable = runnable;
        this.repository = repository;
    }

    /**
     * Returns the wrapped Runnable object.
     *
     * @return The wrapped Runnable object.
     */
    public Runnable getRunnable() {
        return runnable;
    }

    /**
     * @return Returning {@code true} indicates that this task can be interrupted during runtime.
     */
    @Override
    public boolean canBeStopped() {
        return true;
    }

    /**
     * Implements {@link Task#execute(TaskExecutionContext)}, launching the
     * {@link Runnable#run()} method on the wrapped object.
     */
    public void execute(TaskExecutionContext context) {

        CronTaskRepository.LongTimedExecutor executor = repository.longTimed(null);
        executor.start();
        try {
            runnable.run();
        }
        finally {
            executor.stop();
        }

    }

    /**
     * Overrides {@link Object#toString()}.
     */
    public String toString() {
        StringBuffer b = new StringBuffer();
        b.append("Task[");
        b.append("runnable=");
        b.append(runnable);
        b.append("]");
        return b.toString();
    }
}
