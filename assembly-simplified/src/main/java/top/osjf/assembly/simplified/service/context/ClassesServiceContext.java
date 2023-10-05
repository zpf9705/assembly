package top.osjf.assembly.simplified.service.context;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeansException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.context.ConfigurableApplicationContext;
import top.osjf.assembly.simplified.service.annotation.ServiceCollection;
import top.osjf.assembly.util.ScanUtils;
import top.osjf.assembly.util.data.ClassMap;
import top.osjf.assembly.util.data.ThreadSafeClassMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Support implementation class for service context {@link ServiceContext}.
 *
 * <p>The implementation process is as follows.
 *
 * <p>Due to scanning the package path where the startup class is located,
 * {@link SpringApplicationRunListener} listening is implemented.
 * In the first step of obtaining the package path of the startup class,
 * it should be noted that this is not an instantiation of the injection container,
 * so it is not {@link top.osjf.assembly.simplified.service.ServiceContextConfiguration}
 * configuring the injected object{@link ClassesServiceContext}.
 *
 * <p>After the first step of obtaining the startup class package, we manually configure
 * and inject a {@link ClassesServiceContext} as the preparation object.
 *
 * <p>When {@link SpringApplicationRunListener} enters the start phase, we call
 * back the {@link #started(ConfigurableApplicationContext)} method.
 *
 * <p>The object calling the {@link #started(ConfigurableApplicationContext)} method here
 * is created through Spring's SPI mechanism, not a container singleton.
 *
 * <p>At this point, we collect the annotated class of {@link ServiceCollection},
 * with the package as the main class package path.
 *
 * <p>By using the spring context method {@link org.springframework.context.ApplicationContext#getBeansOfType(Class)},
 * we can query all container subclasses and place them in the object we just injected into the container.
 *
 * <p>At this point, the service collection is complete.
 *
 * <p>It should be noted that the objects created by Spring's spi mechanism are not container objects.
 * These objects are configured {@link SpringApplicationRunListener} in [/META INF/spring. factories],
 * and for extension notifications, it is important to pay attention.
 *
 * @author zpf
 * @since 2.0.4
 */
public class ClassesServiceContext implements ServiceContext, SpringApplicationRunListener {

    private final ClassMap<String, Object> contextMap = new ThreadSafeClassMap<>(4);

    public static final String CLASSES_SERVICE_CONTENT_BEAN = "CLASSES_SERVICE_CONTENT_BEAN";

    private String applicationPath;

    /**
     * The empty structure here is mainly used for configuration purposes.
     */
    public ClassesServiceContext() {
    }

    /**
     * The necessary constructor for using {@link SpringApplicationRunListener}.
     *
     * @param application Spring's application startup class.
     * @param args        The startup parameters for the application startup class of Spring.
     */
    public ClassesServiceContext(SpringApplication application, String[] args) {
        this.applicationPath = application.getMainApplicationClass().getPackage().getName();
    }

    //This method is called by an object created by the SPI mechanism, not a container object.
    @Override
    @SuppressWarnings("unchecked")
    public void started(ConfigurableApplicationContext context) {
        Set<Class<Object>> serviceClasses = ScanUtils.getTypesAnnotatedWith(ServiceCollection.class,
                //Only query the interface or abstract class of the package path where the startup is located.
                applicationPath);
        if (CollectionUtils.isEmpty(serviceClasses)) {
            return;
        }
        List<Map<String, Object>> beanMaps = new ArrayList<>();
        for (Class<Object> serviceClass : serviceClasses) {
            Map<String, Object> beansMap;
            try {
                beansMap = context.getBeansOfType(serviceClass);
            } catch (BeansException ignored) {
                continue;
            }
            beanMaps.add(beansMap);
        }
        if (CollectionUtils.isEmpty(beanMaps)) {
            return;
        }
        //Find the previously prepared implementation class object and assign a value to the forwarding service map.
        ClassesServiceContext contextBean = context.getBean(CLASSES_SERVICE_CONTENT_BEAN, ClassesServiceContext.class);
        contextBean.contextMap.mergeMaps(beanMaps.toArray(new Map[]{}));
    }

    @Override
    public Object getService(String serviceName) throws NoSuchServiceException {
        Object service = contextMap.get(serviceName);
        if (service == null) {
            throw new NoSuchServiceException("No service named " + serviceName + " was found from the service context");
        }
        return service;
    }

    @Override
    public <S> S getService(String serviceName, Class<S> requiredType) throws NoSuchServiceException {
        S service = contextMap.getValueOnClass(serviceName, requiredType);
        if (service == null) {
            throw new NoSuchServiceException("No service named " + serviceName + " was found from the service context");
        }
        return service;
    }
}
