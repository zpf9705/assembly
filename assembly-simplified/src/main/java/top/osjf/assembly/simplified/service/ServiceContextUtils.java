package top.osjf.assembly.simplified.service;

import top.osjf.assembly.simplified.service.annotation.ServiceCollection;
import top.osjf.assembly.simplified.service.annotation.Type;
import top.osjf.assembly.simplified.service.context.ClassesServiceContext;
import top.osjf.assembly.simplified.service.context.ServiceContext;
import top.osjf.assembly.util.encode.DigestUtils;
import top.osjf.assembly.util.lang.*;

import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Help class on context collection tools.
 *
 * @author zpf
 * @since 2.0.6
 */
public final class ServiceContextUtils {

    /*** Name separator.*/
    private static final String ARROW = " -> ";

    /*** Select the name of the service context object based on the type enumeration.*/
    public static final String SERVICE_CONTEXT_NAME = "TYPE-CHOOSE-SERVICE_CONTEXT";

    /*** The name collected in the default class mode.*/
    public static final String CONFIG_BEAN_NAME = "CLASSES_SERVICE_CONTENT_BEAN";

    /*** The name identification suffix of this service management proxy class.*/
    private static final String SERVICE_COLLECTION_BEAN_SIGNS = "@a.s.proxy";

    /*** Instantiation is still not supported.*/
    private ServiceContextUtils() {
    }

    /**
     * Retrieve cache service based on service name and known elements.
     * <p>Provide services to obtain equations, with sub equations provided
     * externally.
     * Encode the ID name based on the application ID.
     * Priority should be given to using service name encoding, followed
     * by using alias name encoding.
     * @param serviceName   The service name passed in.
     * @param requiredType  The type that needs to be converted.
     * @param applicationId Application ID.
     * @param getFun        The equation for obtaining services.
     * @param <S>           Service type.
     * @return The converted service type may be {@literal null}.
     * @see #formatId(Class, String, String)
     * @see #formatAlisa(Class, String, String)
     */
    //Obtain service objects based on the provided elements.
    public static <S> S getService(String serviceName, Class<S> requiredType,
                                   String applicationId, Function<String, S> getFun) {
        if (StringUtils.isBlank(serviceName) || requiredType == null || getFun == null ||
                StringUtils.isBlank(applicationId)) {
            return null;
        }
        //Priority is given to direct queries.
        S service = getFun.apply(serviceName);
        if (service == null) {
            //Next, encode the ID.
            String id = formatId(requiredType, serviceName, applicationId);
            service = getFun.apply(id);
            if (service == null) {
                //Finally, encode the alias.
                String alisa = formatAlisa(requiredType, serviceName, applicationId);
                service = getFun.apply(alisa);
            }
        }
        return service;
    }

    /**
     * Analyze whether the current type is {@link Type} type.
     * Due to the external {@link Type#SIMPLE} type, the object
     * needs to be created in a timely manner.
     * When the above viewpoint is met, create a {@link ClassesServiceContext}
     * and return it as a singleton object.
     * @param type    The type of service selected.
     * @param context Known service context.
     * @return The service context that has been analyzed.
     */
    //If the type is empty or class, select the class mode, and for the rest,
    // select the already initialized simple mode
    public static ServiceContext newOrDefault(Type type, ServiceContext context) {
        if (type == null || Objects.equals(type, Type.CLASSES)) return new ClassesServiceContext();
        return context;
    }

    /**
     * Retrieve the class object based on its name.
     * @param className Class#getName.
     * @return Class Object.
     */
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

    /**
     * Retrieve a collection of abstract class and interface class
     * objects based on known class objects.
     * Contains abstract classes or interfaces that carry
     * {@link ServiceCollection} at all levels.
     * @param clazz Known class object.
     * @return Get the result list.
     */
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

    /**
     * @return Predicate for {@link ServiceCollection}.
     */
    //Interface or abstract class, and annotate the service to collect annotations.
    public static Predicate<Class<?>> isServiceCollectionTarget() {
        return ServiceContextUtils::isServiceCollectionTarget;
    }

