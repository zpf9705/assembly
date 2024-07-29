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

import org.springframework.core.env.Environment;
import top.osjf.cron.core.repository.CronTaskRepository;
import top.osjf.cron.core.util.ArrayUtils;
import top.osjf.cron.spring.annotation.CronAnnotationAttributes;

import java.lang.reflect.AnnotatedElement;
import java.util.List;

/**
 * Abstract {@link RegistrantCollector}, extract common methods, and use default
 * {@link RunnableRegistrant} to add task classes to be registered.
 *
 * @param <T> The executable subclass type of {@link AnnotatedElement}
 *            is used to construct {@link Runnable}.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
public abstract class AbstractRunnableRegistrantCollector<T extends AnnotatedElement> extends AbstractRegistrantCollector {

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
        for (T element : findAndFilterAnnotatedElements(realBeanType)) {
            CronAnnotationAttributes cronAttribute = getCronAttribute(element);
            String expression = ifGetDefaultExpression(cronAttribute.getExpression());
            Runnable rab = createRunnable(bean, element);
            if (ArrayUtils.isEmpty(activeProfiles)) {
                //When the environment is not activated, it indicates that
                // everything is applicable and can be registered directly.
                addRunnableRegistrant(expression, rab, element);
            } else {
                if (profilesCheck(cronAttribute.getProfiles(), activeProfiles)) {
                    addRunnableRegistrant(expression, rab, element);
                }
            }
        }
    }

    /**
     * If it is the default cron expression, it depends on whether it is
     * precise to the second to obtain the default value. Subclass determines
     * rewriting based on type and returns the supported default cron.
     *
     * @param expression cron expression.
     * @return default support cron expression.
     */
    public String ifGetDefaultExpression(String expression) {
        return expression;
    }

    /**
     * Add a {@link RunnableRegistrant}.
     *
     * @param expression Cron expression.
     * @param rab        run body.
     * @param element    Annotated element.
     */
    public void addRunnableRegistrant(String expression, Runnable rab, T element) {
        addRegistrant(addRunnableRegistrantInternal(expression, rab, element));
    }

    /**
     * Internal construction {@link RunnableRegistrant}.
     *
     * @param expression Cron expression.
     * @param rab        run body.
     * @param element    Annotated element.
     * @return Subclass for {@link RunnableRegistrant}.
     */
    protected abstract RunnableRegistrant addRunnableRegistrantInternal(String expression, Runnable rab, T element);

    /**
     * Return the collected and filtered {@link AnnotatedElement} collection.
     *
     * @param realBeanType {@link #add}.
     * @return the collected and filtered {@link AnnotatedElement} collection.
     */
    protected abstract List<T> findAndFilterAnnotatedElements(Class<?> realBeanType);

    /**
     * Create a runtime {@link Runnable} based on the currently
     * accessed bean and {@link AnnotatedElement}.
     *
     * @param bean    {@link #add}.
     * @param element program elements that can carry annotations.
     * @return a runtime {@link Runnable}.
     */
    protected abstract Runnable createRunnable(Object bean, T element);
}
