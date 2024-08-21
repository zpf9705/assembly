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
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.MergedBeanDefinitionPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportAware;
import org.springframework.context.annotation.Role;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import top.osjf.cron.core.lifestyle.LifeStyle;
import top.osjf.cron.core.lifestyle.StartupProperties;
import top.osjf.cron.spring.annotation.Cron;
import top.osjf.cron.spring.annotation.MappedAnnotationAttributes;
import top.osjf.cron.spring.cron4j.EnableCron4jCronTaskRegister;
import top.osjf.cron.spring.hutool.EnableHutoolCronTaskRegister;
import top.osjf.cron.spring.quartz.EnableQuartzCronTaskRegister;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Create a post processor for custom bean elements.
 *
 * <p>This post processor mainly monitors each step of obtaining the container
 * object that has already been created, and determines whether it contains a
 * method to wear {@link Cron} annotations.
 * If so, actively register a {@link Runnable} and skip it.
 *
 * <p>The end of execution of {@link #postProcessAfterInitialization(Object, String)}
 * also indicates that the spring container has been refreshed, and all container beans
 * have been placed properly.
 * At this time, it receives the notification of the refreshed event sent by {@link
 * ContextRefreshedEvent} to start the thread operation of the scheduled task.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
@Configuration(proxyBeanMethods = false)
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
public class CronTaskRegisterPostProcessor implements ImportAware, ApplicationContextAware,
        ApplicationListener<ContextRefreshedEvent>, MergedBeanDefinitionPostProcessor, EnvironmentAware, Ordered {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private ApplicationContext applicationContext;

    private Environment environment;

    private RegistrantCollector collector;

    private final StartupProperties properties = StartupProperties.of();

    private final List<Class<?>> annotationClasses = Stream
            .of(EnableHutoolCronTaskRegister.class,
                    EnableCron4jCronTaskRegister.class,
                    EnableQuartzCronTaskRegister.class).collect(Collectors.toList());

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void setEnvironment(@NonNull Environment environment) {
        this.environment = environment;
    }

    @Override
    public void setImportMetadata(@NonNull AnnotationMetadata annotationMetadata) {
        Map<String, Object> map = new HashMap<>();
        for (MergedAnnotation<Annotation> annotation : annotationMetadata.getAnnotations()) {
            if (annotationClasses.contains(annotation.getType())) {
                map.putAll(MappedAnnotationAttributes.of(annotationMetadata.
                        getAnnotationAttributes(annotation.getType().getCanonicalName())));
            }
        }
        properties.addStartupProperties(map);
    }

    @Override
    public void postProcessMergedBeanDefinition(@NonNull RootBeanDefinition beanDefinition, @NonNull Class<?> beanType,
                                                @NonNull String beanName) {
    }

    @Nullable
    @Override
    public Object postProcessAfterInitialization(@NonNull Object bean, @NonNull String beanName) throws BeansException {

        // its target object, and obtain it directly without the proxy.
        Class<?> realBeanType;

        if (AopUtils.isAopProxy(bean)) {
            //If it is a dynamic proxy object, first obtain the class object of
            realBeanType = AopProxyUtils.ultimateTargetClass(bean);
        } else  /*its target object, and obtain it directly without the proxy.*/ realBeanType = bean.getClass();

        //The bean loading of RegistrantCollector does not implement subsequent conditional additions.
        if (collector == null && advanceApprovalOfCondition(realBeanType)) {
            return bean;
        }
        //#applicationContext.getBean(CronTaskRegistrant.class) If the bean is not initialized,
        // the current method flow will be repeated. In this case, return directly.
        if (collector == null) {
            /*
             *      ⬆️⬆️⬆️⬆️⬆️⬆️⬆️⬆️⬆️⬆️⬆️
             * */
            collector = applicationContext.getBean(RegistrantCollector.class);
        }
        try {
            collector.add(realBeanType, bean, environment);
        } catch (Exception e) {
            //Print the stack first.
            e.printStackTrace(System.err);
            if (log.isErrorEnabled()) {
                log.error("Add of timed task for the real type [{}] of bean [{}] failed," +
                        " reason for failure: {}.", realBeanType.getName(), beanName, e.getMessage());
            }
        }
        return bean;
    }

    @Override
    public void onApplicationEvent(@NonNull ContextRefreshedEvent event) {
        if (event.getApplicationContext() == applicationContext) {
            finishRegistration();
        }
    }

    /**
     * Determine whether it is a type that needs to skip registration in advance.
     *
     * @param realBeanType bean real type.
     * @return If {@code true} skips registration and returns registration detection.
     */
    protected boolean advanceApprovalOfCondition(Class<?> realBeanType) {
        return RegistrantCollector.class.isAssignableFrom(realBeanType);
    }

    protected void finishRegistration() {

        //Complete registration.
        CronTaskRealRegistrant realRegistrant = applicationContext.getBean(CronTaskRealRegistrant.class);
        while (collector.hasNext()) {
            Registrant registrant = collector.next();
            if (realRegistrant.supports(registrant)) {
                try {
                    realRegistrant.register(registrant);
                } catch (Exception e) {
                    e.printStackTrace(System.err);
                    if (log.isErrorEnabled()) {
                        log.error("Registration type [{}] task failed, reason for failure [{}]",
                                registrant.getClass().getName(), e.getMessage());
                    }
                }
            }
        }

        //Start scheduled tasks.
        LifeStyle lifeStyle = applicationContext.getBean(LifeStyle.class);

        //start up metadata find in container
        for (StartupProperties value : applicationContext.getBeansOfType(StartupProperties.class).values()) {
            properties.addStartupProperties(value);
        }

        //start up
        lifeStyle.start(properties);

        //Clean up temporary registration resources.
        collector.close();
    }

    @Override
    public int getOrder() {
        return Integer.MIN_VALUE + 10;
    }
}
