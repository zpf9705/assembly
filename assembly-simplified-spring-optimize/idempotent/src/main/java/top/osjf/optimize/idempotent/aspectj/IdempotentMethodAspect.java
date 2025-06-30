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
import top.osjf.optimize.idempotent.decoder.Decoder;
import top.osjf.optimize.idempotent.decoder.JSONDecoder;
import top.osjf.optimize.idempotent.exception.IdempotentException;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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
     * Get the spring el expression for accessing the URI mapping path.
     */
    private static final String GET_URI_EXPRESSION = "#" + RequestAttributes.REFERENCE_REQUEST + ".getRequestURI()";

    /**
     * Shared SpEL expression parser instance.
     */
    private final ExpressionParser expressionParser = new SpelExpressionParser();

    /**
     * Parameter name discoverer used to extract method parameter names.
     */
    private final ParameterNameDiscoverer parameterNameDiscoverer = new LocalVariableTableParameterNameDiscoverer();

    private final IdempotentCache cache = new IdempotentCache();

    private final JSONDecoder jsonDecoder = new JSONDecoder();

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Around("@annotation(idempotentAnnotation)")
    public Object around(ProceedingJoinPoint pjp, Idempotent idempotentAnnotation) throws Throwable {
        // Obtain idempotent identifier.
        String idempotentKey = generateIdempotentKey(pjp, idempotentAnnotation);

        // Verify if the current request is a duplicate request.
        TimeUnit timeUnit = idempotentAnnotation.timeUnit();
        long duration = idempotentAnnotation.duration();
        boolean cacheSuccess = cache.cacheIdempotent(idempotentKey, timeUnit.toNanos(duration));
        Assert.isTrue(cacheSuccess, () -> {
            throw new IdempotentException(idempotentAnnotation.message());
        });

        try {
            Object result = pjp.proceed();
            if (idempotentAnnotation.removeKeyWhenFinished()) {
                cache.removeIdempotent(idempotentKey);
            }
            return result;
        }
        catch (Throwable e) {
            // When there is an exception, decide whether to delete idempotent keys based on the configuration.
            if (idempotentAnnotation.removeKeyWhenError()) {
                cache.removeIdempotent(idempotentKey);
            }
            throw e;
        }
    }

    private String generateIdempotentKey(ProceedingJoinPoint pjp, Idempotent idempotentAnnotation) {

        // build the spEL context based on the current tangent point.
        StandardEvaluationContext context = buildStandardEvaluationContext(pjp);

        // maybe url prefix ?
        String urlPrefix = "";
        if (RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes
                && idempotentAnnotation.addUriPrefixIfWebRequest()) {
            urlPrefix = expressionParser.parseExpression(GET_URI_EXPRESSION).getValue(context, String.class);
        }

        //Resolve the unique identifier

        String expressionValue;
        String expressionString = idempotentAnnotation.value();
        if ("".equals(expressionString)) {
            Object[] args = pjp.getArgs();
            Assert.notEmpty(args, "No args"); // The expression is empty, and the parameter must exist.
            // If the expression is empty, then convert the method parameter array
            // to JSON and concatenate them as idempotent keys.
            expressionValue =  Arrays.stream(pjp.getArgs()).map(jsonDecoder::decode)
                    .collect(Collectors.joining("-"));
        }
        else {
            try {
                expressionValue = expressionParser.parseExpression(expressionString).getValue(context, String.class);
            }
            catch (ParseException ex){
                // exceptions return self .
                expressionValue = expressionString;
            }
        }

        return urlPrefix + "@" + expressionValue;
    }

    /**
     * Build a method level {@link MethodBasedEvaluationContext}, which can use the spring
     * el expression to obtain the relevant content of {@link HttpServletRequest} (if it is
     * a web request) and method parameters.
     * @param pjp the aspectj {@link ProceedingJoinPoint} instance.
     * @return The expression execution context instance of the component.
     */
    private StandardEvaluationContext buildStandardEvaluationContext(ProceedingJoinPoint pjp) {

        // Retrieve the current method and its parameters.
        Method method = ((MethodSignature) pjp.getSignature()).getMethod();
        Object[] args = pjp.getArgs();

        StandardEvaluationContext context
                = new MethodBasedEvaluationContext(pjp.getTarget(), method, args, parameterNameDiscoverer);

        // Support for parameter parsing.
        String[] parameterNames = parameterNameDiscoverer.getParameterNames(method);
        if (parameterNames != null && parameterNames.length > 0) {
            for (int i = 0; i < parameterNames.length; i++) {
                context.setVariable(parameterNames[i], args[i]);
            }
        }

        // If in a servlet environment, the request information is placed
        // in context for easy retrieval of request parameters.
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes instanceof ServletRequestAttributes) {
            context.setVariable(RequestAttributes.REFERENCE_REQUEST,
                    ((ServletRequestAttributes) requestAttributes).getRequest());
        }

        // Add JSON Decoder support.
        context.setVariable("json", jsonDecoder);

        // Add other Decoder support.
        applicationContext.getBeansOfType(Decoder.class).forEach(context::setVariable);
        return context;
    }
}
