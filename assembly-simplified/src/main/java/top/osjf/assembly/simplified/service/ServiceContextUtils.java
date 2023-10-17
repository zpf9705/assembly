package top.osjf.assembly.simplified.service;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import top.osjf.assembly.simplified.service.annotation.ServiceCollection;
import top.osjf.assembly.simplified.service.annotation.Type;
import top.osjf.assembly.simplified.service.context.ClassesServiceContext;
import top.osjf.assembly.simplified.service.context.ServiceContext;
import top.osjf.assembly.util.io.ScanUtils;
import top.osjf.assembly.util.lang.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Help class on context collection tools.
 *
 * @author zpf
 * @since 2.0.6
 */
public final class ServiceContextUtils {

    /**
     * Name separator.
     */
    private static final String ARROW = " -> ";

    /**
     * Select the name of the service context object based on the type enumeration.
     */
    public static final String SERVICE_CONTEXT_NAME = "TYPE-CHOOSE-SERVICE_CONTEXT";

    /**
     * The name collected in the default class mode.
     */
    public static final String CONFIG_BEAN_NAME = "CLASSES_SERVICE_CONTENT_BEAN";


    private ServiceContextUtils() {

    }

    public static <S> S getService(String serviceName, Class<S> requiredType,
                                   String applicationId, String defaultApplicationPackage,
                                   Function<String, S> getFun) {
        if (StringUtils.isBlank(serviceName) || requiredType == null || getFun == null ||
                StringUtils.isBlank(applicationId) || StringUtils.isBlank(defaultApplicationPackage)) {
            return null;
        }

        //Try encoding directly to obtain.
        String encodeName = encodeNameFromElements(requiredType, serviceName, applicationId);

        S service = getFun.apply(encodeName);

        if (service != null) return service;

        //Parent class package path.
        NamedContext context = getBeanNameUseParentOrMainPackageName(requiredType, serviceName, applicationId,
                defaultApplicationPackage);

        if (context.hasValue()) {

            for (String name : context.getValue()) {
                service = getFun.apply(name);
                if (service != null) {
                    return service;
                }
            }
        }

        //Full path of oneself.
        Set<Class<S>> scan = ScanUtils.scan(defaultApplicationPackage, clazz -> Objects.equals(clazz.getSimpleName(),
                serviceName.substring(0, 1).toUpperCase() + serviceName.substring(1)));

        if (CollectionUtils.isNotEmpty(scan)) {

            Class<S> clazz = CollectionUtils.get(scan, 0);

            String encodeName0 = encodeNameFromElements(requiredType, clazz.getName(), applicationId);

            service = getFun.apply(encodeName0);

            if (service == null) {

                Annotation[] annotations = clazz.getAnnotations();

                if (ArrayUtils.isNotEmpty(annotations)) {

                    Annotation annotation = Arrays.stream(annotations).filter(an -> {
                        Class<? extends Annotation> annotationType = an.annotationType();
                        return Component.class == annotationType
                                || Repository.class == annotationType
                                || Service.class == annotationType
                                || Controller.class == annotationType;
                    }).findFirst().orElse(null);

                    if (annotation != null) {

                        Object fieldValue = ReflectUtils.getFieldValue(annotation, "value");

                        encodeName0 = encodeNameFromElements(requiredType, fieldValue.toString(), applicationId);

                        service = getFun.apply(encodeName0);
                    }

                    return service;
                }
            }
        }
        return null;
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
        LinkedList<Class<?>> sortServices = new LinkedList<>();

        //Interface directly implemented by class
        Class<?>[] ourSee = clazz.getInterfaces();

        //If the interface directly implemented by the class does not exist, query the inherited class.
        if (ArrayUtils.isEmpty(ourSee)) {
            Class<?> superclass = clazz.getSuperclass();
            //It cannot be an Object .
            if ("java.lang.Object".equals(superclass.getName())) {
                //No interface, no parent class, directly returning null.
                return null;
            } else {
                //must comply with annotation requirements.
                if (isServiceCollectionTarget(superclass)) {
                    sortServices.add(superclass);
                }
            }
        } else {
            Class<?> clazz0 = Arrays.stream(ourSee).filter(isServiceCollectionTarget())
                    .findFirst().orElse(null);
            //For interfaces, use any one of the current IDs, and take the first one here.
            if (clazz0 != null) {
                sortServices.add(clazz0);
            }
        }

        //get all super classes
        List<Class<?>> supers = ClassUtils.getAllSuperclasses(clazz);
        supers.remove(Object.class);
        supers = supers.stream().filter(isServiceCollectionTarget()).collect(Collectors.toList());

        //get all interfaces
        List<Class<?>> interfaces = ClassUtils.getAllInterfaces(clazz);
        interfaces = interfaces.stream().filter(isServiceCollectionTarget()).collect(Collectors.toList());

        //If the first element is not yet present at this point,
        // take the first one from all filtered parent and interface classes.
        if (sortServices.isEmpty()) {
            if (!interfaces.isEmpty()) {
                sortServices.add(interfaces.get(0));
            } else {
                if (!supers.isEmpty()) {
                    sortServices.add(supers.get(0));
                }
            }
        }

        //If it is still empty, return null directly.
        if (CollectionUtils.isEmpty(sortServices)) return null;

        interfaces.addAll(supers);

        interfaces.removeAll(sortServices);

        //After removing the duplicate from the first position, add it directly.
        sortServices.addAll(interfaces);

        return sortServices;
    }

