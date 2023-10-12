package top.osjf.assembly.simplified.service.context;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import top.osjf.assembly.simplified.service.annotation.ServiceCollection;
import top.osjf.assembly.util.annotation.CanNull;
import top.osjf.assembly.util.annotation.NotNull;
import top.osjf.assembly.util.lang.ArrayUtils;
import top.osjf.assembly.util.lang.CollectionUtils;
import top.osjf.assembly.util.lang.ConvertUtils;
import top.osjf.assembly.util.logger.Console;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

/**
 * Support implementation class for service context {@link ServiceContext}.
 *
 * <p>Compared to {@link ClassesServiceContext}, the loading process is simpler and does
 * not require a fixed package path.
 *
 * <p>The service loading process is as follows:
 * <p>The creation process of monitoring container beans depends on {@link BeanPostProcessor}.
 * <p>Firstly, check whether a single bean meets the service collection requirements.
 * <p>If it does not meet the requirements, then search for the service collection loading
 * of its interface class and parent class, and finally collect it into a thread safe
 * collection container {@link #contextMap}.
 *
 * <p>The basic requirement for service collection is to wear an annotated
 * {@link ServiceCollection} interface or abstract class, and satisfy such a
 * bean to be collected by the service.
 * <h3>Service avoids the combination of duplicate beans:</h3>
 * <pre>
 * ServiceNameProvider(example) {@link #POINT} SimpleServiceContext(example)
 * </pre>
 *
 * <p>The collection of this service configuration can only rely on
 * <pre>&#064;EnableServiceCollection2@type=SIMPLE.</pre>
 *
 * @author zpf
 * @since 2.0.6
 */
public class SimpleServiceContext extends AbstractServiceContext implements BeanPostProcessor,
        ApplicationListener<ContextRefreshedEvent> {

    private final ConcurrentMap<String, Object> contextMap = new ConcurrentHashMap<>(16);

    public static final String POINT = " ===> ";

    @CanNull
    @Override
    public Object postProcessAfterInitialization(@NotNull Object bean, @NotNull String beanName) throws BeansException {
        preServiceCollection(bean, beanName);
        return bean;
    }

    @Override
    public <S> S getService(String serviceName, Class<S> requiredType) throws NoSuchServiceException {
        //alisa prepare
        final String serviceName0 = serviceName;
        //The bean name points to its class abbreviation.
        //ServiceName ===> Test
        serviceName = serviceName + POINT + requiredType.getSimpleName();
        S service;
        Object obj = contextMap.get(serviceName);
        if (obj == null) {
            service = super.getService(serviceName0, requiredType);
        } else {
            service = ConvertUtils.convert(requiredType, obj);
        }
        return service;
    }

    @Override
    public void onApplicationEvent(@NotNull ContextRefreshedEvent event) {
        if (getApplicationContext() == event.getApplicationContext()) {
            Console.info("top.osjf.assembly.simplified.service.context.SimpleServiceContext ==> " +
                    "Service collection finish");
        }
    }

    private void preServiceCollection(Object bean, String beanName) {
        // loaded direct return
        if (contextMap.containsKey(beanName)) {
            return;
        }
        List<Class<?>> classes = new ArrayList<>();
        Class<?> dela = bean.getClass();
        //If this bean directly contains service collection annotations and satisfies
        // the requirements of an interface or abstract class, then service collection
        // is directly carried out.
        if (dela.getAnnotation(ServiceCollection.class) != null
                && (dela.isInterface()
                || Modifier.isAbstract(dela.getModifiers()))) {
            classes.add(dela);
        } else {
            //Collect the interface collection and inheritance classes of this bean,
            // and search for service collection identification annotations.
            Class<?>[] interfaces = dela.getInterfaces();
            Class<?> superclass = dela.getSuperclass();
            if (//The interface needs to meet the requirements of wearing annotations.
                    (ArrayUtils.isEmpty(interfaces)
                            || Arrays.stream(interfaces).allMatch(c -> c.getAnnotation(ServiceCollection.class) == null))
                            &&
                            //Inheritance classes need to be abstract and not the initial Object
                            // class, including service collection annotations.
                            ("java.lang.Object".equals(superclass.getName())
                                    || !Modifier.isAbstract(superclass.getModifiers())
                                    || superclass.getAnnotation(ServiceCollection.class) == null)) {
                return;
            }
            if (ArrayUtils.isNotEmpty(interfaces)) {
                List<Class<?>> classes0 = Arrays.stream(interfaces)
                        .filter(c -> c.getAnnotation(ServiceCollection.class) != null)
                        .collect(Collectors.toList());
                classes.addAll(classes0);
            }
            if (superclass.getAnnotation(ServiceCollection.class) != null) {
                classes.add(superclass);
            }
        }
        if (CollectionUtils.isEmpty(classes)) return;
        Console.info("Service collection : {}", classes);
        for (Class<?> clazz : classes) {
            Map<String, ?> beansOfType;
            try {
                //The collection of final services still relies on contextual objects.
                beansOfType = getApplicationContext().getBeansOfType(clazz);
            } catch (BeansException e) {
                return;
            }
            beansOfType.forEach(contextMap::putIfAbsent);
        }
    }
}
