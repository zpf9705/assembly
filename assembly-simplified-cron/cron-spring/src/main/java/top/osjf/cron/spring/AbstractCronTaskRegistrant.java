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

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Abstract {@link CronTaskRegistrant} provides some common constructs
 * and methods for subclasses.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
@SuppressWarnings("rawtypes")
public abstract class AbstractCronTaskRegistrant implements CronTaskRegistrant {

    private final CronTaskRepository cronTaskRepository;

    /**
     * Construct within {@link CronTaskRepository} to be a {@link CronTaskRegistrant}.
     *
     * @param cronTaskRepository Scheduled task resource operation class.
     */
    public AbstractCronTaskRegistrant(CronTaskRepository cronTaskRepository) {
        this.cronTaskRepository = cronTaskRepository;
    }

    /**
     * Return the timed task resource operation interface.
     *
     * @param <T> Specific types of resources.
     * @return timed task resource operation interface.
     */
    @SuppressWarnings("unchecked")
    public <T extends CronTaskRepository> T getCronTaskRepository() {
        return (T) cronTaskRepository;
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
     * The generics applicable to {@link CronTaskRepository} are the
     * registration processing of {@link String} and {@link Runnable}.
     *
     * @param realBeanType {@inheritDoc}
     * @param bean         {@inheritDoc}
     * @param environment  {@inheritDoc}
     * @throws Exception {@inheritDoc}
     */
    @Override
    public void register(Class<?> realBeanType, Object bean, Environment environment) throws Exception {
        CronTaskRepository<String, Runnable> sRCronTaskRepository = getCronTaskRepository();
        String[] activeProfiles = environment.getActiveProfiles();
        for (AnnotatedElement element : findAndFilterAnnotatedElements(realBeanType)) {
            CronAnnotationAttributes cronAttribute = getCronAttribute(element);
            String expression = cronAttribute.getExpression();
            Runnable rab = createRunnable(bean, element);
            if (ArrayUtils.isEmpty(activeProfiles)) {
                //When the environment is not activated, it indicates that
                // everything is applicable and can be registered directly.
                sRCronTaskRepository.register(expression, rab);
            } else {
                if (profilesCheck(cronAttribute.getProfiles(), activeProfiles)) {
                    sRCronTaskRepository.register(expression, rab);
                }
            }
        }
    }

    /**
     * Return the collected and filtered {@link AnnotatedElement} collection.
     *
     * <p>This method defaults to {@link Method} executor.
     *
     * @param realBeanType {@link #register}.
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
     * @param bean    {@link #register}.
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
