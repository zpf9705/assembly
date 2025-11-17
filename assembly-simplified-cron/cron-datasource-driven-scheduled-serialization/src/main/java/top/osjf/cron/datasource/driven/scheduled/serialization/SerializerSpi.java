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

import java.lang.annotation.*;

/**
 * Serializer Service Provider Interface (SPI) annotation, used to mark a class as an implementation
 * of a configuration task element serializer and define its priority order.
 *
 * <p>This annotation is applied to classes implementing {@link ConfigTaskElementSerializer}, with the
 * {@link #order()} method specifying their loading priority. Lower values indicate higher priority.
 * For example: {@link #HIGHEST_PRECEDENCE} indicates the highest priority, while {@link #LOWEST_PRECEDENCE}
 * indicates the lowest.
 *
 * <p>When using SPI discovery mechanisms (e.g., ServiceLoader), the framework sorts multiple implementations
 * by this order value and selects the one with the smallest value as the primary serializer for a given format,
 * avoiding ambiguity.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.2
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface SerializerSpi {

    /**
     * Useful constant for the highest precedence value.
     * @see Integer#MIN_VALUE
     */
    int HIGHEST_PRECEDENCE = Integer.MIN_VALUE;

    /**
     * Useful constant for the lowest precedence value.
     * @see Integer#MAX_VALUE
     */
    int LOWEST_PRECEDENCE = Integer.MAX_VALUE;


    /**
     * Get the order value of this object.
     * <p>Lower values indicate higher priority; higher values indicate lower priority.
     * For example: an implementation with order 1 will be preferred over one with order 5.
     * <p>If multiple implementations have the same order value, their relative ordering is
     * arbitrary, it is recommended to use predefined constants {@link #HIGHEST_PRECEDENCE}
     * or {@link #LOWEST_PRECEDENCE} to clearly express extreme priority requirements.
     * @return the order value
     * @see #HIGHEST_PRECEDENCE
     * @see #LOWEST_PRECEDENCE
     */
    int order();
}
