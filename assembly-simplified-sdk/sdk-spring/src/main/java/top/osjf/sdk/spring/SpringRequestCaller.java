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

package top.osjf.sdk.spring;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import top.osjf.sdk.core.caller.Callback;
import top.osjf.sdk.core.caller.RequestCaller;
import top.osjf.sdk.core.support.NotNull;
import top.osjf.sdk.core.support.Nullable;
import top.osjf.sdk.core.util.MapUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * The {@code SpringRequestCaller} class is a subclass of {@code RequestCaller}
 * specifically designed for use in Spring frameworks.
 *
 * <p>By extending {@code RequestCaller}  and passing in Spring's {@code ApplicationContext},
 * this class leverages Spring's IoC (Inversion of Control) container to obtain and
 * manage beans, simplifying the logic for dependency injection and invoking other
 * beans in a Spring environment.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.2
 */
public class SpringRequestCaller extends RequestCaller implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(@NotNull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Sort by {@link AnnotationAwareOrderComparator} use annotation
     * {@link org.springframework.core.annotation.Order}.
     *
     * @param callbacks {@inheritDoc}
     */
    @Override
    protected void sortCallbacks(List<Callback> callbacks) {
        AnnotationAwareOrderComparator.sort(callbacks);
    }

    /**
     * Hand over object management to the Spring framework and search for
     * all {@code Class} type option objects related to annotations from
     * the Spring container.
     *
     * @param name  {@inheritDoc}
     * @param clazz {@inheritDoc}
     * @param <T>   {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    @Nullable
    @SuppressWarnings("unchecked")
    protected <T> T getClassedInstance(String name, Class<T> clazz) {
        Map<String, ?> beanMap = applicationContext.getBeansOfType(clazz);
        if (MapUtils.isNotEmpty(beanMap)) {
            List<?> beans = Arrays.asList(beanMap.values().toArray());
            AnnotationAwareOrderComparator.sort(beans);
            return (T) beans.get(0);
        }
        return null;
    }
}
