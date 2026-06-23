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


package top.osjf.cron.core.listener;

/**
 * Exception propagation strategy for {@link CronListener cron listener}.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.2
 */
public enum ListenerErrorPropagateStrategy {

    /**
     * Isolate mode:
     * If an exception occurs in current listener callback, only trigger its own {@link CronListener#failed}
     * or {@link CronListener#failedFallback} method, will not interrupt the entire task, and will not broadcast
     * the exception to other registered listeners.
     */
    ISOLATE,

    /**
     * Propagate mode:
     * Once the current listener throws an exception, the task execution will be terminated immediately, the
     * exception will be broadcast to all listeners, and all listeners will execute the {@link CronListener#failed}
     * or {@link CronListener#failedFallback }callback.
     */
    PROPAGATE
}