    //Interface or abstract class, and annotate the service to collect annotations.
    public static Predicate<Class<?>> isServiceCollectionTarget() {
        return ServiceContextUtils::isServiceCollectionTarget;
    }

    //Interface or abstract class, and annotate the service to collect annotations.
    public static boolean isServiceCollectionTarget(Class<?> clazz) {
        return (clazz.isInterface()
                ||
                Modifier.isAbstract(clazz.getModifiers()))
                &&
                clazz.getAnnotation(ServiceCollection.class) != null;
    }

    public static boolean isCollectionService(String beanName, String applicationId) {
        if (StringUtils.isBlank(applicationId) || StringUtils.isBlank(beanName)) {
            return false;
        }
        return beanName.startsWith(applicationId + ARROW);
    }

    //A custom generation scheme for bean names.
    public static String encodeNameFromElements(Class<?> parent, String suffix, String applicationId) {
        if (parent == null || StringUtils.isBlank(suffix) || StringUtils.isBlank(applicationId)) {
            return null;
        }
        //id
        return
                //Project name.
                applicationId
                        + ARROW +
                        DigestUtils.md5Hex(
                                //Possible service collection prefixes.
                                getServiceCollectionPrefix(parent)
                                        + ARROW
                                        //Avoid using the same name and obtain the package path.
                                        + suffix);
    }

    public interface NamedContext {

        boolean hasValue();

        List<String> getValue();
    }

    //Obtain bean services based on the abbreviation.
    public static NamedContext getBeanNameUseParentOrMainPackageName(Class<?> parent, String serviceName,
                                                                     String applicationId,
                                                                     String defaultApplicationPackage) {
        String parentPackageMontageName = null;
        String defaultApplicationPackageMontageName = null;
        if (parent != null && StringUtils.isNotBlank(serviceName)
                && StringUtils.isNotBlank(defaultApplicationPackage)) {
            if (!serviceName.startsWith(parent.getPackage().getName())
                    || !serviceName.contains(parent.getPackage().getName())) {
                if (!serviceName.startsWith(".")) {
                    serviceName = "." + serviceName;
                }
                //It needs to be in the same package as the parent class/interface.
                parentPackageMontageName = parent.getPackage().getName() + serviceName;
            }
            if (!serviceName.startsWith(defaultApplicationPackage)
                    || !serviceName.contains(defaultApplicationPackage)) {
                if (!serviceName.startsWith(".")) {
                    serviceName = "." + serviceName;
                }
                defaultApplicationPackageMontageName = defaultApplicationPackage + serviceName;
            }
        }
        String parentPackageMontageNameEc = encodeNameFromElements(parent, parentPackageMontageName, applicationId);
        String defaultApplicationPackageMontageNameEc =
                encodeNameFromElements(parent, defaultApplicationPackageMontageName, applicationId);

        List<String> values = Stream.of(parentPackageMontageNameEc, defaultApplicationPackageMontageNameEc)
                .collect(Collectors.toList());
        return new NamedContext() {

            @Override
            public boolean hasValue() {
                return CollectionUtils.isNotEmpty(values);
            }

            @Override
            public List<String> getValue() {
                return values;
            }
        };
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
