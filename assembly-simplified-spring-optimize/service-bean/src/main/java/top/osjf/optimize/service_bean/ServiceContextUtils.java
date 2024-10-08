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

package top.osjf.optimize.service_bean;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;
import top.osjf.optimize.service_bean.annotation.ServiceCollection;
import top.osjf.optimize.service_bean.context.ServicePair;

import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Help class on context collection tools.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
public final class ServiceContextUtils {

    /*** Name separator.*/
    private static final String ARROW = " -> ";

    /*** The name collected in the record class mode.*/
    public static final String RECORD_BEAN_NAME = "RECORD_SERVICE_CONTENT_BEAN";

    /*** The bean name of the bean monitor when setting the service context.*/
    public static final String SC_AWARE_BPP_NANE = "CONTENT_AWARE_BPP_BEAN";

    /*** The name identification suffix of this service management proxy class.*/
    private static final String SERVICE_COLLECTION_BEAN_SIGNS = "@a.s.proxy";

    /*** The name identification suffix of this service management proxy class.*/
    public static final String SERVICE_SCOPE = "service";

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
     *
     * @param serviceName   The service name passed in.
     * @param requiredType  The type that needs to be converted.
     * @param applicationId Application ID.
     * @param getFun        The equation for obtaining services.
     * @param <S>           Service type.
     * @return The {@link Pair} with service encode name and service. //1.0.2
     * @see #formatId(Class, String, String)
     * @see #formatAlisa(Class, String, String)
     */
    //Obtain service objects based on the provided elements.
    public static <S> ServicePair<S> getService(String serviceName, Class<S> requiredType,
                                                String applicationId, Function<String, S> getFun) {

        if (StringUtils.isBlank(serviceName) ||
                requiredType == null ||
                getFun == null ||
                StringUtils.isBlank(applicationId)) {
            return ServicePair.empty();
        }
        String encodeServiceName = null;
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
                if (service != null) encodeServiceName = alisa;
            } else {
                encodeServiceName = id;
            }
        } else {
            encodeServiceName = serviceName;
        }
        return ServicePair.of(encodeServiceName, service);
    }

    /**
     * Retrieve the class object based on its name.
     *
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
     *
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
     *
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
     *
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
     * Returns a collection of names for candidate retrieval services.
     *
     * @param serviceName   The service name passed in.
     * @param requiredType  The type that needs to be converted.
     * @param applicationId Application ID.
     * @return candidate service names.
     */
    public static List<String> getCandidateServiceNames(String serviceName, Class<?> requiredType, String applicationId) {
        return Stream.of(
                formatId(requiredType, serviceName, applicationId),
                formatAlisa(requiredType, serviceName, applicationId),
                //As the final option
                serviceName
        ).collect(Collectors.toList());
    }

    /**
     * Conditionally encode the ID of the bean.
     *
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
     *
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
     *
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
        return DigestUtils.md5DigestAsHex(
                //Project name.
                (applicationId
                        + ARROW
                        //Possible service collection prefixes.
                        + getServiceCollectionPrefix(parent)
                        + ARROW
                        //Avoid using the same name and obtain the package path.
                        + suffix).getBytes(StandardCharsets.UTF_8)) +
                (StringUtils.isNotBlank(idSign) ? idSign : "");
    }

    /**
     * Encode the name according to the conditions.
     *
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
     *
     * @param parent Known class object parent.
     * @return Prefix for bean encode.
     */
    //Obtain the service name prefix.
    private static String getServiceCollectionPrefix(Class<?> parent) {
        if (parent == null) {
            return "";
        }
        ServiceCollection collection = parent.getAnnotation(ServiceCollection.class);
        Assert.notNull(collection,
                "Not a service collection class");
        String value = collection.prefix();
        if (StringUtils.isBlank(value)) {
            value = parent.getName();
        }
        return value;
    }
}
