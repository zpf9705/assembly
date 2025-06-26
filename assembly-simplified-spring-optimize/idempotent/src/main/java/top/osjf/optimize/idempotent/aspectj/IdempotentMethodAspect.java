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

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParseException;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import top.osjf.optimize.idempotent.annotation.Idempotent;
import top.osjf.optimize.idempotent.cache.IdempotentCache;
import top.osjf.optimize.idempotent.exception.IdempotentException;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * NOTE: This file has been copied and slightly modified from {com.healthy-chn.cloud}.
 * <p>
 * The idempotent aspect support class intercepts methods carrying idempotent annotations
 * {@link Idempotent}, parses the information required for idempotent annotations and
 * method information, controls the idempotent effective time, and comprehensively manages
 * idempotent information before and after method execution.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.4
 */
@Aspect
public class IdempotentMethodAspect implements ApplicationContextAware {

    /**
     * Shared SpEL expression parser instance.
     */
    private final ExpressionParser expressionParser = new SpelExpressionParser();

    /**
     * Parameter name discoverer used to extract method parameter names.
     */
    private final ParameterNameDiscoverer parameterNameDiscoverer = new LocalVariableTableParameterNameDiscoverer();

    private final IdempotentCache cache = new IdempotentCache();

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Around("@annotation(idempotentAnnotation)")
    public Object around(ProceedingJoinPoint joinPoint, Idempotent idempotentAnnotation) throws Throwable {
        // Obtain idempotent identifier.
        String idempotentKey = generateIdempotentKey(joinPoint, idempotentAnnotation);

        // Verify if the current request is a duplicate request.
        TimeUnit timeUnit = idempotentAnnotation.timeUnit();
        long duration = idempotentAnnotation.duration();
        boolean cacheSuccess = cache.cacheIdempotent(idempotentKey, timeUnit.toNanos(duration));
        Assert.isTrue(cacheSuccess, () -> {
            throw new IdempotentException(idempotentAnnotation.message());
        });

        try {
            Object result = joinPoint.proceed();
            if (idempotentAnnotation.removeKeyWhenFinished()) {
                cache.removeIdempotent(idempotentKey);
            }
            return result;
        } catch (Throwable e) {
            // When there is an exception, decide whether to delete idempotent keys based on the configuration.
            if (idempotentAnnotation.removeKeyWhenError()) {
                cache.removeIdempotent(idempotentKey);
            }
            throw e;
        }
    }

    private String generateIdempotentKey(ProceedingJoinPoint pjp, Idempotent idempotentAnnotation) {
        String unique = idempotentAnnotation.unique();
        // If no expression is filled in, return prefix directly.
        if ("".equals(unique)) {
            return idempotentAnnotation.prefix();
        }

        // Obtain the spEL context based on the current tangent point.
        StandardEvaluationContext context = getStandardEvaluationContext(pjp);

        // Combine with the prefix to obtain the complete key
        return parseExpression(idempotentAnnotation.prefix(), context) +
                //Resolve the unique identifier
                "@" + parseExpression(unique, context);
    }

    private String parseExpression(String expressionString, StandardEvaluationContext context) {
        if ("".equals(expressionString)) {
            return "";
        }
        try {
            return expressionParser.parseExpression(expressionString).getValue(context, String.class);
        } catch (ParseException ex) {
            // exceptions return self .
            return expressionString;
        }
    }

    private StandardEvaluationContext getStandardEvaluationContext(ProceedingJoinPoint pjp) {

        // Retrieve the current method and its parameters.
        Method method = ((MethodSignature) pjp.getSignature()).getMethod();
        Object[] args = pjp.getArgs();

        StandardEvaluationContext context
                = new MethodBasedEvaluationContext(pjp.getTarget(), method, args, parameterNameDiscoverer);

        // Set support for bean name resolution.
        context.setBeanResolver(new BeanFactoryResolver(applicationContext.getAutowireCapableBeanFactory()));

        // Support for parameter parsing.
        String[] parameterNames = parameterNameDiscoverer.getParameterNames(method);
        if (parameterNames != null && parameterNames.length > 0) {
            for (int i = 0; i < parameterNames.length; i++) {
                context.setVariable(parameterNames[i], args[i]);
            }
        }

        // If in a servlet environment, the request information is placed
        // in context for easy retrieval of request parameters.
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder
                .getRequestAttributes();
        if (requestAttributes != null) {
            context.setVariable(RequestAttributes.REFERENCE_REQUEST, requestAttributes.getRequest());
        }
        return context;
    }
}
