
package top.osjf.commons.util;

import top.osjf.commons.lang.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * This class was copied from {@code org.springframework.beans}, with minor modifications
 * and adaptations. I would like to express my sincere gratitude here!
 *
 * Static convenience methods for JavaBeans: for instantiating beans,
 * checking bean property types, copying bean properties, etc.
 *
 * <p>Mainly for internal use within the framework, but to some degree also
 * useful for application classes. Consider
 * <a href="https://commons.apache.org/proper/commons-beanutils/">Apache Commons BeanUtils</a>,
 * <a href="https://hotelsdotcom.github.io/bull/">BULL - Bean Utils Light Library</a>,
 * or similar third-party frameworks for more comprehensive bean utilities.
 */
public abstract class BeanUtils {

    private static final Map<Class<?>, Object> DEFAULT_TYPE_VALUES;

    static {
        Map<Class<?>, Object> values = new HashMap<>();
        values.put(boolean.class, false);
        values.put(byte.class, (byte) 0);
        values.put(short.class, (short) 0);
        values.put(int.class, 0);
        values.put(long.class, 0L);
        values.put(float.class, 0F);
        values.put(double.class, 0D);
        values.put(char.class, '\0');
        DEFAULT_TYPE_VALUES = Collections.unmodifiableMap(values);
    }

    /**
     * Instantiate a class using its 'primary' constructor (for Kotlin classes,
     * potentially having default arguments declared) or its default constructor
     * (for regular Java classes, expecting a standard no-arg setup).
     * <p>Note that this method tries to set the constructor accessible
     * if given a non-accessible (that is, non-public) constructor.
     * @param clazz the class to instantiate
     * @return the new instance
     * @throws BeanInstantiationException if the bean cannot be instantiated.
     * The cause may notably indicate a {@link NoSuchMethodException} if no
     * primary/default constructor was found, a {@link NoClassDefFoundError}
     * or other {@link LinkageError} in case of an unresolvable class definition
     * (e.g. due to a missing dependency at runtime), or an exception thrown
     * from the constructor invocation itself.
     * @see Constructor#newInstance
     * @param <T>
     */
    public static <T> T instantiateClass(Class<T> clazz) throws BeanInstantiationException {
        Assert.notNull(clazz, "Class must not be null");
        if (clazz.isInterface()) {
            throw new BeanInstantiationException(clazz, "Specified class is an interface");
        }
        try {
            return instantiateClass(clazz.getDeclaredConstructor());
        }
        catch (NoSuchMethodException ex) {
            throw new BeanInstantiationException(clazz, "No default constructor found", ex);
        }
        catch (LinkageError err) {
            throw new BeanInstantiationException(clazz, "Unresolvable class definition", err);
        }
    }

    /**
     * Instantiate a class using its no-arg constructor and return the new instance
     * as the specified assignable type.
     * <p>Useful in cases where the type of the class to instantiate (clazz) is not
     * available, but the type desired (assignableTo) is known.
     * <p>Note that this method tries to set the constructor accessible if given a
     * non-accessible (that is, non-public) constructor.
     * @param clazz class to instantiate
     * @param assignableTo type that clazz must be assignableTo
     * @return the new instance
     * @throws BeanInstantiationException if the bean cannot be instantiated
     * @see Constructor#newInstance
     * @param <T>
     */
    @SuppressWarnings("unchecked")
    public static <T> T instantiateClass(Class<?> clazz, Class<T> assignableTo) throws BeanInstantiationException {
        Assert.isAssignable(assignableTo, clazz);
        return (T) instantiateClass(clazz);
    }

