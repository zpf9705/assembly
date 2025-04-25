/*
 * Copyright 2002-2019 the original author or authors.
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

package top.osjf.proxy.core;

import org.aopalliance.intercept.MethodInvocation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

/**
 * NOTE: This file has been copied and slightly modified from {org.springframework.aop}.
 * <p>
 * Spring's implementation of the AOP Alliance
 * {@link org.aopalliance.intercept.MethodInvocation} interface,
 * implementing the extended
 * {@link ProxyMethodInvocation} interface.
 *
 * <p>Invokes the target object using reflection. Subclasses can override the
 * {@link #invokeJoinpoint()} method to change this behavior, so this is also
 * a useful base class for more specialized MethodInvocation implementations.
 *
 * <p>It is possible to clone an invocation, to invoke {@link #proceed()}
 * repeatedly (once per clone), using the {@link #invocableClone()} method.
 * It is also possible to attach custom attributes to the invocation,
 * using the {@link #setUserAttribute} / {@link #getUserAttribute} methods.
 *
 * <p><b>NOTE:</b> This class is considered internal and should not be
 * directly accessed. The sole reason for it being public is compatibility
 * with existing framework integrations (e.g. Pitchfork). For any other
 * purposes, use the {@link ProxyMethodInvocation} interface instead.
 *
 * @see #invokeJoinpoint
 * @see #proceed
 * @see #invocableClone
 * @see #setUserAttribute
 * @see #getUserAttribute
 */
public class ReflectiveMethodInvocation implements ProxyMethodInvocation, Cloneable {

    protected final Object proxy;

    @Nullable
    protected final Object target;

    protected final Method method;

    protected Object[] arguments;

    /**
     * Lazily initialized map of user-specific attributes for this invocation.
     */
    @Nullable
    private Map<String, Object> userAttributes;

    /**
     * Construct a new ReflectiveMethodInvocation with the given arguments.
     * @param proxy the proxy object that the invocation was made on
     * @param target the target object to invoke
     * @param method the method to invoke
     * @param arguments the arguments to invoke the method with
     */
    protected ReflectiveMethodInvocation(
            Object proxy, @Nullable Object target, Method method, @Nullable Object[] arguments) {

        /*
         * All parameters are based on those given by the agent.
         */
        this.proxy = proxy;
        this.target = target;
        this.method = method;
        this.arguments = arguments;
    }


    @Override
    public final Object getProxy() {
        return this.proxy;
    }

    @Override
    @Nullable
    public final Object getThis() {
        return this.target;
    }

    @Nonnull
    @Override
    public final AccessibleObject getStaticPart() {
        return this.method;
    }

    /**
     * Return the method invoked on the proxied interface.
     * May or may not correspond with a method invoked on an underlying
     * implementation of that interface.
     */
    @Nonnull
    @Override
    public final Method getMethod() {
        return this.method;
    }

    @Nonnull
    @Override
    public final Object[] getArguments() {
        return this.arguments;
    }

    @Override
    public void setArguments(Object... arguments) {
        this.arguments = arguments;
    }


    @Override
    @Nullable
    public Object proceed() throws Throwable {
        return invokeJoinpoint();
    }

    /**
     * Invoke the joinpoint using reflection.
     * Subclasses can override this to use custom invocation.
     * @return the return value of the joinpoint
     * @throws Throwable if invoking the joinpoint resulted in an exception
     */
    @Nullable
    protected Object invokeJoinpoint() throws Throwable {
        if ((!Modifier.isPublic(method.getModifiers()) ||
                !Modifier.isPublic(method.getDeclaringClass().getModifiers())) && !method.isAccessible()) {
            method.setAccessible(true);
        }
        // Use reflection to invoke the method.
        try {
            return method.invoke(target, arguments);
        }
        catch (InvocationTargetException ex) {
            // Invoked method threw a checked exception.
            // We must rethrow it. The client won't see the interceptor.
            throw ex.getTargetException();
        }
        catch (IllegalArgumentException ex) {
            throw new AopInvocationException("AOP configuration seems to be invalid: tried calling method [" +
                    method + "] on target [" + target + "]", ex);
        }
        catch (IllegalAccessException ex) {
            throw new AopInvocationException("Could not access method [" + method + "]", ex);
        }
    }

    /**
     * This implementation returns a shallow copy of this invocation object,
     * including an independent copy of the original arguments array.
     * <p>We want a shallow copy in this case: We want to use the same interceptor
     * chain and other object references, but we want an independent value for the
     * current interceptor index.
     * @see java.lang.Object#clone()
     */
    @Override
    public MethodInvocation invocableClone() {
        Object[] cloneArguments = this.arguments;
        if (this.arguments.length > 0) {
            // Build an independent copy of the arguments array.
            cloneArguments = this.arguments.clone();
        }
        return invocableClone(cloneArguments);
    }

    /**
     * This implementation returns a shallow copy of this invocation object,
     * using the given arguments array for the clone.
     * <p>We want a shallow copy in this case: We want to use the same interceptor
     * chain and other object references, but we want an independent value for the
     * current interceptor index.
     * @see java.lang.Object#clone()
     */
    @Override
    public MethodInvocation invocableClone(Object... arguments) {
        // Force initialization of the user attributes Map,
        // for having a shared Map reference in the clone.
        if (this.userAttributes == null) {
            this.userAttributes = new HashMap<>();
        }

        // Create the MethodInvocation clone.
        try {
            ReflectiveMethodInvocation clone = (ReflectiveMethodInvocation) clone();
            clone.arguments = arguments;
            return clone;
        }
        catch (CloneNotSupportedException ex) {
            throw new IllegalStateException(
                    "Should be able to clone object of type [" + getClass() + "]: " + ex);
        }
    }


    @Override
    public void setUserAttribute(String key, @Nullable Object value) {
        if (value != null) {
            if (this.userAttributes == null) {
                this.userAttributes = new HashMap<>();
            }
            this.userAttributes.put(key, value);
        }
        else {
            if (this.userAttributes != null) {
                this.userAttributes.remove(key);
            }
        }
    }

    @Override
    @Nullable
    public Object getUserAttribute(String key) {
        return (this.userAttributes != null ? this.userAttributes.get(key) : null);
    }

    /**
     * Return user attributes associated with this invocation.
     * This method provides an invocation-bound alternative to a ThreadLocal.
     * <p>This map is initialized lazily and is not used in the AOP framework itself.
     * @return any user attributes associated with this invocation
     * (never {@code null})
     */
    public Map<String, Object> getUserAttributes() {
        if (this.userAttributes == null) {
            this.userAttributes = new HashMap<>();
        }
        return this.userAttributes;
    }


    @Override
    public String toString() {
        // Don't do toString on target, it may be proxied.
        StringBuilder sb = new StringBuilder("ReflectiveMethodInvocation: ");
        sb.append(this.method).append("; ");
        if (this.target == null) {
            sb.append("target is null");
        }
        else {
            sb.append("target is of class [").append(this.target.getClass().getName()).append(']');
        }
        return sb.toString();
    }

}
