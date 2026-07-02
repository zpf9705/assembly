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
 * Marker interface for all scheduled task repository implementations.
 *
 * <p>This interface declares no methods. It serves only as a tag to identify
 * that the implementing class is a scheduled task resource repository component,
 * enabling unified scanning, recognition and special processing within the framework.
 * Implementations of all cron task registration, query, modification and lifecycle
 * management repositories must inherit this marker interface.
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
