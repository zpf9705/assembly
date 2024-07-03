package top.osjf.assembly.simplified.service.context;

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
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.context.event.ContextRefreshedEvent;
import top.osjf.assembly.simplified.service.ServiceContextUtils;
import top.osjf.assembly.simplified.service.annotation.EnableServiceCollection;
import top.osjf.assembly.simplified.service.annotation.EnableServiceCollection2;
import top.osjf.assembly.simplified.service.annotation.EnableServiceCollection3;
import top.osjf.assembly.simplified.support.SmartContextRefreshed;
import top.osjf.assembly.util.annotation.NotNull;
import top.osjf.assembly.util.io.ScanUtils;
import top.osjf.assembly.util.lang.CollectionUtils;
import top.osjf.assembly.util.lang.ConvertUtils;
import top.osjf.assembly.util.lang.ReflectUtils;
import top.osjf.assembly.util.lang.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
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
 * @author zpf
 * @since 2.0.6
 */
public abstract class AbstractServiceContext extends SmartContextRefreshed implements ServiceContext,
        ApplicationContextAware {

    private ApplicationContext context;

    private final Map<String, Object> contextMap = new ConcurrentHashMap<>(16);

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
        @NotNull
        public String generateBeanName(@NotNull BeanDefinition definition, @NotNull BeanDefinitionRegistry registry) {

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


                        if (CollectionUtils.isNotEmpty(filterServices)) {

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

        /*** The package path where the spring main class is located.*/
        private static String mainApplicationPackage;

        /*** Spring configuration context caching.*/
        private static ConfigurableApplicationContext context;

        /*** Do you want to set a new bean name generator.*/
        private static boolean enableCustomBeanNameGeneratorSet;

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
            Class<?> mainApplicationClass = application.getMainApplicationClass();
            mainApplicationPackage = mainApplicationClass.getPackage().getName();

            //There is one of the three annotations that requires setting a bean name generator.
            if (serviceCollectionExist(mainApplicationClass, EnableServiceCollection.class) ||
                    serviceCollectionExist(mainApplicationClass, EnableServiceCollection2.class) ||
                    serviceCollectionExist(mainApplicationClass, EnableServiceCollection3.class)) {
                enableCustomBeanNameGeneratorSet = true;
            }
        }

        /**
         * Returns whether the current project indicates a service annotation.
         *
         * @param mainApplicationClass   The main class for launching the Spring framework.
         * @param serviceAnnotationClass The class object for collecting annotations for the service to be determined.
         * @return If {@code true} the current project indicates a service annotation,otherwise not.
         */
        private static boolean serviceCollectionExist(Class<?> mainApplicationClass,
                                                      Class<? extends Annotation> serviceAnnotationClass) {
            return mainApplicationClass.isAnnotationPresent(serviceAnnotationClass)
                    //Only the package path where the main class is located is restricted here.
                    || !ScanUtils.getTypesAnnotatedWith(serviceAnnotationClass, mainApplicationPackage).isEmpty();
        }

        /**
         * Return the package path where the SpringBoot main class is located.
         *
         * @return the package path where the SpringBoot main class is located.
         */
        public static String getMainApplicationPackage() {
            return mainApplicationPackage;
        }

        /**
         * Set the spring context object for initialization.
         *
         * @param context0 the spring context object for initialization.
         */
        public static void setConfigurableApplicationContext(ConfigurableApplicationContext context0) {
            context = context0;
        }

        /**
         * Return the spring context object for initialization.
         *
         * @return the spring context object for initialization.
         */
        public static ConfigurableApplicationContext getConfigurableApplicationContext() {
            return context;
        }

        /**
         * Clear the cache at runtime.
         */
        public static void clearCache() {
            mainApplicationPackage = null;
            context = null;
            resetBeanNameGeneratorSwitch();
        }

        /**
         * Clear the setting cache of the bean name registration machine.
         */
        public static void resetBeanNameGeneratorSwitch() {
            if (enableCustomBeanNameGeneratorSet) enableCustomBeanNameGeneratorSet = false;
        }

        @Override
        public void contextPrepared(ConfigurableApplicationContext context) {
            setConfigurableApplicationContext(context);
            if (enableCustomBeanNameGeneratorSet) {
                Method method = ReflectUtils.getMethod(context.getClass(), "setBeanNameGenerator",
                        BeanNameGenerator.class);
                if (method != null) {
                    ReflectUtils.invoke(context, method, new ServiceContextBeanNameGenerator(context.getId()));
                }
            }
        }
    }

    @Override
    public void setApplicationContext(@NotNull ApplicationContext context) throws BeansException {
        this.context = context;
    }

    @Override
    public <S> S getService(String serviceName, Class<S> requiredType) throws NoSuchServiceException {
        if (StringUtils.isBlank(serviceName)) throw new NullPointerException("ServiceName must not be null");
        if (requiredType == null) throw new NullPointerException("RequiredType must not be null");
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
            return ConvertUtils.convert(requiredType, obj);
        };
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        super.onApplicationEvent(event);
    }

    @Override
    public void reloadWithScanPackages(String... packages) {
        throw new UnsupportedOperationException();
    }

    @Override
    @NotNull
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
     * Return the package path where the SpringBoot main class is located.
     *
     * @return the package path where the SpringBoot main class is located.
     */
    protected String getMainApplicationPackage() {
        return ServiceContextRunListener.getMainApplicationPackage();
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
     * Return the spring context object for initialization.
     *
     * @return the spring context object for initialization.
     */
    protected ConfigurableApplicationContext getConfigurableApplicationContext() {
        return ServiceContextRunListener.getConfigurableApplicationContext();
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
     * Clear {@link ServiceContextRunListener} intermediate variables.
     */
    protected void clearCache() {
        ServiceContextRunListener.clearCache();
    }

    /**
     * Clear the name of the bean named by the rule.
     */
    protected void clearRecordBeanNames() {
        ServiceContextBeanNameGenerator.clearRecordBeanNames();
    }
}
