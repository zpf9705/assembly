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


package top.osjf.optimize.idempotent.aspectj;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.AccessException;
import org.springframework.expression.BeanResolver;
import org.springframework.expression.EvaluationContext;
import org.springframework.lang.NonNull;

import javax.annotation.Nullable;

/**
 * A {@link JoinPoint} method-based {@link EvaluationContext} that provides explicit
 * support for method-based invocations.
 *
 * <p>Expose the actual method arguments using the following aliases:
 * <ol>
 * <li>pX where X is the index of the argument (p0 for the first argument)</li>
 * <li>aX where X is the index of the argument (a1 for the second argument)</li>
 * <li>the name of the parameter as discovered by a configurable {@link ParameterNameDiscoverer}</li>
 * </ol>
 *
 * <p><strong>Note:</strong>
 * When it is not possible to query values from a method, {@link #lookupVariable(String)}
 * will provide search support for {@link #getBeanResolver()}. The use of '#' to obtain
 * beans is not recommended, and this is also for more flexible retrieval, which obviously
 * violates the original design intention of spring el. Please follow the usage of spring
 * el in general and use it if necessary.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.4
 */
public class JoinPointMethodBasedEvaluationContext extends MethodBasedEvaluationContext {

    /**
     * Constructs a {@code JoinPointMethodBasedEvaluationContext} with given {@link JoinPoint}
     * instance.
     * <p>Default setting {@link DefaultParameterNameDiscoverer}.</p>
     * @param joinPoint the given {@link JoinPoint} instance.
     */
    public JoinPointMethodBasedEvaluationContext(JoinPoint joinPoint) {
        this(joinPoint, new DefaultParameterNameDiscoverer());
    }

    /**
     * Constructs a {@code JoinPointMethodBasedEvaluationContext} with given {@link JoinPoint}
     * instance and the given {@link ParameterNameDiscoverer} instance and there is no {@link BeanResolver}
     * default set to {@literal null}.
     * @param joinPoint               the given {@link JoinPoint} instance.
     * @param parameterNameDiscoverer the given {@link ParameterNameDiscoverer} instance.
     */
    public JoinPointMethodBasedEvaluationContext(JoinPoint joinPoint, ParameterNameDiscoverer parameterNameDiscoverer) {
        this(joinPoint, parameterNameDiscoverer , null);
    }

    /**
     * Constructs a {@code JoinPointMethodBasedEvaluationContext} with given {@link JoinPoint}
     * instance and the given {@link BeanResolver} instance.
     * <p>Default setting {@link DefaultParameterNameDiscoverer}.</p>
     * @param joinPoint               the given {@link JoinPoint} instance.
     * @param beanResolver             the given {@link BeanResolver} instance.
     */
    public JoinPointMethodBasedEvaluationContext(JoinPoint joinPoint, BeanResolver beanResolver) {
        this(joinPoint, new DefaultParameterNameDiscoverer() , beanResolver);
    }

    /**
     * Constructs a {@code JoinPointMethodBasedEvaluationContext} with given {@link JoinPoint}
     * instance and the given {@link ParameterNameDiscoverer} instance and the given {@link BeanResolver}
     * instance
     * <p>Directly set method parameters in the expression call during construction as support.
     * @param joinPoint               the given {@link JoinPoint} instance.
     * @param parameterNameDiscoverer the given {@link ParameterNameDiscoverer} instance.
     * @param beanResolver            the given {@link BeanResolver} instance.
     */
    public JoinPointMethodBasedEvaluationContext(JoinPoint joinPoint, ParameterNameDiscoverer parameterNameDiscoverer,
                                                 BeanResolver beanResolver) {
        super(joinPoint.getTarget(), ((MethodSignature) joinPoint.getSignature()).getMethod(),
                joinPoint.getArgs(), parameterNameDiscoverer);
        // Resolve bean name .
        super.setBeanResolver(beanResolver);
        // Directly load the target parameters during initialization.
        super.lazyLoadArguments();
    }

    /**
     * Support using {@link #getBeanResolver()} to attempt to resolve whether
     * the lookup name is a bean when there is no matching value.
     * @param name {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Nullable
    @Override
    public Object lookupVariable(@NonNull String name) {
        Object o = super.lookupVariable(name);
        if (o == null) {
            try {

                // According to the specification of spring el expressions, '@' is used to obtain bean objects,
                // '#' is used to obtain configuration properties, and the callback here is for obtaining '#'.
                // However, it will support attempting to obtain beans again. Although this violates the syntax
                // requirements of spring, it provides a more flexible expression application. It is recommended
                // to follow the rules of spin el syntax.

                BeanResolver beanResolver = getBeanResolver();
                if (beanResolver != null) {
                    return getBeanResolver().resolve(this, name); // maybe bean name
                }
            }
            catch (AccessException ex){
                Throwable cause = ex.getCause();
                if (cause instanceof RuntimeException) {
                    throw (RuntimeException) cause;
                }
                else {
                    throw new RuntimeException(cause);
                }
            }
        }
        return o;
    }
}
