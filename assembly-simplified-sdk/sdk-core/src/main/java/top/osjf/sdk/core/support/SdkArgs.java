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

package top.osjf.sdk.core.support;

import top.osjf.sdk.core.exception.SdkException;
import top.osjf.sdk.core.util.CollectionUtils;
import top.osjf.sdk.core.util.MapUtils;
import top.osjf.sdk.core.util.StringUtils;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

/**
 * An abstract class for SDK argument validation, providing a series
 * of static methods to check the input arguments before method or
 * function execution.
 * These methods ensure that the parameters meet specific conditions;
 * if not, a custom {@code SdkException} is thrown.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.2
 */
public abstract class SdkArgs {

    /**
     * Validates that the given boolean expression is true.
     * If the expression evaluates to false, a {@code SdkException} is thrown with
     * the specified error message.
     *
     * @param expression The boolean expression to validate.
     * @param message    The error message to be carried in the exception
     *                   when the expression is false.
     */
    public static void isTrue(final boolean expression, final String message) {
        if (!expression) {
            throw new SdkException(message);
        }
    }

    /**
     * Validates that the given Object is {@literal null}.
     * If the expression evaluates to false, a {@code SdkException} is thrown with
     * the specified error message.
     *
     * @param obj     The Object to validate.
     * @param message The error message to be carried in the exception
     *                when the expression is false.
     */
    public static void notNull(final Object obj, final String message) {
        if (Objects.isNull(obj)) {
            throw new SdkException(message);
        }
    }

    /**
     * <p>This method checks whether the provided integer n is non-negative
     * (i.e., greater than or equal to 0).
     * <p>If the integer n is less than 0, it throws an {@code SdkException} with
     * the provided message as the exception information.
     *
     * @param n       The integer to be validated.
     * @param message The exception message to be thrown when the integer is negative.
     */
    public static void notNegative(final int n, final String message) {
        if (n < 0) {
            throw new SdkException(message);
        }
    }

    /**
     * Validates that the given string is not null, empty, or blank.
     * If the string is null, empty, or only contains whitespace characters,
     * a {@code SdkException} is thrown with the specified error message.
     * {@code StringUtils.isBlank} is used to check if the string is null,
     * empty, or blank.
     *
     * @param s       The string to validate.
     * @param message The error message to be carried in the exception when the
     *                string is null, empty, or blank.
     */
    public static void hasText(final String s, final String message) {
        if (StringUtils.isBlank(s)) {
            throw new SdkException(message);
        }
    }

    /**
     * Validates that the given collection is not null or empty.
     * If the collection is null or empty, a {@code SdkException} is thrown with
     * the specified error message.
     * {@code CollectionUtils.isEmpty} is used to check if the collection is
     * null or empty.
     *
     * @param collection The collection to validate.
     * @param message    The error message to be carried in the exception when
     *                   the collection is null or empty.
     */
    public static void notEmpty(final Collection<?> collection, final String message) {
        if (CollectionUtils.isEmpty(collection)) {
            throw new SdkException(message);
        }
    }

    /**
     * Validates that the given map is not null or empty.
     * If the map is null or empty, a {@code SdkException} is thrown with
     * the specified error message.
     * {@code MapUtils.isEmpty} is used to check if the map is
     * null or empty.
     *
     * @param map     The Map object to be validated, with wildcard types for both keys and values,
     *                indicating that it can accept any type of keys and values.
     * @param message The exception message to be thrown when the Map is empty.
     */
    public static void notEmpty(final Map<?, ?> map, final String message) {
        if (MapUtils.isEmpty(map)) {
            throw new SdkException(message);
        }
    }
}
