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


package top.osjf.cron.datasource.driven.scheduled.nacosconfig;

import com.fasterxml.jackson.annotation.JsonIgnore;
import top.osjf.cron.datasource.driven.scheduled.DefaultTaskElement;

/**
 * Represents a task element configured in Nacos configuration center.
 * This class extends {@link DefaultTaskElement}, inheriting common task properties
 * such as task name, execution interval, enable status, etc., and can be further
 * extended with Nacos-specific attributes.
 *
 * <p>This class is typically used for:
 * <ul>
 *   <li>Deserializing task configurations from Nacos config files</li>
 *   <li>Transferring and manipulating task instances in memory</li>
 *   <li>Serializing task lists back to configuration storage or remote services</li>
 * </ul>
 *
 * Note: This class implements Serializable and declares a fixed serialVersionUID
 * to ensure version compatibility during serialization and deserialization,
 * especially in distributed environments or RPC scenarios.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.2
 */
public class NacosConfigTaskElement extends DefaultTaskElement {

    private static final long serialVersionUID = 2576911210334659473L;

    @Override
    @JsonIgnore
    public boolean isAfterUpdate() {
        return super.isAfterUpdate();
    }

    @Override
    @JsonIgnore
    public boolean isAfterInsert() {
        return super.isAfterInsert();
    }
}
