/*
 * Copyright 2002-2021 the original author or authors.
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

package top.osjf.cron.spring.scheduler.config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.framework.AopInfrastructureBean;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.*;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.DestructionAwareBeanPostProcessor;
import org.springframework.beans.factory.config.NamedBeanHolder;
import org.springframework.beans.factory.support.MergedBeanDefinitionPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.env.Environment;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.annotation.*;
import org.springframework.scheduling.config.*;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.scheduling.support.ScheduledMethodRunnable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.util.StringValueResolver;
import top.osjf.cron.core.support.ExpressionSupport;
import top.osjf.cron.core.util.ArrayUtils;
import top.osjf.cron.spring.annotation.Cron;
import top.osjf.cron.spring.annotation.Crones;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Bean post-processor that registers methods annotated with
 * {@link Scheduled @Scheduled} and {@link Cron @Cron} to be invoked by a
 * {@link org.springframework.scheduling.TaskScheduler} according to the
 * "fixedRate", "fixedDelay", or "cron" expression provided via the annotation.
 *
 * <p>This post-processor is automatically registered by Spring's
 * {@code <task:annotation-driven>} XML element, and also by the
 * {@link EnableScheduling @EnableScheduling} annotation.
 *
 * <p>Autodetects any {@link SchedulingConfigurer} instances in the container,
 * allowing for customization of the scheduler to be used or for fine-grained
 * control over task registration (e.g. registration of {@link Trigger} tasks).
 * See the {@link EnableScheduling @EnableScheduling} javadocs for complete usage
 * details.
 *
 * @author Mark Fisher
 * @author Juergen Hoeller
 * @author Chris Beams
 * @author Elizabeth Chatman
 * @author Victor Brown
 * @author Sam Brannen
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0
 * @see Scheduled
 * @see Cron
 * @see EnableScheduling
 * @see SchedulingConfigurer
 * @see org.springframework.scheduling.TaskScheduler
 * @see org.springframework.scheduling.config.ScheduledTaskRegistrar
 * @see AsyncAnnotationBeanPostProcessor
 */
