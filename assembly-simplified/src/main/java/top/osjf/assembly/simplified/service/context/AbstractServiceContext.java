package top.osjf.assembly.simplified.service.context;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.boot.web.reactive.context.AnnotationConfigReactiveWebServerApplicationContext;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import top.osjf.assembly.simplified.service.annotation.EnableServiceCollection;
import top.osjf.assembly.simplified.service.annotation.EnableServiceCollection2;
import top.osjf.assembly.util.annotation.NotNull;
import top.osjf.assembly.util.data.ClassMap;
import top.osjf.assembly.util.data.ThreadSafeClassMap;
import top.osjf.assembly.util.io.ScanUtils;
import top.osjf.assembly.util.lang.CollectionUtils;
import top.osjf.assembly.util.lang.StringUtils;
import top.osjf.assembly.util.logger.Console;

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
 * {@link ServiceContextUtils#customGenerationOfBeanName(Class, Class, String)} to obtain
 * the real bean name, and then obtain the real service class from the cached {@link #contextMap}.
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
public abstract class AbstractServiceContext implements ServiceContext, ApplicationContextAware,
        ApplicationListener<ContextRefreshedEvent> {

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
     * policy can be viewed in {@link ServiceContextUtils#customGenerationOfBeanName(Class, Class, String)}.
     */
    public static class ServiceContextBeanNameGenerator extends AnnotationBeanNameGenerator implements BeanNameGenerator {

        private final String applicationName;

        private static BeanNameGenerator INSTANCE;

        private ServiceContextBeanNameGenerator(String applicationName) {
            this.applicationName = applicationName;
        }

        public synchronized static BeanNameGenerator getInstance(String applicationName) {
            if (INSTANCE != null) {
                return INSTANCE;
            }
            INSTANCE = new ServiceContextBeanNameGenerator(applicationName);
            return INSTANCE;
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
                    if (filterServices.size() > 1) {
                        throw new IllegalArgumentException("Only one parent class and interface of service collection " +
                                "is allowed to carry service collection annotations, but the number of related parent " +
                                "classes and interfaces worn by " + beanClassName + " is greater than 1.");
                    } else {
                        beanName = ServiceContextUtils.customGenerationOfBeanName(filterServices.get(0),
                                clazz,
                                applicationName);
                        if (StringUtils.isBlank(beanName)) {
                            beanName = super.generateBeanName(definition, registry);
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

        private static boolean enableServiceCollection;

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
            mainApplicationPackage = application.getMainApplicationClass().getPackage().getName();
            enableServiceCollection = ScanUtils.getTypesAnnotatedWith(
                    EnableServiceCollection.class,
                    mainApplicationPackage
            ).size()
                    +
                    ScanUtils.getTypesAnnotatedWith(
                            EnableServiceCollection2.class,
                            mainApplicationPackage).size() >= 1;
        }

        protected static String getMainApplicationPackage() {
            return mainApplicationPackage;
        }

        protected static ConfigurableApplicationContext getContext() {
            return context;
        }

        protected static void clear() {
            mainApplicationPackage = null;
            context = null;
        }

        @Override
        public void contextPrepared(ConfigurableApplicationContext context) {
            ServiceContextRunListener.context = context;
            if (!enableServiceCollection){
                return;
            }
            BeanNameGenerator generator = ServiceContextBeanNameGenerator.getInstance(context.getId());
            if (generator == null) {
                return;
            }
            if (context instanceof AnnotationConfigServletWebServerApplicationContext) {
                ((AnnotationConfigServletWebServerApplicationContext) context)
                        .setBeanNameGenerator(generator);
            } else if (context instanceof AnnotationConfigReactiveWebServerApplicationContext) {
                ((AnnotationConfigReactiveWebServerApplicationContext) context)
                        .setBeanNameGenerator(generator);
            } else if (context instanceof AnnotationConfigApplicationContext) {
                ((AnnotationConfigApplicationContext) context)
                        .setBeanNameGenerator(generator);
            }
        }
    }

    @Override
    public void setApplicationContext(@NotNull ApplicationContext context) throws BeansException {
        this.context = context;
    }

    @Override
    public <S> S getService(String serviceName, Class<S> requiredType) throws NoSuchServiceException {
        final String serviceName0 = serviceName;
        try {
            serviceName = ServiceContextUtils.getBeanName(requiredType, serviceName, context.getApplicationName());
        } catch (ClassNotFoundException e) {
            //Throw an exception that cannot be found.
            throw new NoSuchServiceException(e.getMessage());
        }
        S service = contextMap.getValueOnClass(serviceName, requiredType);
        if (service == null) {
            try {
                //If the collected beans are not found,
                //Using spring's context lookup may be based on aliases.
                //However, when using aliases, please be aware of potential bean name duplication errors.
                service = context.getBean(serviceName0, requiredType);
            } catch (BeansException e) {
                //Throw an exception that cannot be found.
                throw new NoSuchServiceException("No service named " + serviceName + " was found " +
                        "from the service context");
            }
        }
        return service;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (getApplicationContext() == event.getApplicationContext()) {
            Console.info("{} ====> Service collection finish", this.getClass().getName());
        } else {
            throw new IllegalStateException("Startup application no same");
        }
    }

    @Override
    public void reloadWithScanPackages(String... packages) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ApplicationContext getApplicationContext() {
        return context;
    }

    protected ClassMap<String, Object> getContextMap() {
        return contextMap;
    }

    @Override
    public void close() {
        contextMap.clear();
    }
}