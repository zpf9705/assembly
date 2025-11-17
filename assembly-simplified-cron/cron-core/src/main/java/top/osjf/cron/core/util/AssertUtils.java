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


package top.osjf.cron.core.util;

import java.util.Collection;

/**
 * Utility class for validating method arguments and object states.
 *
 * <p>Provides static assertion methods to check common preconditions such as
 * non-null values, non-empty strings/collections, boolean conditions, etc.
 * Throws appropriate exceptions if assertions fail.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.2
 */
public abstract class AssertUtils {

    /**
     * Checks that the specified object is not null.
     *
     * @param object  the object to check
     * @param message the exception message if null
     * @throws IllegalArgumentException if object is null
     */
    public static void assertNotNull(Object object, String message) {
        if (object == null) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Checks that the specified boolean condition is true.
     *
     * @param value   the boolean condition to check
     * @param message the exception message if false
     * @throws IllegalArgumentException if value is false
     */
    public static void assertTrue(boolean value, String message) {
        if (!value) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Checks that the given collection is not null and not empty.
     *
     * @param collection the collection to check
     * @param message    the exception message if null or empty
     * @throws IllegalArgumentException if collection is null or empty
     */
    public static void assertNotEmpty(Collection<?> collection, String message) {
        if (CollectionUtils.isEmpty(collection)) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Checks that the given string is not blank (not null, not empty, and not whitespace only).
     *
     * @param string  the string to check
     * @param message the exception message if blank
     * @throws IllegalArgumentException if string is blank
     */
    public static void assertNotBlank(String string, String message) {
        if (StringUtils.isBlank(string)) {
            throw new IllegalArgumentException(message);
        }
    }
}
