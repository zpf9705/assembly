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

import top.osjf.cron.spring.annotation.Cron;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Registration abstraction processing for method {@link Method} execution.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
public class AbstractMethodRunnableRegistrantCollector extends AbstractRunnableRegistrantCollector<Method> {

    @Override
    protected List<Method> findAndFilterAnnotatedElements(Class<?> realBeanType) {
        return Arrays.stream(realBeanType.getDeclaredMethods())
                .filter(method -> {
                    int modifiers = method.getModifiers();
                    return method.isAnnotationPresent(Cron.class)
                            && !Modifier.isStatic(modifiers)
                            && Modifier.isPublic(modifiers);
                }).collect(Collectors.toList());
    }

    @Override
    protected Runnable createRunnable(Object bean, Method method) {
        return () -> {
            try {
                method.invoke(bean);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }
}
