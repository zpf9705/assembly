package top.osjf.assembly.simplified.service;

import top.osjf.assembly.simplified.service.annotation.ServiceCollection;
import top.osjf.assembly.simplified.service.annotation.Type;
import top.osjf.assembly.simplified.service.context.AbstractServiceContext;
import top.osjf.assembly.simplified.service.context.ClassesServiceContext;
import top.osjf.assembly.simplified.service.context.ServiceContext;
import top.osjf.assembly.util.lang.*;

import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Help class on context collection tools.
 *
 * @author zpf
 * @since 2.0.6
 */
public final class ServiceContextUtils {

    /** Name separator.*/
    private static final String ARROW = " -> ";

    /** Select the name of the service context object based on the type enumeration.*/
    public static final String SERVICE_CONTEXT_NAME = "TYPE-CHOOSE-SERVICE_CONTEXT";

    /** The name collected in the default class mode.*/
    public static final String CONFIG_BEAN_NAME = "CLASSES_SERVICE_CONTENT_BEAN";

    private ServiceContextUtils() {

    }

    //If the type is empty or class, select the class mode, and for the rest,
    // select the already initialized simple mode
    public static ServiceContext newOrDefault(Type type, ServiceContext context) {
        if (type == null || Objects.equals(type, Type.CLASSES)) return new ClassesServiceContext();
        return context;
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
        //Ensure that your input service name is the class in
        // the path where the project is located.
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
