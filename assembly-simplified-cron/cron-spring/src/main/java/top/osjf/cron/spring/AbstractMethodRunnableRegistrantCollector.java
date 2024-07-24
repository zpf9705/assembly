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

import cn.hutool.core.util.ReflectUtil;
import top.osjf.cron.spring.annotation.Cron;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Registration abstraction processing for method {@link Method} execution.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
public abstract class AbstractMethodRunnableRegistrantCollector extends AbstractRunnableRegistrantCollector<Method> {

    @Override
    protected List<Method> findAndFilterAnnotatedElements(Class<?> realBeanType) {
        Method[] methods = ReflectUtil.getMethods(realBeanType, method -> {
            int modifiers = method.getModifiers();
            return method.isAnnotationPresent(Cron.class)
                    && !Modifier.isStatic(modifiers)
                    && Modifier.isPublic(modifiers);
        });
        return methods != null ? Arrays.asList(methods) : Collections.emptyList();
    }

    @Override
    protected Runnable createRunnable(Object bean, Method method) {
        return () -> ReflectUtil.invoke(bean, method);
    }
}