    /**
     * Convenience method to instantiate a class using the given constructor.
     * <p>Note that this method tries to set the constructor accessible if given a
     * non-accessible (that is, non-public) constructor, and supports Kotlin classes
     * with optional parameters and default values.
     * @param ctor the constructor to instantiate
     * @param args the constructor arguments to apply (use {@code null} for an unspecified
     * parameter, Kotlin optional parameters and Java primitive types are supported)
     * @return the new instance
     * @throws BeanInstantiationException if the bean cannot be instantiated
     * @see Constructor#newInstance
     * @param <T>
     */
    public static <T> T instantiateClass(Constructor<T> ctor, Object... args) throws BeanInstantiationException {
        Assert.notNull(ctor, "Constructor must not be null");
        try {
            ReflectionUtils.makeAccessible(ctor);
            Class<?>[] parameterTypes = ctor.getParameterTypes();
            Assert.isTrue(args.length <= parameterTypes.length, "Can't specify more arguments than constructor parameters");
            Object[] argsWithDefaultValues = new Object[args.length];
            for (int i = 0 ; i < args.length; i++) {
                if (args[i] == null) {
                    Class<?> parameterType = parameterTypes[i];
                    argsWithDefaultValues[i] = (parameterType.isPrimitive() ? DEFAULT_TYPE_VALUES.get(parameterType) : null);
                }
                else {
                    argsWithDefaultValues[i] = args[i];
                }
            }
            return ctor.newInstance(argsWithDefaultValues);

        }
        catch (InstantiationException ex) {
            throw new BeanInstantiationException(ctor, "Is it an abstract class?", ex);
        }
        catch (IllegalAccessException ex) {
            throw new BeanInstantiationException(ctor, "Is the constructor accessible?", ex);
        }
        catch (IllegalArgumentException ex) {
            throw new BeanInstantiationException(ctor, "Illegal arguments for constructor", ex);
        }
        catch (InvocationTargetException ex) {
            throw new BeanInstantiationException(ctor, "Constructor threw exception", ex.getTargetException());
        }
    }

    /**
     * Return a resolvable constructor for the provided class, either a primary or single
     * public constructor with arguments, or a single non-public constructor with arguments,
     * or simply a default constructor. Callers have to be prepared to resolve arguments
     * for the returned constructor's parameters, if any.
     * @param clazz the class to check
     * @throws IllegalStateException in case of no unique constructor found at all
     * @param <T>
     */
    @SuppressWarnings("unchecked")
    public static <T> Constructor<T> getResolvableConstructor(Class<T> clazz) {

        Constructor<?>[] ctors = clazz.getConstructors();
        if (ctors.length == 1) {
            // A single public constructor
            return (Constructor<T>) ctors[0];
        }
        else if (ctors.length == 0){
            ctors = clazz.getDeclaredConstructors();
            if (ctors.length == 1) {
                // A single non-public constructor, e.g. from a non-public record type
                return (Constructor<T>) ctors[0];
            }
        }

        // Several constructors -> let's try to take the default constructor
        try {
            return clazz.getDeclaredConstructor();
        }
        catch (NoSuchMethodException ex) {
            // Giving up...
        }

        // No unique constructor at all
        throw new IllegalStateException("No primary or single unique constructor found for " + clazz);
    }

    /**
     * Find a method with the given method name and the given parameter types,
     * declared on the given class or one of its superclasses. Prefers public methods,
     * but will return a protected, package access, or private method too.
     * <p>Checks {@code Class.getMethod} first, falling back to
     * {@code findDeclaredMethod}. This allows to find public methods
     * without issues even in environments with restricted Java security settings.
     * @param clazz the class to check
     * @param methodName the name of the method to find
     * @param paramTypes the parameter types of the method to find
     * @return the Method object, or {@code null} if not found
     * @see Class#getMethod
     * @see #findDeclaredMethod
     */
    @Nullable
    public static Method findMethod(Class<?> clazz, String methodName, Class<?>... paramTypes) {
        try {
            return clazz.getMethod(methodName, paramTypes);
        }
        catch (NoSuchMethodException ex) {
            return findDeclaredMethod(clazz, methodName, paramTypes);
        }
    }

    /**
     * Find a method with the given method name and the given parameter types,
     * declared on the given class or one of its superclasses. Will return a public,
     * protected, package access, or private method.
     * <p>Checks {@code Class.getDeclaredMethod}, cascading upwards to all superclasses.
     * @param clazz the class to check
     * @param methodName the name of the method to find
     * @param paramTypes the parameter types of the method to find
     * @return the Method object, or {@code null} if not found
     * @see Class#getDeclaredMethod
     */
    @Nullable
    public static Method findDeclaredMethod(Class<?> clazz, String methodName, Class<?>... paramTypes) {
        try {
            return clazz.getDeclaredMethod(methodName, paramTypes);
        }
        catch (NoSuchMethodException ex) {
            if (clazz.getSuperclass() != null) {
                return findDeclaredMethod(clazz.getSuperclass(), methodName, paramTypes);
            }
            return null;
        }
    }

