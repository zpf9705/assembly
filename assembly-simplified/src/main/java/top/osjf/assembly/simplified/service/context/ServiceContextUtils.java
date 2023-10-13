package top.osjf.assembly.simplified.service.context;

import top.osjf.assembly.simplified.service.annotation.ServiceCollection;
import top.osjf.assembly.util.lang.*;

import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author zpf
 * @since 2.0.6
 */
public final class ServiceContextUtils {

    private static final String ARROW = " -> ";

    private ServiceContextUtils() {
    }

    //Obtain the class object based on the class name.
    public static Class<?> getClass(String className) {
        if (className == null) {
            return null;
        }
        try {
            return ClassUtils.getClass(className, false);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    //Obtain and filter class objects that carry service collection annotations.
    public static List<Class<?>> getFilterServices(Class<?> clazz) {
        if (clazz == null) {
            return null;
        }
        List<Class<?>> allSuperclasses = ClassUtils.getAllSuperclasses(clazz);
        List<Class<?>> allInterfaces = ClassUtils.getAllInterfaces(clazz);
        allInterfaces.addAll(allSuperclasses);
        if (CollectionUtils.isEmpty(allInterfaces)) {
            return Collections.emptyList();
        }
        return allInterfaces.stream().filter(isServiceCollectionParent()).collect(Collectors.toList());
    }

    //Interface or abstract class, and annotate the service to collect annotations.
    public static Predicate<Class<?>> isServiceCollectionParent() {
        return ServiceContextUtils::isServiceCollectionParent;
    }

    //Interface or abstract class, and annotate the service to collect annotations.
    public static boolean isServiceCollectionParent(Class<?> clazz) {
        return (clazz.isInterface()
                ||
                Modifier.isAbstract(clazz.getModifiers()))
                &&
                clazz.getAnnotation(ServiceCollection.class) != null;
    }

    //A custom generation scheme for bean names.
    public static String customGenerationOfBeanName(Class<?> parent, Class<?> clazz, String applicationName) {
        if (parent == null || clazz == null || StringUtils.isBlank(applicationName)) {
            return null;
        }
        //id
        String id =
                //Project name.
                applicationName
                        + ARROW
                        //Possible service collection prefixes.
                        + getServiceCollectionPrefix(parent)
                        + ARROW
                        //Avoid using the same name and obtain the package path.
                        + clazz.getName();
        return DigestUtils.md5Hex(id);
    }

    //Obtain bean services based on the abbreviation.
    public static String getBeanName(Class<?> parent, String serviceName, String applicationName) throws
            ClassNotFoundException {
        Class<?> clazz = getClass(serviceName);
        if (clazz == null) {
            clazz = getClass(parent.getPackage().getName() +
                    "." + serviceName);
            if (clazz == null) {
                clazz = getClass(AbstractServiceContext.ServiceContextRunListener
                        .getMainApplicationPackage() + "." + serviceName);
                if (clazz == null) {
                    throw new ClassNotFoundException(
                            String.format(
                                    "No class corresponding to [%s] [%s] [%s] was found in the main project [%s]." +
                                            "It is recommended to provide a more detailed package path.",
                                    serviceName,
                                    parent.getPackage().getName() + "." + serviceName,
                                    AbstractServiceContext.ServiceContextRunListener.getMainApplicationPackage() +
                                            "." + serviceName,
                                    applicationName)
                    );
                }
            }
        }
        return customGenerationOfBeanName(parent, clazz, applicationName);
    }

    private static String getServiceCollectionPrefix(Class<?> parent) {
        if (parent == null) {
            return "";
        }
        ServiceCollection collection = parent.getAnnotation(ServiceCollection.class);
        Asserts.notNull(collection,
                "Not a service collection class");
        String value = collection.prefix();
        if (StringUtils.isBlank(value)) {
            value = parent.getName();
        }
        return value;
    }
}
