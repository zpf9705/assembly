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


package top.osjf.cron.datasource.driven.scheduled.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * {@link DatabaseTaskElement} JPA Repository Interface.
 *
 * <p>Functional Overview:
 * <ul>
 * <li>Provides standard CRUD operations for {@code DatabaseTaskElement} entity</li>
 * <li>Includes custom query and batch update methods</li>
 * <li>Complies with Spring Data JPA Repository pattern specification</li>
 * </ul>
 *
 * <p>Technical Features:
 * <ul>
 * <li>Annotated with {@link Repository} as Spring data access component</li>
 * <li>Extends JpaRepository for basic data access capabilities</li>
 * <li>String primary key type requires matching {@link javax.persistence.Id}
 *  field in entity</li>
 * </ul>
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.1
 */
@Repository
public interface DatabaseTaskElementJpaRepository extends JpaRepository<DatabaseTaskElement, String> {
    /**
     * Checks if any records exist with non-null {@link DatabaseTaskElement#getTaskId()}.
     * <p>
     * Implementation:
     * - Based on Spring Data JPA query method derivation
     * - Auto-generated SQL: SELECT COUNT(*) gt 0 FROM table WHERE task_id IS NOT NULL
     *
     * @return
     * {@code true} -  records meeting criteria exist
     * {@code false} - no records meet criteria
     */
    boolean existsByTaskIdIsNotNull();

    /**
     * Clears registered scheduling information (batch update)
     * <p>
     * Operation Details:
     * - Sets taskId to empty string
     * - Sets updateSign to 0 (typically indicates un-updated state)
     * - Clears statusDescription field
     */
    @Modifying
    @Query("UPDATE DatabaseTaskElement e SET e.taskId = '', e.updateSign = 0, e.statusDescription = ''")
    void clearRegisteredScheduledInfo();

    /**
     * Finds elements by update sign and task ID conditions
     * <p>
     * Query Logic:
     * <ul>
     * <li>Finds all records with updateSign=1 (records needing update)</li>
     * <li>Includes records with updateSign=0 AND taskId IS NULL (unassigned records)</li>
     * </ul>
     *
     * @return
     * List of qualified database task elements.
     */
    @Query("SELECT e FROM DatabaseTaskElement e WHERE e.updateSign = 1 OR (e.updateSign = 0 AND e.taskId IS NULL)")
    List<DatabaseTaskElement> findElementsByUpdateSignAndTaskId();
}