    /**
     * Find a method with the given method name and minimal parameters (best case: none),
     * declared on the given class or one of its superclasses. Prefers public methods,
     * but will return a protected, package access, or private method too.
     * <p>Checks {@code Class.getMethods} first, falling back to
     * {@code findDeclaredMethodWithMinimalParameters}. This allows for finding public
     * methods without issues even in environments with restricted Java security settings.
     * @param clazz the class to check
     * @param methodName the name of the method to find
     * @return the Method object, or {@code null} if not found
     * @throws IllegalArgumentException if methods of the given name were found but
     * could not be resolved to a unique method with minimal parameters
     * @see Class#getMethods
     * @see #findDeclaredMethodWithMinimalParameters
     */
    @Nullable
    public static Method findMethodWithMinimalParameters(Class<?> clazz, String methodName)
            throws IllegalArgumentException {

        Method targetMethod = findMethodWithMinimalParameters(clazz.getMethods(), methodName);
        if (targetMethod == null) {
            targetMethod = findDeclaredMethodWithMinimalParameters(clazz, methodName);
        }
        return targetMethod;
    }

    /**
     * Find a method with the given method name and minimal parameters (best case: none),
     * declared on the given class or one of its superclasses. Will return a public,
     * protected, package access, or private method.
     * <p>Checks {@code Class.getDeclaredMethods}, cascading upwards to all superclasses.
     * @param clazz the class to check
     * @param methodName the name of the method to find
     * @return the Method object, or {@code null} if not found
     * @throws IllegalArgumentException if methods of the given name were found but
     * could not be resolved to a unique method with minimal parameters
     * @see Class#getDeclaredMethods
     */
    @Nullable
    public static Method findDeclaredMethodWithMinimalParameters(Class<?> clazz, String methodName)
            throws IllegalArgumentException {

        Method targetMethod = findMethodWithMinimalParameters(clazz.getDeclaredMethods(), methodName);
        if (targetMethod == null && clazz.getSuperclass() != null) {
            targetMethod = findDeclaredMethodWithMinimalParameters(clazz.getSuperclass(), methodName);
        }
        return targetMethod;
    }

    /**
     * Find a method with the given method name and minimal parameters (best case: none)
     * in the given list of methods.
     * @param methods the methods to check
     * @param methodName the name of the method to find
     * @return the Method object, or {@code null} if not found
     * @throws IllegalArgumentException if methods of the given name were found but
     * could not be resolved to a unique method with minimal parameters
     */
    @Nullable
    public static Method findMethodWithMinimalParameters(Method[] methods, String methodName)
            throws IllegalArgumentException {

        Method targetMethod = null;
        int numMethodsFoundWithCurrentMinimumArgs = 0;
        for (Method method : methods) {
            if (method.getName().equals(methodName)) {
                int numParams = method.getParameterCount();
                if (targetMethod == null || numParams < targetMethod.getParameterCount()) {
                    targetMethod = method;
                    numMethodsFoundWithCurrentMinimumArgs = 1;
                }
                else if (!method.isBridge() && targetMethod.getParameterCount() == numParams) {
                    if (targetMethod.isBridge()) {
                        // Prefer regular method over bridge...
                        targetMethod = method;
                    }
                    else {
                        // Additional candidate with same length
                        numMethodsFoundWithCurrentMinimumArgs++;
                    }
                }
            }
        }
        if (numMethodsFoundWithCurrentMinimumArgs > 1) {
            throw new IllegalArgumentException("Cannot resolve method '" + methodName +
                    "' to a unique method. Attempted to resolve to overloaded method with " +
                    "the least number of parameters but there were " +
                    numMethodsFoundWithCurrentMinimumArgs + " candidates.");
        }
        return targetMethod;
    }

}
