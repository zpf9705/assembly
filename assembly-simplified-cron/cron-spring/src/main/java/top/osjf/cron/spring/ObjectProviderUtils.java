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


package top.osjf.cron.spring;

import org.springframework.beans.factory.ObjectProvider;
import top.osjf.cron.core.lang.Nullable;

/**
 * Simple {@link ObjectProvider} related tool classes.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.3
 */
public abstract class ObjectProviderUtils {

    /**
     * Retrieve the highest priority element from the given {@code ObjectProvider}.
     *
     * @param provider provide objects of type {@code T}, which may have different
     *                 priorities.
     * @param <T>      the type of object provided by the {@code ObjectProvider}.
     * @return If the provider is not {@literal null}, return the element with the
     * highest priority in the provider, otherwise return {@literal null}.
     */
    @Nullable
    public static <T> T getPriority(ObjectProvider<T> provider) {
        if (provider == null) {
            return null;
        }
        return provider.orderedStream().findFirst().orElse(null);
    }
}
