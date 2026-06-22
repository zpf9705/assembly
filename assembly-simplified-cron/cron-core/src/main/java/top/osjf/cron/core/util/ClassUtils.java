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

import java.lang.reflect.UndeclaredThrowableException;

/**
 * Simple utility class for resolving {@code .class} with the {@link Class} API.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.2
 */
@Deprecated
public abstract class ClassUtils {

    /**
     * Use the API {@code forName} of {@link Class} to obtain a {@code Class} object with
     * a given name without static initialization.
     *
     * @param className the given class name.
     * @return a {@code Class} object by given name.
     */
    public static Class<?> forName(String className) {
        try {
            return Class.forName(className);
        }
        catch (ClassNotFoundException ex) {
            throw new UndeclaredThrowableException(ex);
        }
    }
}