    /**
     * <strong>Meet the conditions for service collection.</strong>
     * <ul>
     *     <li>Interface or abstract class.</li>
     *     <li>Carry {@link ServiceCollection}.</li>
     * </ul>
     * @param clazz Known class object.
     * @return if {@code true} is collection service,otherwise.
     */
    //Interface or abstract class, and annotate the service to collect annotations.
    public static boolean isServiceCollectionTarget(Class<?> clazz) {
        return (clazz.isInterface()
                ||
                Modifier.isAbstract(clazz.getModifiers()))
                &&
                clazz.getAnnotation(ServiceCollection.class) != null;
    }

    /**
     * Determine whether it is a bean collected by the
     * service by checking whether the ID suffix of the
     * bean ends with {@link #SERVICE_COLLECTION_BEAN_SIGNS}.
     * @param beanName Bean of ID.
     * @return After encode,this ID Of their beans.
     */
    //Whether to collect services for the target.
    public static boolean isCollectionService(String beanName) {
        if (StringUtils.isBlank(beanName)) {
            return false;
        }
        return beanName.endsWith(SERVICE_COLLECTION_BEAN_SIGNS);
    }

    /**
     * Conditionally encode the ID of the bean.
     * @param parent        Known class object parent.
     * @param suffix        Known Service name.
     * @param applicationId Application id.
     * @return Encode result string.
     */
    //A custom generation scheme for bean name.
    public static String formatId(Class<?> parent, String suffix, String applicationId) {
        return encodeName(parent, suffix, applicationId, SERVICE_COLLECTION_BEAN_SIGNS);
    }

    /**
     * Conditionally encode the alisa name of the bean.
     * @param parent        Known class object parent.
     * @param suffix        Known Service name.
     * @param applicationId Application id.
     * @return Encode result string.
     */
    //A custom generation scheme for bean alisa name.
    public static String formatAlisa(Class<?> parent, String suffix, String applicationId) {
        return encodeName(parent, suffix, applicationId, null);
    }

    /**
     * Encode the name according to the conditions.
     * @param parent        Known class object parent.
     * @param suffix        Known Service name.
     * @param applicationId Application id.
     * @param idSign        Id sign.
     * @return Encode result string.
     */
    //Encrypted bean name definition.
    private static String encodeName(Class<?> parent, String suffix, String applicationId, String idSign) {
        if (parent == null || StringUtils.isBlank(suffix) || StringUtils.isBlank(applicationId)) {
            return null;
        }
        return DigestUtils.md5Hex(
                //Project name.
                applicationId
                        + ARROW
                        //Possible service collection prefixes.
                        + getServiceCollectionPrefix(parent)
                        + ARROW
                        //Avoid using the same name and obtain the package path.
                        + suffix) +
                (StringUtils.isNotBlank(idSign) ? idSign : "");
    }

    /**
     * Encode the name according to the conditions.
     * @param clazz           Known class object.
     * @param ignoredFullName Whether to ignore the full name of the class.
     * @return Alias of clazz in analyze result.
     */
    //Analyze the remaining class names based on their applicability.
    public static List<String> analyzeClassAlias(Class<?> clazz, boolean ignoredFullName) {
        if (clazz == null) {
            return Collections.emptyList();
        }
        List<String> alisa = new ArrayList<>();
        String simpleName = clazz.getSimpleName();
        alisa.add(simpleName);
        alisa.add(simpleName.substring(0, 1).toLowerCase() + simpleName.substring(1));
        if (!ignoredFullName) {
            alisa.add(clazz.getName());
        }
        return alisa;
    }

    /**
     * Obtain the full path of the empty class based
     * on the identification of the annotation collected
     * by the known class retrieval service.
     * @param parent Known class object parent.
     * @return Prefix for bean encode.
     */
    //Obtain the service name prefix.
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
