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
 * Extended runtime context for listener exception scenarios, inheriting basic capabilities
 * from {@link ListenerContext}.
 *
 * <p>This context encapsulates metadata of the abnormal listener execution, including the specific
 * lifecycle stage where the exception was thrown and the corresponding {@link CronListener} instance.
 * It assists developers in troubleshooting, logging detailed error information and implementing
 * targeted exception alert logic within {@link CronListener#failed failed} or {@link CronListener#failedFallback
 * failedFallback} callback methods.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.2
 */
public interface ListenerErrorContext extends ListenerContext {

    /**
     * Returns the lifecycle phase during which the listener threw an exception.
     * @return the abnormal listener lifecycle stage.
     */
    ListenerLifecycle getErrorListenerLifecycle();

    /**
     * Returns the {@link CronListener} instance that triggered the current exception.
     * @return the cron listener implementation where the exception occurred
     */
    CronListener getErrorCronListener();
}
