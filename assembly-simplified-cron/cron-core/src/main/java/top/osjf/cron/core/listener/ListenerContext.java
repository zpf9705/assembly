/*
 * Copyright 2024-? the original author or authors.
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
 * The context interface for the scheduled task listener callback.
 *
 * <p>In the implementation of timed task or event listeners, such contextual
 * interfaces are often used to convey relevant information about tasks or events,
 * such as the unique ID of the task, the status of the task, and related business
 * data. By implementing this interface, it is easy to share this information between
 * different listeners or processors.
 *
 * <p>The ID tag of this interface adopts generic symbols, which is compatible
 * with and solves the problem of inconsistent ID types in multiple frameworks.
 *
 * @param <ID> the type of context unique identifier generally represents the unique
 *             identifier of the callback task being executed.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.3
 */
public interface ListenerContext<ID> {

    /**
     * Return the unique ID of the scheduled task for this callback.
     *
     * @return the unique ID of the scheduled task for this callback.
     */
    ID getID();

    /**
     * Return the original context information object of the callback
     * task, and return different original objects according to the
     * different frameworks used.
     *
     * @return the original context information object.
     */
    Object getSourceContext();
}
