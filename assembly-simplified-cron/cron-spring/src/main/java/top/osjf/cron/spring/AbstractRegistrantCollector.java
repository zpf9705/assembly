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
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.core.env.Environment;
import top.osjf.cron.core.repository.CronTaskRepository;
import top.osjf.cron.spring.annotation.Cron;
import top.osjf.cron.spring.annotation.CronAnnotationAttributes;
import top.osjf.cron.spring.cron4j.Cron4jRegistrantCollector;

import java.io.Closeable;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Abstract {@link RegistrantCollector}, extract common methods, and use default
 * {@link RunnableRegistrant} to add task classes to be registered.
 *
 * <p>Regarding the registration criteria for scheduled tasks, including the judgment
 * of environment specified in the {@link Cron} annotation, the default {@link Registrant}
 * mentioned above does not meet the requirements and can be further overridden by subclasses.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
public abstract class AbstractRegistrantCollector implements RegistrantCollector {

    /*** The temporary collection set of {@link Registrant}. */
    private final List<Registrant> registrants = new ArrayList<>();

    /***  Iterator for {@link #registrants}. */
    private Iterator<Registrant> iterator;

    /***  The expression for the shortest time interval supported by cron4j.. */
    private static final String cron4jMinExpression = "* * * * *";

    /**
     * Return the temporary collection of {@link Registrant}.
     *
     * @return collection of {@link Registrant}.
     */
    public List<Registrant> getRegistrants() {
        return registrants;
    }

    Iterator<Registrant> getIterator() {
        if (iterator == null) iterator = registrants.iterator();
        return iterator;
    }

    @Override
    public void close() {
        registrants.clear();
    }

    @Override
    public boolean hasNext() {
        return getIterator().hasNext();
    }

    @Override
    public Registrant next() {
        return getIterator().next();
    }

    /**
     * The generics applicable to {@link CronTaskRepository} are the
     * registration processing of {@link String} and {@link Runnable}.
     *
     * @param realBeanType {@inheritDoc}
     * @param bean         {@inheritDoc}
     * @param environment  {@inheritDoc}
     */
    @Override
    public void add(Class<?> realBeanType, Object bean, Environment environment) {
        String[] activeProfiles = environment.getActiveProfiles();
        for (AnnotatedElement element : findAndFilterAnnotatedElements(realBeanType)) {
            CronAnnotationAttributes cronAttribute = getCronAttribute(element);
            String expression = cronAttribute.getExpression();
            //When considering default values here, cron4j takes
            // hierarchical default values.
            if (Objects.equals(expression, Cron.DEFAULT_CRON_EXPRESSION)
                    && getClass().equals(Cron4jRegistrantCollector.class)) {
                expression = cron4jMinExpression;
            }
            Runnable rab = createRunnable(bean, element);
            if (ArrayUtils.isEmpty(activeProfiles)) {
                //When the environment is not activated, it indicates that
                // everything is applicable and can be registered directly.
                addRegistrant(expression, rab);
            } else {
                if (profilesCheck(cronAttribute.getProfiles(), activeProfiles)) {
                    addRegistrant(expression, rab);
                }
            }
        }
    }

    /**
     * Add a {@link Registrant}, default to using {@link RunnableRegistrant}.
     *
     * @param expression Cron expression.
     * @param rab        run body.
     */
    protected void addRegistrant(String expression, Runnable rab) {
        getRegistrants().add(new RunnableRegistrant(expression, rab));
    }

    /**
     * Return the content of the {@link AnnotatedElement} annotation
     * obtained based on the method or type of {@link top.osjf.cron.spring.annotation.Cron}.
     *
     * @param element program elements that can carry annotations.
     * @return {@link top.osjf.cron.spring.annotation.Cron}'s attributes.
     */
    protected CronAnnotationAttributes getCronAttribute(AnnotatedElement element) {
        return CronAnnotationAttributes.of(element);
    }


    /**
     * Return the collected and filtered {@link AnnotatedElement} collection.
     *
     * <p>This method defaults to {@link Method} executor.
     *
     * @param realBeanType {@link #add}.
     * @return the collected and filtered {@link AnnotatedElement} collection.
     */
    protected List<AnnotatedElement> findAndFilterAnnotatedElements(Class<?> realBeanType) {
        return Arrays.stream(realBeanType.getDeclaredMethods())
                .filter(method -> {
                    int modifiers = method.getModifiers();
                    return method.isAnnotationPresent(Cron.class)
                            && !Modifier.isStatic(modifiers)
                            && Modifier.isPublic(modifiers);
                }).collect(Collectors.toList());
    }

    /**
     * Create a runtime {@link Runnable} based on the currently
     * accessed bean and {@link AnnotatedElement}.
     *
     * <p>This method defaults to {@link Method} executor.
     *
     * @param bean    {@link #add}.
     * @param element program elements that can carry annotations.
     * @return a runtime {@link Runnable}.
     */
    protected Runnable createRunnable(Object bean, AnnotatedElement element) {
        Method method = (Method) element;
        return () -> ReflectUtil.invoke(bean, method);
    }

    /**
     * Check if the required operating environment is within the list of activated environments.
     *
     * @param providerProfiles The set of execution environment names that the
     *                         registration task aims to satisfy.
     * @param activeProfiles   The current set of activated environments.
     * @return if {@code true} allow registration ,otherwise no allowed.
     */
    protected boolean profilesCheck(String[] providerProfiles, String[] activeProfiles) {
        if (ArrayUtils.isEmpty(activeProfiles)) {
            //When the environment is not activated, it indicates that
            // everything is applicable and can be registered directly.
            return true;
        }
        if (ArrayUtils.isEmpty(providerProfiles)) {
            //When no running environment is provided, register directly.
            return true;
        }
        //Adaptation provides the presence of the required environment in the activated environment.
        return Arrays.stream(providerProfiles).anyMatch(p -> Arrays.asList(activeProfiles).contains(p));
    }
}
