package top.osjf.assembly.simplified.sdk;

import org.apache.http.entity.ContentType;
import top.osjf.assembly.simplified.sdk.http.HttpResultResponse;
import top.osjf.assembly.simplified.sdk.process.*;
import top.osjf.assembly.simplified.sdk.proxy.*;
import top.osjf.assembly.util.lang.ArrayUtils;
import top.osjf.assembly.util.lang.MapUtils;
import top.osjf.assembly.util.lang.ReflectUtils;
import top.osjf.assembly.util.lang.StringUtils;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * About sdk helper tool class.
 *
 * @author zpf
 * @since 1.1.0
 */
@SuppressWarnings("rawtypes")
public abstract class SdkUtils {

    public static final String named = "Content-Type";

    public static void checkContentType(Map<String, String> headers) {
        if (MapUtils.isEmpty(headers)) {
            return;
        }
        //if no Content-Type
        if (!headers.containsKey(named)) {
            //default to JSON Content-Type
            headers.put(named, ContentType.APPLICATION_JSON.getMimeType());
        }
    }

    /***The left angle bracket included in the generic.*/
    static final String LEFT_ANGLE_BRACKET = "<";

    /***The right angle bracket included in the generic.*/
    static final String RIGHT_ANGLE_BRACKET = ">";

    /***The prefix name of the set method.*/
    static final String SET_METHOD_PREFIX = "set";

    static final Map<Class<?>, Object> rps_classes = new ConcurrentHashMap<>(16);

    /**
     * Create corresponding request parameters based on extension
     * type annotations and interfaces.
     *
     * @param method Proxy target method.
     * @param args   Request parameters.
     * @return The request class parameters created.
     * @see ResponseData
     * @see RequestParam
     */
    public static Request<?> invokeCreateRequest(Method method, Object[] args) {
        int length = ArrayUtils.isEmpty(args) ? 0 : args.length;
        Request<?> request;
        if (length == 1) {
            Object arg = args[0];
            if (arg instanceof Request) {
                //Consider first whether it is the actual request parameter.
                request = (Request<?>) arg;
            } else {
                //When a single parameter is not a request class, consider implementing
                // the parameter interface. If the parameter interface is not implemented,
                // an unknown exception will be thrown.
                if (arg instanceof RequestParameter) {
                    Class<? extends Request> requestType = ((RequestParameter) arg).getRequestType();
                    if (requestType == null) throw new UnknownRequestParameterException();
                    return invokeCreateRequestConstructorWhenFailedUseSet(requestType, arg);
                }
                throw new UnknownRequestParameterException();
            }
        } else {
            //Consider annotations for multiple parameters, and throw an
            // exception for unknown parameter types when there are no annotations present.
            RequestParam requestParam = method.getAnnotation(RequestParam.class);
            if (requestParam != null) {
                return invokeCreateRequestConstructorWhenFailedUseSet(requestParam.value(), args);
            }
            throw new UnknownRequestParameterException();
        }
        return request;
    }

    /**
     * When obtaining a response, convert to the desired type, which can be
     * specified in {@link ResponseData}.
     *
     * @param method   Proxy target method.
     * @param response The response type obtained.
     * @return The required return object.
     */
    public static Object getResponse(Method method, Response response) {
        if (Response.class.isAssignableFrom(method.getReturnType())) {
            //If the method returns a response class, then return it directly.
            return response;
        }
        if (response instanceof ResponseData) {
            ResponseData responseData = (ResponseData) response;
            if (responseData.inspectionResponseResult()) {
                //If the type value is specified, the data returned
                // when the request is successful is obtained.
                if (responseData.isSuccess()) {
                    return responseData.getData();
                } else {
                    //The default data returned when a customization request fails.
                    if (responseData instanceof InspectionResponseData) {
                        Object data = ((InspectionResponseData) responseData).failedSeatData();
                        if (data != null) {
                            //If it is empty, the requested data should prevail.
                            return data;
                        }
                    }
                }
            }
            //On the contrary, directly return the data result.
            return responseData.getData();
        }
        throw new UnknownResponseParameterException();
    }

    static Request<?> invokeCreateRequestConstructorWhenFailedUseSet(Class<? extends Request> requestType,
                                                                     Object... args) {
        Request<?> request;
        try {
            //First, directly instantiate the request class using the
            // construction method based on the parameters.
            request = ReflectUtils.newInstance(requestType, args);
        } catch (Throwable e) {
            //This step determines whether the parameter is empty to
            // determine whether the above is an empty construction instantiation.
            if (ArrayUtils.isEmpty(args)) throw new RequestCreateException(e);
            request = invokeCreateRequestUseSet(requestType, args);
        }
        return request;
    }

