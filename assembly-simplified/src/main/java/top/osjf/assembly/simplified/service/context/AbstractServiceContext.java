package top.osjf.assembly.simplified.service.context;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
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
import top.osjf.assembly.simplified.support.SmartContextRefreshed;
import top.osjf.assembly.util.annotation.NotNull;
import top.osjf.assembly.util.data.ClassMap;
import top.osjf.assembly.util.data.ThreadSafeClassMap;
import top.osjf.assembly.util.io.ScanUtils;
import top.osjf.assembly.util.lang.CollectionUtils;
import top.osjf.assembly.util.lang.ReflectUtils;
import top.osjf.assembly.util.lang.StringUtils;

import java.lang.reflect.Method;
import java.util.List;

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

    private final ClassMap<String, Object> contextMap = new ThreadSafeClassMap<>(16);

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

        public ServiceContextBeanNameGenerator(String applicationId) {
            this.applicationId = applicationId;
        }

        @Override
        @NotNull
        public String generateBeanName(@NotNull BeanDefinition definition, @NotNull BeanDefinitionRegistry registry) {
            String beanName;
            String beanClassName = definition.getBeanClassName();
            Class<?> clazz = ServiceContextUtils.getClass(beanClassName);
            if (clazz == null) {
                beanName = super.generateBeanName(definition, registry);
            } else {
                List<Class<?>> filterServices = ServiceContextUtils.getFilterServices(clazz);
                if (CollectionUtils.isEmpty(filterServices)) {
                    beanName = super.generateBeanName(definition, registry);
                } else {
                    String value;
                    List<String> classAlisa;
                    if (definition instanceof AnnotatedBeanDefinition) {
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
                    //Normal name.
                    beanName = ServiceContextUtils.formatId(filterServices.get(0),
                            value, applicationId);
                    //The alias constructed by the first superior class object.
                    classAlisa.forEach(alisa -> registry.registerAlias(beanName,
                            ServiceContextUtils.formatAlisa(filterServices.get(0),
                                    alisa, applicationId)));
                    //Build a parent alias.
                    filterServices.remove(0);
                    if (CollectionUtils.isNotEmpty(filterServices)) {
                        for (Class<?> filterService : filterServices) {
                            registry.registerAlias(beanName, ServiceContextUtils.formatAlisa(filterService,
                                    value, applicationId));
                            classAlisa.forEach(alias0 -> registry.registerAlias(beanName,
                                    ServiceContextUtils.formatAlisa(filterService,
                                            alias0, applicationId)));
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

        private static String mainApplicationPackage;

        private static ConfigurableApplicationContext context;

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
            if (mainApplicationClass.isAnnotationPresent(EnableServiceCollection.class)
                    || mainApplicationClass.isAnnotationPresent(EnableServiceCollection2.class)
                    //Only the package path where the main class is located is restricted here.
                    || !ScanUtils.getTypesAnnotatedWith(EnableServiceCollection.class, mainApplicationPackage).isEmpty()
                    || !ScanUtils.getTypesAnnotatedWith(EnableServiceCollection2.class, mainApplicationPackage).isEmpty()) {
                enableCustomBeanNameGeneratorSet = true;
            }
        }

        /**
         * Return the package path where the SpringBoot main class is located.
         * @return the package path where the SpringBoot main class is located.
         */
        protected static String getMainApplicationPackage() {
            return mainApplicationPackage;
        }

        /**
         * Set the spring context object for initialization.
         * @param context0 the spring context object for initialization.
         */
        protected static void setConfigurableApplicationContext(ConfigurableApplicationContext context0) {
            context = context0;
        }

        /**
         * Return the spring context object for initialization.
         * @return the spring context object for initialization.
         */
        protected static ConfigurableApplicationContext getContext() {
            return context;
        }

        /**
         * Clear intermediate variables.
         */
        protected static void clearMainApplicationPackageCache() {
            mainApplicationPackage = null;
            resetBeanNameGeneratorSwitch();
        }

        /**
         * Clear the cache of the spring context.
         */
        protected static void clearContextCache() {
            context = null;
            resetBeanNameGeneratorSwitch();
        }

        /**
         * Clear the setting cache of the bean name registration machine.
         */
        protected static void resetBeanNameGeneratorSwitch() {
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
        S service = ServiceContextUtils.getService(serviceName, requiredType, context.getId(),
                encodeServiceName -> contextMap.getValueOnClass(encodeServiceName, requiredType));
        if (service == null) {
            //Throw an exception that cannot be found.
            throw new NoSuchServiceException(serviceName);
        }
        return service;
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

    /**
     * Return the package path where the SpringBoot main class is located.
     * @return the package path where the SpringBoot main class is located.
     */
    public String getApplicationPackage() {
        return ServiceContextRunListener.getMainApplicationPackage();
    }

    /**
     * Return the bean registration machine for Spring.
     * @return the bean registration machine for Spring.
     */
    public BeanDefinitionRegistry getBeanDefinitionRegistry() {
        return (BeanDefinitionRegistry) context;
    }

    /**
     * Return service mapping cache.
     * @return service mapping cache.
     */
    public ClassMap<String, Object> getContextMap() {
        return contextMap;
    }

    /**
     * Return the spring context object for initialization.
     * @return the spring context object for initialization.
     */
    public ConfigurableApplicationContext getConfigurableApplicationContext() {
        return ServiceContextRunListener.getContext();
    }

    /**
     * Clear intermediate variables.
     */
    protected static void clearMainApplicationPackageCache() {
        ServiceContextRunListener.clearMainApplicationPackageCache();
    }

    /**
     * Clear the cache of the spring context.
     */
    protected static void clearContextCache() {
        ServiceContextRunListener.clearContextCache();
    }

    @Override
    public void close() {
        contextMap.clear();
    }
}
