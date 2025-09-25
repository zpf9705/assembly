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


package top.osjf.cron.core.repository;

/**
 * The Repository interface is a task resource scheduling tagging interface, and
 * currently no methods are defined. Usually used to indicate that a class implements
 * the interface in order to be recognized and processed within a specific framework
 * or context. For example, in some frameworks, classes implementing this interface
 * may be automatically registered as some type of data store or repository.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.1
 * @see CronTaskRepository
 * @see RunTimesRegistrarRepository
 * @see RunTimeoutRegistrarRepository
 * @see ListableRepository
 * @see CronListenerRepository
 * @see LifecycleRepository
 */
public interface Repository {
}
