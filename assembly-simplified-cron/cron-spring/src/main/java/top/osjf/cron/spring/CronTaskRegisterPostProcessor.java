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

import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.MergedBeanDefinitionPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.ApplicationListener;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportAware;
import org.springframework.context.annotation.Role;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;
import top.osjf.cron.core.annotation.NotNull;
import top.osjf.cron.core.annotation.Nullable;
import top.osjf.cron.core.lifestyle.LifeStyle;
import top.osjf.cron.spring.annotation.Cron;
import top.osjf.cron.spring.annotation.MappedAnnotationAttributes;

import java.util.LinkedHashMap;
import java.util.Map;

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
public class CronTaskRegisterPostProcessor implements ImportAware,
        ApplicationListener<ContextRefreshedEvent>, MergedBeanDefinitionPostProcessor, EnvironmentAware, Ordered {

    private Environment environment;

    private Map<String, Object> metadata;

    private final LifeStyle lifeStyle;

    private final CronTaskRegistrant cronTaskRegistrant;

    /**
     * The construction methods of dependency lifecycle interface and registration
     * interface are used to complete registration tasks and startup. This special
     * bean depends on other objects, which will cause it to lose its automatic proxy
     * function. Please mark the dependent bean as {@link BeanDefinition#ROLE_INFRASTRUCTURE}
     * or use lazy loading {@link org.springframework.context.annotation.Lazy}.
     *
     * @param lifeStyle          Lifecycle implementation bean.
     * @param cronTaskRegistrant Task registration implementation bean.
     */
    public CronTaskRegisterPostProcessor(LifeStyle lifeStyle,
                                         CronTaskRegistrant cronTaskRegistrant) {
        this.lifeStyle = lifeStyle;
        this.cronTaskRegistrant = cronTaskRegistrant;
    }

    /**
     * Manually set startup metadata.
     *
     * @param metadata startup metadata.
     */
    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    @Override
    public void setEnvironment(@NotNull Environment environment) {
        this.environment = environment;
    }

    @Override
    public void setImportMetadata(@NotNull AnnotationMetadata annotationMetadata) {
        metadata = new LinkedHashMap<>();
        annotationMetadata.getAnnotations().forEach(annotation ->
                metadata.putAll(MappedAnnotationAttributes.of(annotationMetadata.
                        getAnnotationAttributes(annotation.getType().getCanonicalName()))));
    }

    @Override
    public void postProcessMergedBeanDefinition(@NotNull RootBeanDefinition beanDefinition, @NotNull Class<?> beanType,
                                                @NotNull String beanName) {
    }

    @Nullable
    @Override
    public Object postProcessAfterInitialization(@NotNull Object bean, @NotNull String beanName) throws BeansException {
        //If it is a dynamic proxy object, first obtain the class object of
        // its target object, and obtain it directly without the proxy.
        Class<?> target;
        if (AopUtils.isAopProxy(bean)) {
            target = AopProxyUtils.ultimateTargetClass(bean);
        } else {
            target = bean.getClass();
        }
        try {
            cronTaskRegistrant.register(target, bean, environment);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return bean;
    }

    @Override
    public void onApplicationEvent(@NotNull ContextRefreshedEvent event) {
        lifeStyle.start(metadata);
    }

    @Override
    public int getOrder() {
        return Integer.MIN_VALUE + 16;
    }
}