    static Request<?> invokeCreateRequestUseSet(Class<? extends Request> requestType,
                                                Object... args) {
        Request<?> request;
        try {
            //When parameter construction fails, first use an empty
            // construction to instantiate, and then find the set method.
            request = ReflectUtils.newInstance(requestType);
            Field[] fields =
                    ReflectUtils.getFields(requestType, field -> field.getAnnotation(RequestField.class) != null);
            if (ArrayUtils.isEmpty(fields)) {
                //When using methods for assignment, annotations must be identified.
                throw new IllegalArgumentException("When no construction method is provided, please " +
                        "use \"top.osjf.assembly.simplified.sdk.process.RequestField\" to mark the real" +
                        " name of the field.");
            }
            for (int i = 0; i < fields.length; i++) {
                Field field = fields[i];
                RequestField requestField = field.getAnnotation(RequestField.class);
                int order = getOrder(requestField, i, fields);
                Object arg = args[order];
                if (requestField.useReflect()) {
                    //Directly assign values in the case of sequential reflection assignment.
                    ReflectUtils.setFieldValue(request, field, arg);
                } else {
                    //The real name of the field cannot be empty at this time.
                    String fieldName = requestField.value();
                    if (StringUtils.isBlank(fieldName)) {
                        throw new IllegalArgumentException("When using the set method to set a value, " +
                                "the actual field name cannot be empty.");
                    }
                    //The set method performs an assignment.
                    ReflectUtils.invoke(request,
                            SET_METHOD_PREFIX + Character.toUpperCase(fieldName.charAt(0))
                                    + fieldName.substring(1), arg);
                }
            }
        } catch (Throwable e) {
            //There is no remedy at this step, simply throw an exception.
            throw new RequestCreateException(e);
        }
        return request;
    }

    static int getOrder(RequestField requestField, int i, Field[] fields) {
        int order = requestField.order();
        if (order == -1) {
            //If the default value is used for sorting,
            // it will be sorted in the default order of times.
            order = i;
        } else {
            if (order >= fields.length) {
                throw new ArrayIndexOutOfBoundsException("Current order " + order + "," +
                        " parameter length " + fields.length + ".");
            }
        }
        return order;
    }

    /**
     * Return the corresponding class generic carried by the request class.
     *
     * @param request Current request.
     * @return Object types,defaults to {@link HttpResultResponse}.
     * @see Class#getGenericInterfaces()
     * @see Class#getGenericSuperclass()
     * @since 2.1.3
     */
    public static Object getResponseRequiredType(Request<?> request) {

        if (request == null) {
            return null;
        }
        //Loop stop marker.
        boolean goWhile = true;

        //The final return type.
        Object type = null;

        //The class object of the current request class.
        final Class<?> inletClass = request.getClass();

        //Try reading the cache first.
        Object inlet = rps_classes.get(inletClass);
        if (inlet != null) {
            return inlet;
        }

        //Cache temporary type class objects.
        Map<Type, Class<?>> type_classes = new ConcurrentHashMap<>(16);

        //There is no cache to execute the fetch logic.
        Class<?> clazz = inletClass;
        while (goWhile) {
            //Collect the type of the class for visual objects.
            //Including interfaces and classes.
            ClassLoader classLoader = clazz.getClassLoader();
            List<Type> types = new ArrayList<>();
            Type genericSuperclass = clazz.getGenericSuperclass();
            if (genericSuperclass != null) {
                types.add(genericSuperclass);
            }
            Type[] genericInterfaces = clazz.getGenericInterfaces();
            if (ArrayUtils.isNotEmpty(genericInterfaces)) {
                types.addAll(Arrays.asList(genericInterfaces));
            }

            //Filter the main class of the request main class, taking the first one.
            Type typeFilter = types.stream().filter(linkType -> {
                Class<?> typeClass = getTypeClass(linkType, classLoader);
                if (typeClass == null) {
                    return false;
                }
                //Cache the class objects of the current classes for future use
                type_classes.putIfAbsent(linkType, typeClass);
                return request.isAssignableRequest(typeClass);
            }).findFirst().orElse(null);

            //If there is nothing available, simply exit the loop.
            if (typeFilter == null) {
                goWhile = false;
                continue;
            }

            //If it is a type that carries a generic, then obtain whether
            // it contains a generic that responds to the main class and obtain it.
            if (typeFilter instanceof ParameterizedType) {
                Object arguments = getActualResponseTypeArguments(typeFilter, clazz.getClassLoader());
                if (arguments != null) {
                    type = arguments;
                    //After obtaining it, bind the response generic of the
                    // main class and use it for subsequent caching.
                    rps_classes.putIfAbsent(inletClass, type);
                    goWhile = false;
                } else {

                    //The paradigm does not carry response generics, and the class
                    // object logic for filtering types is executed.
                    // According to the specification of the idea, it will not go this far.
                    clazz = type_classes.get(typeFilter);
                }
            } else {

                //Non generic classes, directly use class objects of filter types for subsequent logic.
                clazz = type_classes.get(typeFilter);
            }

        }

        //If the type is empty, cache a default type.
        if (type == null) {
            rps_classes.put(inletClass, HttpResultResponse.class);
        }
        return rps_classes.get(inletClass);
    }

    private static Object getActualResponseTypeArguments(Type type, ClassLoader classLoader) {
        Type[] actualTypeArguments = ((ParameterizedType) type).getActualTypeArguments();
        // as Response and retrieve the first one.
        return Arrays.stream(actualTypeArguments).filter(argType -> {
                    Class<?> typeClass = getTypeClass(argType, classLoader);
                    return typeClass != null && Response.class.isAssignableFrom(typeClass);
                })
                .findFirst()
                .orElse(null);
    }

    private static Class<?> getTypeClass(Type type, ClassLoader classLoader) {
        String typeName = type.getTypeName();
        String className = typeName;
        //A generic type name that carries<...>, After removing such content,
        // the original name of the class is obtained.
        if (typeName.contains(LEFT_ANGLE_BRACKET) && typeName.contains(RIGHT_ANGLE_BRACKET)) {
            className = typeName.split(LEFT_ANGLE_BRACKET)[0];
        }
        Class<?> classObj;
        try {
            classObj = Class.forName(className, false, classLoader);
        } catch (ClassNotFoundException e) {
            classObj = null;
        }
        return classObj;
    }

    public static Object[] toLoggerArray(Object... args) {
        return args;
    }
}
