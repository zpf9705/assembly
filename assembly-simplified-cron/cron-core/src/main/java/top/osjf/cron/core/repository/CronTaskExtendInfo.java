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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.osjf.commons.lang.Nullable;
import top.osjf.commons.util.Attributes;

/**
 * Cron task extended attribute container, allows developers to freely operate attributes
 * and only common delete operations will record logs for operation traceability.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.2
 */
@SuppressWarnings("serial")
public class CronTaskExtendInfo extends Attributes {

    private static final Logger log = LoggerFactory.getLogger(CronTaskExtendInfo.class);

    /** the cron task id.*/
    private final String id;

    /**
     * Create a {@code CronTaskExtendInfo} bound to the specified task ID.
     * @param id unique cron task identifier
     */
    public CronTaskExtendInfo(String id) {
        super();
        this.id = id;
    }

    /**
     * Remove the attribute value corresponding to the specified key,
     * and record an info log after successful deletion for operation traceability.
     *
     * @param key the key of the attribute to be removed
     * @return the removed attribute value, {@code null} if the key does not exist
     */
    @Override
    @Nullable
    public Object remove(Object key) {
        Object removedValue = super.remove(key);
        if (removedValue != null) {
            log.info("The extended attribute [{}] of cron task [{}] has been removed, original value: [{}]",
                    key, id, removedValue);
        }
        return removedValue;
    }

    /**
     * Remove the attribute only when the key and value match simultaneously,
     * and record an info log after successful deletion for operation traceability.
     *
     * @param key   attribute key
     * @param value expected attribute value
     * @return {@code true} if the key-value pair was successfully removed
     */
    @Override
    public boolean remove(Object key, Object value) {
        boolean removed = super.remove(key, value);
        if (removed) {
            log.info("The extended attribute key-value pair [{}={}] of cron task [{}] has been removed",
                    key, value, id);
        }
        return removed;
    }

    /**
     * Clear all extended attributes of the current task,
     * and record an info log after clearing for operation traceability.
     */
    @Override
    public void clear() {
        super.clear();
        log.info("All extended attributes of cron task [{}] have been cleared", id);
    }

    /**
     * Return the unique identifier of the bound cron task.
     * @return task unique id
     */
    public String getId() {
        return id;
    }
}