public class ScheduledAnnotationBeanPostProcessor
        implements ScheduledTaskHolder, MergedBeanDefinitionPostProcessor, DestructionAwareBeanPostProcessor,
        Ordered, EmbeddedValueResolverAware, BeanNameAware, BeanFactoryAware, ApplicationContextAware,
        SmartInitializingSingleton, ApplicationListener<ContextRefreshedEvent>, DisposableBean {

    /**
     * The default name of the {@link TaskScheduler} bean to pick up: {@value}.
     * <p>Note that the initial lookup happens by type; this is just the fallback
     * in case of multiple scheduler beans found in the context.
     *
     * @since 4.2
     */
    public static final String DEFAULT_TASK_SCHEDULER_BEAN_NAME = "taskScheduler";


    protected final Log logger = LogFactory.getLog(getClass());

    private final ScheduledTaskRegistrar registrar;

    @Nullable
    private Object scheduler;

    @Nullable
    private StringValueResolver embeddedValueResolver;

    @Nullable
    private String beanName;

    @Nullable
    private BeanFactory beanFactory;

    @Nullable
    private ApplicationContext applicationContext;

    @Nullable
    private Environment environment;

    private final Set<Class<?>> nonAnnotatedClasses = Collections.newSetFromMap(new ConcurrentHashMap<>(64));

    private final Map<Object, Set<ScheduledTask>> scheduledTasks = new IdentityHashMap<>(16);


    /**
     * Create a default {@code ScheduledAnnotationBeanPostProcessor}.
     */
    public ScheduledAnnotationBeanPostProcessor() {
        this.registrar = new ScheduledTaskRegistrar();
    }

    /**
     * Create a {@code ScheduledAnnotationBeanPostProcessor} delegating to the
     * specified {@link ScheduledTaskRegistrar}.
     *
     * @param registrar the ScheduledTaskRegistrar to register {@code @Scheduled}
     *                  tasks on
     * @since 5.1
     */
    public ScheduledAnnotationBeanPostProcessor(ScheduledTaskRegistrar registrar) {
        Assert.notNull(registrar, "ScheduledTaskRegistrar is required");
        this.registrar = registrar;
    }


    @Override
    public int getOrder() {
        return LOWEST_PRECEDENCE;
    }

    /**
     * Set the {@link TaskScheduler} that will invoke
     * the scheduled methods, or a {@link ScheduledExecutorService}
     * to be wrapped as a TaskScheduler.
     * <p>If not specified, default scheduler resolution will apply: searching for a
     * unique {@link TaskScheduler} bean in the context, or for a {@link TaskScheduler}
     * bean named "taskScheduler" otherwise; the same lookup will also be performed for
     * a {@link ScheduledExecutorService} bean. If neither of the two is resolvable,
     * a local single-threaded default scheduler will be created within the registrar.
     *
     * @param scheduler the {@link TaskScheduler} instance.
     * @see #DEFAULT_TASK_SCHEDULER_BEAN_NAME
     */
    public void setScheduler(Object scheduler) {
        this.scheduler = scheduler;
    }

    @Override
    public void setEmbeddedValueResolver(StringValueResolver resolver) {
        this.embeddedValueResolver = resolver;
    }

    @Override
    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    /**
     * Making a {@link BeanFactory} available is optional; if not set,
     * {@link SchedulingConfigurer} beans won't get autodetected and
     * a {@link #setScheduler scheduler} has to be explicitly configured.
     */
    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    /**
     * Setting an {@link ApplicationContext} is optional: If set, registered
     * tasks will be activated in the {@link ContextRefreshedEvent} phase;
     * if not set, it will happen at {@link #afterSingletonsInstantiated} time.
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        this.environment = applicationContext.getEnvironment();
        if (this.beanFactory == null) {
            this.beanFactory = applicationContext;
        }
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
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (event.getApplicationContext() == this.applicationContext) {
            // Running in an ApplicationContext -> register tasks this late...
            // giving other ContextRefreshedEvent listeners a chance to perform
            // their work at the same time (e.g. Spring Batch's job registration).
            finishRegistration();
        }
    }

    private void finishRegistration() {
        if (this.scheduler != null) {
            this.registrar.setScheduler(this.scheduler);
        }

        if (this.beanFactory instanceof ListableBeanFactory) {
            Map<String, SchedulingConfigurer> beans =
                    ((ListableBeanFactory) this.beanFactory).getBeansOfType(SchedulingConfigurer.class);
            List<SchedulingConfigurer> configurers = new ArrayList<>(beans.values());
            AnnotationAwareOrderComparator.sort(configurers);
            for (SchedulingConfigurer configurer : configurers) {
                configurer.configureTasks(this.registrar);
            }
        }

        if (this.registrar.hasTasks() && this.registrar.getScheduler() == null) {
            Assert.state(this.beanFactory != null, "BeanFactory must be set to find scheduler by type");
            try {
                // Search for TaskScheduler bean...
                this.registrar.setTaskScheduler(resolveSchedulerBean(this.beanFactory, TaskScheduler.class, false));
            } catch (NoUniqueBeanDefinitionException ex) {
                if (logger.isTraceEnabled()) {
                    logger.trace("Could not find unique TaskScheduler bean - attempting to resolve by name: " +
                            ex.getMessage());
                }
                try {
                    this.registrar.setTaskScheduler(resolveSchedulerBean(this.beanFactory, TaskScheduler.class, true));
                } catch (NoSuchBeanDefinitionException ex2) {
                    if (logger.isInfoEnabled()) {
                        logger.info("More than one TaskScheduler bean exists within the context, and " +
                                "none is named 'taskScheduler'. Mark one of them as primary or name it 'taskScheduler' " +
                                "(possibly as an alias); or implement the SchedulingConfigurer interface and call " +
                                "ScheduledTaskRegistrar#setScheduler explicitly within the configureTasks() callback: " +
                                ex.getBeanNamesFound());
                    }
                }
            } catch (NoSuchBeanDefinitionException ex) {
                if (logger.isTraceEnabled()) {
                    logger.trace("Could not find default TaskScheduler bean - attempting to find ScheduledExecutorService: " +
                            ex.getMessage());
                }
                // Search for ScheduledExecutorService bean next...
                try {
                    this.registrar.setScheduler(resolveSchedulerBean(this.beanFactory, ScheduledExecutorService.class, false));
                } catch (NoUniqueBeanDefinitionException ex2) {
                    if (logger.isTraceEnabled()) {
                        logger.trace("Could not find unique ScheduledExecutorService bean - attempting to resolve by name: " +
                                ex2.getMessage());
                    }
                    try {
                        this.registrar.setScheduler(resolveSchedulerBean(this.beanFactory, ScheduledExecutorService.class, true));
                    } catch (NoSuchBeanDefinitionException ex3) {
                        if (logger.isInfoEnabled()) {
                            logger.info("More than one ScheduledExecutorService bean exists within the context, and " +
                                    "none is named 'taskScheduler'. Mark one of them as primary or name it 'taskScheduler' " +
                                    "(possibly as an alias); or implement the SchedulingConfigurer interface and call " +
                                    "ScheduledTaskRegistrar#setScheduler explicitly within the configureTasks() callback: " +
                                    ex2.getBeanNamesFound());
                        }
                    }
                } catch (NoSuchBeanDefinitionException ex2) {
                    if (logger.isTraceEnabled()) {
                        logger.trace("Could not find default ScheduledExecutorService bean - falling back to default: " +
                                ex2.getMessage());
                    }
                    // Giving up -> falling back to default scheduler within the registrar...
                    logger.info("No TaskScheduler/ScheduledExecutorService bean found for scheduled processing");
                }
            }
        }

        this.registrar.afterPropertiesSet();
    }

    private <T> T resolveSchedulerBean(BeanFactory beanFactory, Class<T> schedulerType, boolean byName) {
        if (byName) {
            T scheduler = beanFactory.getBean(DEFAULT_TASK_SCHEDULER_BEAN_NAME, schedulerType);
            if (this.beanName != null && this.beanFactory instanceof ConfigurableBeanFactory) {
                ((ConfigurableBeanFactory) this.beanFactory).registerDependentBean(
                        DEFAULT_TASK_SCHEDULER_BEAN_NAME, this.beanName);
            }
            return scheduler;
        } else if (beanFactory instanceof AutowireCapableBeanFactory) {
            NamedBeanHolder<T> holder = ((AutowireCapableBeanFactory) beanFactory).resolveNamedBean(schedulerType);
            if (this.beanName != null && beanFactory instanceof ConfigurableBeanFactory) {
                ((ConfigurableBeanFactory) beanFactory).registerDependentBean(holder.getBeanName(), this.beanName);
            }
            return holder.getBeanInstance();
        } else {
            return beanFactory.getBean(schedulerType);
        }
    }


    @Override
    public void postProcessMergedBeanDefinition(RootBeanDefinition beanDefinition, Class<?> beanType, String beanName) {
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        if (bean instanceof AopInfrastructureBean || bean instanceof TaskScheduler ||
                bean instanceof ScheduledExecutorService) {
            // Ignore AOP infrastructure such as scoped proxies.
            return bean;
        }

        Class<?> targetClass = AopProxyUtils.ultimateTargetClass(bean);
        if (!this.nonAnnotatedClasses.contains(targetClass) &&
                AnnotationUtils.isCandidateClass(targetClass, Arrays.asList(Scheduled.class, Schedules.class,
                        Cron.class, Crones.class))) {
            Map<Method, Set<? extends Annotation>> annotatedMethods = getTargetAnnotatedMethods(targetClass);
            if (annotatedMethods.isEmpty()) {
                this.nonAnnotatedClasses.add(targetClass);
                if (logger.isTraceEnabled()) {
                    logger.trace("No @Scheduled annotations found on bean class: " + targetClass);
                }
            } else {
                // Non-empty set of methods
                annotatedMethods.forEach((method, scheduledAnnotations) ->
                        scheduledAnnotations.forEach(scheduled -> processScheduled(scheduled, method, bean)));
                if (logger.isTraceEnabled()) {
                    logger.trace(annotatedMethods.size() + " @Scheduled methods processed on bean '" + beanName +
                            "': " + annotatedMethods);
                }
            }
        }
        return bean;
    }

    private Map<Method, Set<? extends Annotation>> getTargetAnnotatedMethods(Class<?> targetClass) {
        Map<Method, Set<? extends Annotation>> annotatedMethods = new HashMap<>();
        annotatedMethods.putAll(getAnnotatedMethods(targetClass, Scheduled.class, Schedules.class));
        annotatedMethods.putAll(getAnnotatedMethods(targetClass, Cron.class, Crones.class));
        return annotatedMethods;
    }


    private Map<Method, Set<? extends Annotation>> getAnnotatedMethods(Class<?> targetClass,
                                                                       Class<? extends Annotation> annotationType,
                                                                       Class<? extends Annotation> containerType) {
        return MethodIntrospector.selectMethods(targetClass,
                (MethodIntrospector.MetadataLookup<Set<? extends Annotation>>) method -> {
                    Set<? extends Annotation> scheduledAnnotations = AnnotatedElementUtils.getMergedRepeatableAnnotations(
                            method, annotationType, containerType);
                    return (!scheduledAnnotations.isEmpty() ? scheduledAnnotations : null);
                });
    }

    protected void processScheduled(Annotation annotation, Method method, Object bean) {
        if (annotation instanceof Scheduled) {
            processScheduled((Scheduled) annotation, method, bean);
        } else if (annotation instanceof Cron) {
            processCrones((Cron) annotation, method, bean);
        }
    }

    /**
     * Process the given {@code @Scheduled} method declaration on the given bean.
     *
     * @param scheduled the {@code @Scheduled} annotation
     * @param method    the method that the annotation has been declared on
     * @param bean      the target bean instance
     * @see #createRunnable(Object, Method)
     */
    protected void processScheduled(Scheduled scheduled, Method method, Object bean) {
        try {
            Runnable runnable = createRunnable(bean, method);
            boolean processedSchedule = false;
            String errorMessage =
                    "Exactly one of the 'cron', 'fixedDelay(String)', or 'fixedRate(String)' attributes is required";

            Set<ScheduledTask> tasks = new LinkedHashSet<>(4);

            // Determine initial delay
            long initialDelay = convertToMillis(scheduled.initialDelay(), scheduled.timeUnit());
            String initialDelayString = scheduled.initialDelayString();
            if (StringUtils.hasText(initialDelayString)) {
                Assert.isTrue(initialDelay < 0, "Specify 'initialDelay' or 'initialDelayString', not both");
                if (this.embeddedValueResolver != null) {
                    initialDelayString = this.embeddedValueResolver.resolveStringValue(initialDelayString);
                }
                if (StringUtils.hasLength(initialDelayString)) {
                    try {
                        initialDelay = convertToMillis(initialDelayString, scheduled.timeUnit());
                    } catch (RuntimeException ex) {
                        throw new IllegalArgumentException(
                                "Invalid initialDelayString value \"" + initialDelayString + "\" - cannot parse into long");
                    }
                }
            }

            // Check cron expression
            String cron = scheduled.cron();
            if (StringUtils.hasText(cron)) {
                String zone = scheduled.zone();
                if (this.embeddedValueResolver != null) {
                    cron = this.embeddedValueResolver.resolveStringValue(cron);
                    zone = this.embeddedValueResolver.resolveStringValue(zone);
                }
                if (StringUtils.hasLength(cron)) {
                    Assert.isTrue(initialDelay == -1, "'initialDelay' not supported for cron triggers");
                    processedSchedule = true;
                    if (!Scheduled.CRON_DISABLED.equals(cron)) {
                        TimeZone timeZone;
                        if (StringUtils.hasText(zone)) {
                            timeZone = StringUtils.parseTimeZoneString(zone);
                        } else {
                            timeZone = TimeZone.getDefault();
                        }
                        tasks.add(this.registrar.scheduleCronTask(new CronTask(runnable, new CronTrigger(cron, timeZone))));
                    }
                }
            }

            // At this point we don't need to differentiate between initial delay set or not anymore
            if (initialDelay < 0) {
                initialDelay = 0;
            }

            // Check fixed delay
            long fixedDelay = convertToMillis(scheduled.fixedDelay(), scheduled.timeUnit());
            if (fixedDelay >= 0) {
                Assert.isTrue(!processedSchedule, errorMessage);
                processedSchedule = true;
                tasks.add(this.registrar.scheduleFixedDelayTask(new FixedDelayTask(runnable, fixedDelay, initialDelay)));
            }

            String fixedDelayString = scheduled.fixedDelayString();
            if (StringUtils.hasText(fixedDelayString)) {
                if (this.embeddedValueResolver != null) {
                    fixedDelayString = this.embeddedValueResolver.resolveStringValue(fixedDelayString);
                }
                if (StringUtils.hasLength(fixedDelayString)) {
                    Assert.isTrue(!processedSchedule, errorMessage);
                    processedSchedule = true;
                    try {
                        fixedDelay = convertToMillis(fixedDelayString, scheduled.timeUnit());
                    } catch (RuntimeException ex) {
                        throw new IllegalArgumentException(
                                "Invalid fixedDelayString value \"" + fixedDelayString + "\" - cannot parse into long");
                    }
                    tasks.add(this.registrar.scheduleFixedDelayTask(new FixedDelayTask(runnable, fixedDelay, initialDelay)));
                }
            }

            // Check fixed rate
            long fixedRate = convertToMillis(scheduled.fixedRate(), scheduled.timeUnit());
            if (fixedRate >= 0) {
                Assert.isTrue(!processedSchedule, errorMessage);
                processedSchedule = true;
                tasks.add(this.registrar.scheduleFixedRateTask(new FixedRateTask(runnable, fixedRate, initialDelay)));
            }
            String fixedRateString = scheduled.fixedRateString();
            if (StringUtils.hasText(fixedRateString)) {
                if (this.embeddedValueResolver != null) {
                    fixedRateString = this.embeddedValueResolver.resolveStringValue(fixedRateString);
                }
                if (StringUtils.hasLength(fixedRateString)) {
                    Assert.isTrue(!processedSchedule, errorMessage);
                    processedSchedule = true;
                    try {
                        fixedRate = convertToMillis(fixedRateString, scheduled.timeUnit());
                    } catch (RuntimeException ex) {
                        throw new IllegalArgumentException(
                                "Invalid fixedRateString value \"" + fixedRateString + "\" - cannot parse into long");
                    }
                    tasks.add(this.registrar.scheduleFixedRateTask(new FixedRateTask(runnable, fixedRate, initialDelay)));
                }
            }

            // Check whether we had any attribute set
            Assert.isTrue(processedSchedule, errorMessage);

            // Finally register the scheduled tasks
            synchronized (this.scheduledTasks) {
                Set<ScheduledTask> regTasks = this.scheduledTasks.computeIfAbsent(bean, key -> new LinkedHashSet<>(4));
                regTasks.addAll(tasks);
            }
        } catch (IllegalArgumentException ex) {
            throw new IllegalStateException(
                    "Encountered invalid @Scheduled method '" + method.getName() + "': " + ex.getMessage());
        }
    }

    /**
     * Process the given {@code @Cron} method declaration on the given bean.
     *
     * @param cron   the {@code @Cron} annotation
     * @param method the method that the annotation has been declared on
     * @param bean   the target bean instance
     * @see #createRunnable(Object, Method)
     */
    protected void processCrones(Cron cron, Method method, Object bean) {
        String[] profiles = cron.profiles();
        if (environment != null && !ArrayUtils.isEmpty(profiles)) {
            List<String> activeProfiles = Arrays.asList(environment.getActiveProfiles());
            if (Arrays.stream(profiles).noneMatch(activeProfiles::contains)) {
                logger.info("Could not register method " + method.getName() + " for bean type "
                        + method.getDeclaringClass().getName() + " task: environment not compatible.");
                return;
            }
        }
        Runnable runnable = createRunnable(bean, method);
        String expression = cron.expression();
        if (!StringUtils.hasText(expression)) {
            expression = ExpressionSupport.secondLevelDefaultExpression();
        }
        CronTrigger cronTrigger = new CronTrigger(expression);
        CronTask cronTask = new CronTask(runnable, cronTrigger);
        ScheduledTask scheduledTask = this.registrar.scheduleCronTask(cronTask);
        synchronized (this.scheduledTasks) {
            Set<ScheduledTask> regTasks = this.scheduledTasks.computeIfAbsent(bean, key -> new LinkedHashSet<>(5));
            regTasks.add(scheduledTask);
        }
    }

    /**
     * Create a {@link Runnable} for the given bean instance,
     * calling the specified scheduled method.
     * <p>The default implementation creates a {@link ScheduledMethodRunnable}.
     *
     * @param target the target bean instance
     * @param method the scheduled method to call
     * @see ScheduledMethodRunnable#ScheduledMethodRunnable(Object, Method)
     * @return a {@link Runnable} for the given bean instance.
     * @since 5.1
     */
    protected Runnable createRunnable(Object target, Method method) {
        Assert.isTrue(method.getParameterCount() == 0, "Only no-arg methods may be annotated with @Scheduled");
        Method invocableMethod = AopUtils.selectInvocableMethod(method, target.getClass());
        return new ScheduledMethodRunnable(target, invocableMethod);
    }

    private static long convertToMillis(long value, TimeUnit timeUnit) {
        return TimeUnit.MILLISECONDS.convert(value, timeUnit);
    }

    private static long convertToMillis(String value, TimeUnit timeUnit) {
        if (isDurationString(value)) {
            return Duration.parse(value).toMillis();
        }
        return convertToMillis(Long.parseLong(value), timeUnit);
    }

    private static boolean isDurationString(String value) {
        return (value.length() > 1 && (isP(value.charAt(0)) || isP(value.charAt(1))));
    }

    private static boolean isP(char ch) {
        return (ch == 'P' || ch == 'p');
    }


    /**
     * Return all currently scheduled tasks, from {@link Scheduled} methods
     * as well as from programmatic {@link SchedulingConfigurer} interaction.
     *
     * @since 5.0.2
     */
    @Override
    public Set<ScheduledTask> getScheduledTasks() {
        Set<ScheduledTask> result = new LinkedHashSet<>();
        synchronized (this.scheduledTasks) {
            Collection<Set<ScheduledTask>> allTasks = this.scheduledTasks.values();
            for (Set<ScheduledTask> tasks : allTasks) {
                result.addAll(tasks);
            }
        }
        result.addAll(this.registrar.getScheduledTasks());
        return result;
    }

    @Override
    public void postProcessBeforeDestruction(Object bean, String beanName) {
        Set<ScheduledTask> tasks;
        synchronized (this.scheduledTasks) {
            tasks = this.scheduledTasks.remove(bean);
        }
        if (tasks != null) {
            for (ScheduledTask task : tasks) {
                task.cancel();
            }
        }
    }

    @Override
    public boolean requiresDestruction(Object bean) {
        synchronized (this.scheduledTasks) {
            return this.scheduledTasks.containsKey(bean);
        }
    }

    @Override
    public void destroy() {
        synchronized (this.scheduledTasks) {
            Collection<Set<ScheduledTask>> allTasks = this.scheduledTasks.values();
            for (Set<ScheduledTask> tasks : allTasks) {
                for (ScheduledTask task : tasks) {
                    task.cancel();
                }
            }
            this.scheduledTasks.clear();
        }
        this.registrar.destroy();
    }

}
