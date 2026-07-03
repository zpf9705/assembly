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


package top.osjf.cron.core.repository;

import java.lang.annotation.*;

/**
 * Annotations that do not allow concurrent execution of tasks.
 *
 * <p>Used in the method of timed tasks, it marks that the current timed task does
 * not allow concurrent execution. When the previous task is not completed, the next
 * triggered task will skip execution directly, avoiding problems such as data confusion,
 * resource competition, and duplicate business processing caused by multi-threaded
 * concurrent execution of the same task.
 *
 * <p>If the currently selected {@link CronTaskRepository} supports concurrent task execution,
 * the method that annotates this annotation will lose its ability to execute concurrently.
 * Otherwise, the annotation will be invalid. Please refer to Method
 * {@link CronTaskRepository#isSupportConcurrentExecution()} for specific disabling capabilities.
 *
 * <p>The task method body marked by this annotation will not support calling
 * {@link CronTaskRepository#cancelDisallowConcurrentExecution} to release the constraint that
 * cannot be executed concurrently, as it has been considered as a static configuration and
 * developers can consider making choices based on their actual situation.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.2
 *
 * @see CronTaskRepository#isSupportConcurrentExecution
 * @see CronTaskRepository#disallowConcurrentExecution
 * @see CronTaskRepository#cancelDisallowConcurrentExecution
 * @see AnnotationMethodRegistrar
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface DisallowConcurrentExecution {

}
