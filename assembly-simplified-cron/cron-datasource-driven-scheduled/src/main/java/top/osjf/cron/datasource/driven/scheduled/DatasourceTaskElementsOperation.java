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


package top.osjf.cron.datasource.driven.scheduled;

import java.util.List;

/**
 * Interface defining operations for datasource task elements management.
 * Provides functionality to clean up and retrieve task information.
 *
 * <p>This interface is primarily used for managing scheduled task configurations from a datasource, including:
 * <ul>
 *   <li>Cleaning up invalid or expired task data to prevent dirty data during registration</li>
 *   <li>Retrieving task information sets for registration and runtime dynamic checks</li>
 * </ul>
 *
 * <p>Implementations should define cleanup logic and task information retrieval strategies according
 * to business requirements.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.4
 */
public interface DatasourceTaskElementsOperation {

    /**
     * Cleans up task information data in the datasource to prevent dirty data during registration.
     *
     * <p>Developers should define cleanup logic based on business needs. Typical scenarios include:
     * <ul>
     *   <li>Removing invalid or expired task configurations</li>
     *   <li>Resetting task states to unregistered</li>
     *   <li>Validating task configuration integrity</li>
     * </ul>
     */
    void purgeDatasourceTaskElements();

    /**
     * Retrieves the current set of task information from the datasource, which may include the primary task.
     *
     * <p>The returned task information is used for:
     * <ul>
     *   <li>Task registration (e.g., adding to a scheduler)</li>
     *   <li>Runtime dynamic information checks (e.g., task state validation)</li>
     * </ul>
     *
     * <p><b>Note:</b> The returned collection might be a reference to the original data.
     * External modifications could affect internal state. Implementations are encouraged
     * to return copies or immutable collections.
     *
     * @return Current set of task information from the datasource (may include primary task)
     */
    List<TaskElement> getDatasourceTaskElements();
}
