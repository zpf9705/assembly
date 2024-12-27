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

import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.lang.NonNull;
import org.springframework.util.ReflectionUtils;
import top.osjf.cron.core.DefaultRegistrant;
import top.osjf.cron.core.Registrant;
import top.osjf.cron.core.util.ArrayUtils;
import top.osjf.cron.core.util.CollectionUtils;
import top.osjf.cron.spring.annotation.Cron;
import top.osjf.cron.spring.annotation.CronAnnotationAttributes;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
public abstract class AbstractRegistrantCollector implements RegistrantCollector, EnvironmentAware {

    private List<String> activeProfiles;
    private Iterator<Registrant> iterator;
    private final List<Registrant> registrants = new ArrayList<>();

    protected List<Registrant> getRegistrants() {
        return registrants;
    }

    private Iterator<Registrant> getIterator() {
        if (iterator == null) iterator = registrants.iterator();
        return iterator;
    }

    @Override
    public void setEnvironment(@NonNull Environment environment) {
        activeProfiles = Arrays.asList(environment.getActiveProfiles());
    }

    @Override
    public void add(Class<?> targetType, Object target) {
        for (Method method : findAndFilterAnnotatedMethods(targetType)) {
            CronAnnotationAttributes cronAttribute = CronAnnotationAttributes.of(method);
            String expression = getExpression(cronAttribute.getExpression());
            Runnable runnable = () -> ReflectionUtils.invokeMethod(method, target);
            if (CollectionUtils.isEmpty(activeProfiles)
                    || Arrays.stream(cronAttribute.getProfiles()).anyMatch(activeProfiles::contains)) {
                addRunnableRegistrant(expression, runnable, method);
            }
        }
    }

    protected List<Method> findAndFilterAnnotatedMethods(Class<?> realBeanType) {
        Method[] uniqueDeclaredMethods = ReflectionUtils.getUniqueDeclaredMethods(realBeanType, method -> {
            int modifiers = method.getModifiers();
            return method.isAnnotationPresent(Cron.class)
                    && !Modifier.isStatic(modifiers) && Modifier.isPublic(modifiers);
        });
        return ArrayUtils.isEmpty(uniqueDeclaredMethods) ? Collections.emptyList() :
                new ArrayList<>(Arrays.asList(uniqueDeclaredMethods));
    }

    protected String getExpression(String expression) {
        return expression;
    }

    protected void addRunnableRegistrant(String expression, Runnable runnable, Method method) {
        getRegistrants().add(new DefaultRegistrant(expression, runnable));
    }

    @Override
    public boolean hasNext() {
        return getIterator().hasNext();
    }

    @Override
    public Registrant next() {
        return getIterator().next();
    }

    @Override
    public void close() {
        registrants.clear();
    }
}
