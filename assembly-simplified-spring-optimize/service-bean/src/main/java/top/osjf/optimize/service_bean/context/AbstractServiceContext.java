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

package top.osjf.optimize.service_bean.context;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.lang.NonNull;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;
import top.osjf.optimize.service_bean.ServiceContextUtils;
import top.osjf.optimize.service_bean.annotation.EnableServiceCollection;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The abstract help class for the service context.
 *
 * <p>During the startup process of the spring boot project, this class
 * dynamically registered a bean name automatic generator using
 * {@link ServiceContextBeanNameGenerator}.
 * The generator was modified to address the potential duplication of service
 * names in service collection. At the same time, the collection requirement
 * for single class single service main class collection was also checked.
 * When this requirement is not met, the default {@link AnnotationBeanNameGenerator}
 * is still used.
 *
 * <p>At the same time, there is also {@link ServiceContextRunListener} that needs
 * to be paid attention to.
 * In addition to providing variable collection and usage, it can also be horizontally
 * extended in the methods provided by {@link SpringApplicationRunListener} for each cycle.
 * Currently, {@link ServiceContextRunListener#contextPrepared(ConfigurableApplicationContext)}
 * has been rewritten to put the bean name generator {@link ServiceContextBeanNameGenerator}
 * into the container for use before the container refreshes.
 *
 * <p>Next is the default implementation of the service acquisition
 * method {@link #getService(String, Class)}.
 * This method will convert the type based on the provided service name, encode it through
 * {@link ServiceContextUtils#formatId(Class, String, String)} to obtain the real bean name,
 * and then obtain the real service class from the cached {@link #contextMap}.
 * Of course, if it is not obtained, it will directly retrieve the incoming service name
 * and obtain it through the spring context {@link ApplicationContext#getBean(String, Class)}.
 * If the second method still does not obtain it, it will throw {@link NoSuchServiceException},
 * so it is hoped that the consumer, The incoming service name is defined as the full name
 * {@link Class#getName()} of the service class, and it is a real class that exists in the project.
 * Otherwise, an exception {@link ClassNotFoundException} will be thrown.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
public abstract class AbstractServiceContext implements ServiceContext, ApplicationContextAware,
        ApplicationListener<ContextRefreshedEvent> {

    private ApplicationContext context;

    private final Map<String, Object> contextMap = new ConcurrentHashMap<>(16);

    private static final Logger logger = LoggerFactory.getLogger(ServiceContext.class);

    //************************* Help classes ******************************

    /**
     * Customized bean name generator.
     *
     * <p>The main purpose is to address the potential problem of duplicate names
     * in beans during service collection, where the service collection objects are uniformly named.
     *
     * <p>In addition to service collection, bean naming will still follow the
     * default {@link AnnotationBeanNameGenerator}.
     *
     * <p>Custom bean name generation logic: Based on the class full name provided
     * by {@link BeanDefinition#getBeanClassName()}, query its class object.
     * If it is null, directly implement the logic of
     * {@link AnnotationBeanNameGenerator#generateBeanName(BeanDefinition, BeanDefinitionRegistry)}.
     *
     * <p>Otherwise, obtain all the class objects of its parent class and interface,
     * and determine whether there are service collection annotations. Only one class
     * can contain service annotations, and multiple will throw exceptions
     * {@link IllegalArgumentException} to remind. The specific custom bean name
     * policy can be viewed in {@link ServiceContextUtils#formatId(Class, String, String)}.
     */
    public static class ServiceContextBeanNameGenerator extends AnnotationBeanNameGenerator implements BeanNameGenerator {

        private final String applicationId;

        private static final List<String> recordBeanNames = new CopyOnWriteArrayList<>();

        /*** Support custom editing of scope collections for bean names.*/
        private final List<String> scopes = Stream.of(BeanDefinition.SCOPE_SINGLETON,
                        BeanDefinition.SCOPE_PROTOTYPE, AbstractBeanDefinition.SCOPE_DEFAULT)
                .collect(Collectors.toList());

        public ServiceContextBeanNameGenerator(String applicationId) {
            this.applicationId = applicationId;
        }

        /**
         * Returns the name of the bean named by the rule.
         *
         * @return the name of the bean named by the rule.
         */
        public static List<String> getRecordBeanNames() {
            return recordBeanNames;
        }

        /**
         * Clear the name of the bean named by the rule.
         */
        public static void clearRecordBeanNames() {
            recordBeanNames.clear();
        }

        @Override
        @NonNull
        public String generateBeanName(@NonNull BeanDefinition definition, @NonNull BeanDefinitionRegistry registry) {

            String beanName;

            //Service collection only accepts default singletons
            String scope = definition.getScope();
            if (!scopes.contains(scope)) {
                beanName = super.generateBeanName(definition, registry);

            } else {

                //search for the class object based on the class name.
                String beanClassName = definition.getBeanClassName();
                Class<?> clazz = ServiceContextUtils.getClass(beanClassName);

                //Unknown class object, not processed.
                if (clazz == null) {
                    beanName = super.generateBeanName(definition, registry);

                } else {

                    //Single instance beans perform service collection operations.
                    List<Class<?>> filterServices = ServiceContextUtils.getFilterServices(clazz);

                    //If no collection flag is found, the default bean name definition rule will be used.
                    if (CollectionUtils.isEmpty(filterServices)) {
                        beanName = super.generateBeanName(definition, registry);

                    } else {

                        //Service collection name definition rules.
                        String value;
                        List<String> classAlisa;
                        if (definition instanceof AnnotatedBeanDefinition) {
                            //Get the name of the Spring build annotation.
                            value = determineBeanNameFromAnnotation((AnnotatedBeanDefinition) definition);
                            if (StringUtils.isBlank(value)) {
                                value = clazz.getName();
                                classAlisa = ServiceContextUtils.analyzeClassAlias(clazz, true);
                            } else {
                                classAlisa = ServiceContextUtils.analyzeClassAlias(clazz, false);
                            }
                        } else {
                            value = clazz.getName();
                            classAlisa = ServiceContextUtils.analyzeClassAlias(clazz, true);
                        }

                        Class<?> ms = filterServices.get(0);

                        //Format the main bean name according to the rules first.
                        beanName = ServiceContextUtils.formatId(ms, value, applicationId);

                        //Cache the name of the main bean.
                        recordBeanNames.add(beanName);

                        //The alias constructed by the first superior class object.
                        classAlisa.forEach(alisa -> registry.registerAlias(beanName,
                                ServiceContextUtils.formatAlisa(ms, alisa, applicationId)));

                        //Remove the first level class that has been built.
                        filterServices.remove(0);


                        if (!CollectionUtils.isEmpty(filterServices)) {

                            //Build an alias for the parent class.
                            for (Class<?> filterService : filterServices) {
                                registry.registerAlias(beanName, ServiceContextUtils.formatAlisa(filterService,
                                        value, applicationId));
                                classAlisa.forEach(alias0 -> registry.registerAlias(beanName,
                                        ServiceContextUtils.formatAlisa(filterService, alias0, applicationId)));
                            }
                        }
                    }
                }
            }
            return beanName;
        }
    }

    /**
     * Start listening class for service context.
     *
     * <p>Using {@link SpringApplicationRunListener} implementation, it is mainly achieved
     * through the construction method {@link #ServiceContextRunListener(SpringApplication, String[])},
     * providing extension assistance in various stages of spring boot application startup.
     *
     * <p>The original extension {@link #contextPrepared(ConfigurableApplicationContext)} was to put
     * in a custom bean name generator.
     */
    public static class ServiceContextRunListener implements SpringApplicationRunListener {

        /*** The main class for launching the Spring framework.*/
        private Class<?> mainApplicationClass;

        /**
         * The empty structure here is mainly used for configuration purposes.
         */
        public ServiceContextRunListener() {
        }

        /**
         * The necessary constructor for using {@link SpringApplicationRunListener}.
         * <p>Is the same object as calling {@link #started(ConfigurableApplicationContext)}.
         *
         * @param application Spring's application startup class.
         * @param args        The startup parameters for the application startup class of Spring.
         */
        public ServiceContextRunListener(SpringApplication application, String[] args) {

            //Determine whether to add a custom bean name generator based on the existence of adaptive annotations.
            mainApplicationClass = application.getMainApplicationClass();
        }

        /**
         * Returns whether the current project indicates a service annotation.
         *
         * @return If {@code true} the current project indicates a service annotation,otherwise not.
         */
        private boolean serviceCollectionExistInMainApplicationClassPackage() {
            Class<EnableServiceCollection> type = EnableServiceCollection.class;
            if (mainApplicationClass.isAnnotationPresent(type)) {
                return true;
            }
            ClassPathScanningCandidateComponentProvider provider
                    = new ClassPathScanningCandidateComponentProvider(false) {
                @Override
                protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
                    AnnotationMetadata metadata = beanDefinition.getMetadata();
                    return metadata.isIndependent()
                            && !Objects.equals(metadata.getSuperClassName(), Enum.class.getName());
                }
            };
            provider.addIncludeFilter(new AnnotationTypeFilter(type));
            String packageName = mainApplicationClass.getPackage().getName();
            while (packageName.contains(".")) {
                packageName = packageName.substring(0, packageName.lastIndexOf("."));
                if (!provider.findCandidateComponents(packageName).isEmpty()) {
                    return true;
                }
            }
            return false;
        }

        @Override
        //<!-- https://mvnrepository.com/artifact/top.osjf.spring.optimize/service-bean Fix the errors in this method-->
        public void contextPrepared(ConfigurableApplicationContext context) {
            //Check if the following method for placing bean name generator has been called.
            if (context.getBeanFactory().containsBean(AnnotationConfigUtils.CONFIGURATION_BEAN_NAME_GENERATOR)) {
                return;
            }
            //The existence of @EnableServiceCollection will be scanned based on the package
            // path where the main class is launched.
            if (serviceCollectionExistInMainApplicationClassPackage()) {
                setForApplicationContextCustomBeanNameGenerator(context);
            }
        }

        /**
         * Set up a custom bean name generator {@code BeanNameGenerator} for
         * {@link ConfigurableApplicationContext}, mainly to adapt to the current
         * framework components.
         *
         * <p>Users can call this method at different stages of Spring's startup
         * lifecycle to set custom {@code BeanNameGenerator}, but if you need to define
         * it in advance, be sure to do so before the {@link #contextPrepared} method,
         * otherwise the existence of {@code @EnableServiceCollection} will be scanned based
         * on the package path where the main class is launched.
         *
         * @since 1.0.0-repair
         * @param context Spring context.
         */
        public static void setForApplicationContextCustomBeanNameGenerator(ConfigurableApplicationContext context) {
            Class<? extends ConfigurableApplicationContext> contextClass = context.getClass();
            Method method = ReflectionUtils
                    .findMethod(contextClass, "setBeanNameGenerator", BeanNameGenerator.class);
            if (method != null) {
                ReflectionUtils.invokeMethod(method, context, new ServiceContextBeanNameGenerator(context.getId()));
                if (logger.isDebugEnabled()) {
                    logger.debug("Put the custom bean name generator [{}] into the Spring context [{}].",
                            contextClass.getName(), ServiceContextBeanNameGenerator.class.getName());
                }
            } else {
                if (logger.isWarnEnabled()) {
                    logger.warn("Method [{}] was not found in the Spring context [{}]," +
                                    " therefore [{}] component failed!",
                            "setBeanNameGenerator(BeanNameGenerator)",
                            contextClass,
                            "service-bean");
                }
            }
        }
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext context) throws BeansException {
        this.context = context;
    }

    @Override
    public <S> S getService(String serviceName, Class<S> requiredType) throws NoSuchServiceException {
        if (StringUtils.isBlank(serviceName) || requiredType == null) {
            throw new NullPointerException("serviceName or requiredType");
        }
        S service = ServiceContextUtils
                .getService(serviceName, requiredType, context.getId(), getService(requiredType));
        if (service == null) {
            //Throw an exception that cannot be found.
            throw new NoSuchServiceException(serviceName, requiredType);
        }
        return service;
    }

    /**
     * Return the {@link Function} of the parsing service.
     *
     * @param requiredType type the bean must match; can be an interface or superclass.
     * @param <S>          types of required.
     * @return an instance of the service.
     */
    <S> Function<String, S> getService(Class<S> requiredType) {
        return encodeServiceName -> {
            Object obj = contextMap.get(encodeServiceName);
            if (obj == null) {
                return null;
            }
            return requiredType.cast(obj);
        };
    }

    @Override
    public void onApplicationEvent(@NonNull ContextRefreshedEvent event) {
    }

    @Override
    @NonNull
    public ApplicationContext getApplicationContext() {
        return context;
    }

    @Override
    public void close() {
        contextMap.clear();
    }

    //———————————————————————————————————— info op

    /**
     * Return service mapping cache.
     *
     * @return service mapping cache.
     */
    protected Map<String, Object> getContextMap() {
        return contextMap;
    }

    /**
     * Return the bean registration machine for Spring.
     *
     * @return the bean registration machine for Spring.
     */
    protected BeanDefinitionRegistry getBeanDefinitionRegistry() {
        return (BeanDefinitionRegistry) context;
    }

    /**
     * Returns the name of the bean named by the rule.
     *
     * @return the name of the bean named by the rule.
     */
    protected List<String> getRecordBeanNames() {
        return ServiceContextBeanNameGenerator.getRecordBeanNames();
    }

    //———————————————————————————————————— clear op

    /**
     * Clear the name of the bean named by the rule.
     */
    protected void clearRecordBeanNames() {
        ServiceContextBeanNameGenerator.clearRecordBeanNames();
    }
}
