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


package top.osjf.cron.datasource.driven.scheduled;

/**
 * Post-processor interface for resolved task {@link Runnable}.
 *
 * <p>Implement this interface to customize, wrap, enhance or replace the
 * {@link Runnable} instance after the scheduler has resolved the target
 * task bean and method.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.2
 */
public interface ResolvedRunnablePostProcessor {

    /**
     * Post-process the resolved task {@link Runnable}.
     * @param resolvedRunnable the original Runnable resolved by the scheduler
     * @param taskElement the task configuration element
     * @return the final Runnable instance used for scheduling execution
     */
    Runnable postProcessResolvedRunnable(Runnable resolvedRunnable, TaskElement taskElement);
}
