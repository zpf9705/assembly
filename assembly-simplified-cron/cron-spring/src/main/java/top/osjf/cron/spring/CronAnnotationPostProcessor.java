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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.AopInfrastructureBean;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.support.MergedBeanDefinitionPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportAware;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor;
import org.springframework.util.Assert;
import top.osjf.cron.core.lifestyle.LifeStyle;
import top.osjf.cron.core.lifestyle.StartupProperties;
import top.osjf.cron.core.listener.CronListener;
import top.osjf.cron.core.repository.CronMethodRunnable;
import top.osjf.cron.core.repository.CronTask;
import top.osjf.cron.core.repository.CronTaskRepository;
import top.osjf.cron.core.util.ArrayUtils;
import top.osjf.cron.core.util.StringUtils;
import top.osjf.cron.spring.annotation.Cron;
import top.osjf.cron.spring.annotation.Crones;
import top.osjf.cron.spring.cron4j.EnableCron4jCronTaskRegister;
import top.osjf.cron.spring.hutool.EnableHutoolCronTaskRegister;
import top.osjf.cron.spring.quartz.EnableQuartzCronTaskRegister;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Bean post-processor that registers methods annotated with {@link Cron @Cron} to
 * be invoked by a {@link CronTaskRepository} according to the "cron" expression
 * provided via the annotation.
 *
 * <p>This post-processor is automatically registered in the configuration
 * {@link CronTaskConfiguration} according to any of the following:
 * <ul>
 * <li>{@link EnableHutoolCronTaskRegister @EnableHutoolCronTaskRegister}</li>
 * <li>{@link EnableQuartzCronTaskRegister @EnableQuartzCronTaskRegister}</li>
 * <li>{@link EnableCron4jCronTaskRegister @EnableCron4jCronTaskRegister}</li>
 * </ul>
 *
 * <p>Automatically detect instances of {@link CronTaskRepository CronTaskRepository},
 * {@link CronListener CronListener}, {@link LifeStyle LifeStyle} in the container,
 * and after collection is complete (copying the logic from
 * {@link ScheduledAnnotationBeanPostProcessor#postProcessAfterInitialization} during
 * the collection process), automatically register and start.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 * @see Cron
 * @see Crones
 * @see EnableHutoolCronTaskRegister
 * @see EnableQuartzCronTaskRegister
 * @see EnableCron4jCronTaskRegister
 * @see CronTaskRepository
 * @see CronListener
 * @see top.osjf.cron.core.listener.ListenerContext
 * @see LifeStyle
 */
public class CronAnnotationPostProcessor implements ImportAware, ApplicationContextAware, EnvironmentAware,
        SmartInitializingSingleton, ApplicationListener<ContextRefreshedEvent>, MergedBeanDefinitionPostProcessor {

    private static final Logger logger = LoggerFactory.getLogger(CronAnnotationPostProcessor.class);

    private ApplicationContext applicationContext;

    private List<String> activeProfiles;

    private boolean isCron4j;

    private final StartupProperties startupProperties = StartupProperties.of();

    private final Set<Class<?>> nonAnnotatedClasses = Collections.newSetFromMap(new ConcurrentHashMap<>(16));

    private final Set<CronTask> cronTasks = Collections.newSetFromMap(new ConcurrentHashMap<>(16));

    private final List<Class<?>> annotationClasses = Arrays.asList(EnableHutoolCronTaskRegister.class,
            EnableCron4jCronTaskRegister.class,
            EnableQuartzCronTaskRegister.class);

    @Override
    public void setImportMetadata(@NonNull AnnotationMetadata annotationMetadata) {
        for (MergedAnnotation<Annotation> annotation : annotationMetadata.getAnnotations()) {
            Class<Annotation> type = annotation.getType();
            if (annotationClasses.contains(type)) {
                startupProperties.addProperties(annotationMetadata.getAnnotationAttributes
                        (annotation.getType().getCanonicalName()));
                isCron4j = Objects.equals(type, EnableCron4jCronTaskRegister.class);
            }
        }
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void setEnvironment(@NonNull Environment environment) {
        activeProfiles = Arrays.asList(environment.getActiveProfiles());
    }

    @Override
    public void postProcessMergedBeanDefinition(@NonNull RootBeanDefinition beanDefinition, @NonNull Class<?> beanType,
                                                @NonNull String beanName) {
    }

    @Nullable
    @Override
    public Object postProcessAfterInitialization(@NonNull Object bean, @NonNull String beanName) throws BeansException {

        if (bean instanceof AopInfrastructureBean || bean instanceof TaskScheduler ||
                bean instanceof ScheduledExecutorService || bean instanceof CronTaskRepository ||
                bean instanceof LifeStyle) {
            // Ignore AOP infrastructure such as scoped proxies.
            return bean;
        }

        Class<?> targetClass = AopProxyUtils.ultimateTargetClass(bean);
        if (!this.nonAnnotatedClasses.contains(targetClass) &&
                AnnotationUtils.isCandidateClass(targetClass, Arrays.asList(Cron.class, Crones.class))) {
            Map<Method, Set<Cron>> annotatedMethods = MethodIntrospector.selectMethods(targetClass,
                    (MethodIntrospector.MetadataLookup<Set<Cron>>) method -> {
                        Set<Cron> scheduledAnnotations = AnnotatedElementUtils.getMergedRepeatableAnnotations(
                                method, Cron.class, Crones.class);
                        return (!scheduledAnnotations.isEmpty() ? scheduledAnnotations : null);
                    });
            if (annotatedMethods.isEmpty()) {
                this.nonAnnotatedClasses.add(targetClass);
                if (logger.isTraceEnabled()) {
                    logger.trace("No @Cron annotations found on bean class: " + targetClass);
                }
            } else {
                // Non-empty set of methods
                annotatedMethods.forEach((method, cronAnnotations) ->
                        cronAnnotations.forEach(cron -> processCron(cron, method, bean)));
                if (logger.isTraceEnabled()) {
                    logger.trace(annotatedMethods.size() + " @Cron methods processed on bean '" + beanName +
                            "': " + annotatedMethods);
                }
            }
        }
        return bean;
    }


    @Override
    public void afterSingletonsInstantiated() {
        // Remove resolved singleton classes from cache
        this.nonAnnotatedClasses.clear();

        if (this.applicationContext == null) {
            // Not running in an ApplicationContext -> register tasks early...
            finishRegistration();
        }
    }

    @Override
    public void onApplicationEvent(@NonNull ContextRefreshedEvent event) {
        if (event.getApplicationContext() == this.applicationContext) {
            // Running in an ApplicationContext -> register tasks this late...
            // giving other ContextRefreshedEvent listeners a chance to perform
            // their work at the same time (e.g. Spring Batch's job registration).
            finishRegistration();
        }
    }

    /**
     * Process the given {@code @Cron} method declaration on the given bean.
     * <p>Regarding the regulations on expressions, the following default values
     * are provided:
     * <ul>
     * <li>Supports second level frameworks, default values are:{@link Cron#DEFAULT_CRON_EXPRESSION}</li>
     * <li>Non second level frameworks, such as {@code Cron4j}, specify the minimum unit they support.</li>
     * </ul>
     *
     * @param cron   the {@code @Cron} annotation
     * @param method the method that the annotation has been declared on
     * @param bean   the target bean instance
     * @see #createRunnable(Object, Method)
     * @since 1.0.3
     */
    protected void processCron(Cron cron, Method method, Object bean) {
        CronMethodRunnable runnable = createRunnable(bean, method);
        String[] profiles = cron.profiles();
        String expression = cron.expression();
        if (StringUtils.isBlank(expression)) {
            if (isCron4j) {
                expression = "* * * * *"; //Cron4j only supports minute level support.
            } else {
                expression = Cron.DEFAULT_CRON_EXPRESSION; //Default execution time is 1 second.
            }
        }
        synchronized (this.cronTasks) {
            //No environment specified or specified environment adapted
            // to the current activated environment.
            if (ArrayUtils.isEmpty(profiles) ||
                    Arrays.stream(profiles).anyMatch(activeProfiles::contains)) {
                CronTask cronTask = new CronTask(expression, runnable);
                cronTasks.add(cronTask);
            }
        }
    }

    /**
     * Create a {@link Runnable} for the given bean instance,
     * calling the specified scheduled method.
     * <p>The default implementation creates a {@link CronMethodRunnable}.
     *
     * @param target the target bean instance
     * @param method the scheduled method to call
     * @since 1.0.3
     */
    private CronMethodRunnable createRunnable(Object target, Method method) {
        Assert.isTrue(method.getParameterCount() == 0,
                "Only no-arg methods may be annotated with @Cron");
        Method invocableMethod = AopUtils.selectInvocableMethod(method, target.getClass());
        return new CronMethodRunnable(target, invocableMethod);
    }

    /**
     * Perform registration work after collecting scheduled task information.
     */
    private void finishRegistration() {
        CronTaskRepository cronTaskRepository = applicationContext.getBean(CronTaskRepository.class);
        for (CronTask cronTask : cronTasks) {
            cronTaskRepository.register(cronTask);
        }
        for (CronListener listener : applicationContext.getBeansOfType(CronListener.class).values()) {
            cronTaskRepository.addListener(listener);
        }
        for (StartupProperties properties : applicationContext.getBeansOfType(StartupProperties.class).values()) {
            startupProperties.addProperties(properties);
        }
        LifeStyle lifeStyle = applicationContext.getBean(LifeStyle.class);
        lifeStyle.start(startupProperties);
        cronTasks.clear();
    }
}
