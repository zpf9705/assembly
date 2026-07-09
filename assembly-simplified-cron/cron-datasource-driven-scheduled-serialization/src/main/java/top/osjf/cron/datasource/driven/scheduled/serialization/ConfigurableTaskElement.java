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


package top.osjf.cron.datasource.driven.scheduled.serialization;

import com.fasterxml.jackson.annotation.JsonIgnore;
import top.osjf.cron.datasource.driven.scheduled.DefaultTaskElement;
import top.osjf.cron.datasource.driven.scheduled.PurgedTaskElement;

/**
 * Represents a configurable scheduled task element.
 *
 * <p>This class extends {@link DefaultTaskElement}, inheriting common properties such as task name,
 * execution interval, enable status, executor, etc., and serves as a generic data carrier
 * for task configuration across various data sources — including configuration centers (e.g.,
 * Nacos, Apollo), databases, local files, or remote services.
 *
 * <p>Designed for general-purpose use in serialization workflows:
 * <ul>
 *   <li>Deserializing task configurations from external sources</li>
 *   <li>Transferring and manipulating task instances in memory</li>
 *   <li>Serializing task lists back to persistent storage or remote systems</li>
 * </ul>
 *
 * <p>Note: This class implements Serializable with a fixed serialVersionUID  to ensure version
 * compatibility during serialization and deserialization,especially in distributed environments
 * or RPC scenarios. Future modifications should maintain backward compatibility; consider using
 * transient fields or other compatible extension mechanisms when adding new state.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.2
 */
public class ConfigurableTaskElement extends DefaultTaskElement implements PurgedTaskElement {

    private static final long serialVersionUID = 2576911210334659473L;

    @Override
    @JsonIgnore
    @Deprecated
    public boolean isAfterUpdate() {
        return super.isAfterUpdate();
    }

    @Override
    @JsonIgnore
    @Deprecated
    public boolean isAfterInsert() {
        return super.isAfterInsert();
    }
}
