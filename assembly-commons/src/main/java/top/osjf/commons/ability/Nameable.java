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


package top.osjf.commons.ability;

/**
 * A general capability interface that defines a unified specification for obtaining
 * the unique readable name identifier of any object.
 *
 * <p>
 * This interface abstracts the common capability of fetching object identification names,
 * which unifies the way to mark different component implementations within the project.
 * It avoids customizing name acquisition methods repeatedly in various business modules,
 * and provides a consistent dimension for log recording, exception troubleshooting,
 * data statistics and page display scenarios.
 *
 * <p>
 * Typical application scenarios:
 * <ul>
 * <li>Quickly locate the source of exceptions through component names;</li>
 * <li>Add readable identification tags to log records for link tracing;</li>
 * <li>Provide unified dimension tags for monitoring indicator aggregation statistics;</li>
 * <li>Distinguish multiple different implementation classes of the same parent interface;</li>
 * <li>Display friendly readable names on operation and maintenance or management pages.</li>
 * </ul>
 *
 * <p>
 * This interface belongs to a bottom-level general capability and can be implemented
 * by any component that needs to expose its own readable identity, such as repository,
 * listener, processor, strategy, handler and other custom business components.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.2
 */
public interface Nameable {

    /**
     * Get the unique readable name identifier of the current object instance.
     *
     * <p>
     * <b>Mandatory constraints:</b>
     * The return value must not be {@code null} or blank string.
     * A null or empty name will lead to invalid log tags, ambiguous exception location,
     * abnormal monitoring statistics and other system hidden troubles.
     *
     * <p>
     * <b>Recommended specification for return content:</b>
     * <ol>
     * <li>Preferred: simple class name of the implementation class;</li>
     * <li>Alternative: business unique identifier or framework identification name;</li>
     * <li>Custom: readable fixed name defined according to business rules.</li>
     * </ol>
     *
     * @return non-null unique readable name identifier of current object
     */
    String getName();
}
