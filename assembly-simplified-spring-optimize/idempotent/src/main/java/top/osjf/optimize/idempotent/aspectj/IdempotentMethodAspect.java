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
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.expression.BeanResolver;
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
import top.osjf.optimize.idempotent.cache.IdempotentCaffeineCache;
import top.osjf.optimize.idempotent.decoder.Decoder;
import top.osjf.optimize.idempotent.decoder.JSONDecoder;
import top.osjf.optimize.idempotent.exception.IdempotentException;
import top.osjf.optimize.idempotent.exception.IdempotentExceptionTranslator;
import top.osjf.optimize.idempotent.global.config.IdempotentGlobalConfiguration;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
public class IdempotentMethodAspect implements ApplicationContextAware , ApplicationListener<ContextRefreshedEvent> {

    /**
     * Get the spring el expression for accessing the URI mapping path.
     */
    private static final String GET_URI_EXPRESSION = "#" + RequestAttributes.REFERENCE_REQUEST + ".getRequestURI()";

    /**
     * Shared SpEL expression parser instance.
     */
    private final SpelExpressionParser expressionParser = new SpelExpressionParser();

    /**
     * Parameter name discoverer used to extract method parameter names.
     */
    private final ParameterNameDiscoverer parameterNameDiscoverer = new LocalVariableTableParameterNameDiscoverer();

    private IdempotentCache cache;

    private final JSONDecoder jsonDecoder = new JSONDecoder();

    private ApplicationContext applicationContext;
    private BeanResolver beanResolver;

    /**
     * The Global configuration.
     */
    private IdempotentGlobalConfiguration globalConfiguration;

    private IdempotentExceptionTranslator idempotentExceptionTranslator;

    /**
     * Set the global configuration {@link IdempotentGlobalConfiguration} as a global
     * configuration property when certain properties do not have personalized configurations.
     * @param globalConfiguration the specialized global config of idempotent.
     */
    public void setGlobalConfiguration(IdempotentGlobalConfiguration globalConfiguration) {
        this.globalConfiguration = globalConfiguration;
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        beanResolver = new BeanFactoryResolver(this.applicationContext);
    }

    @Override
    public void onApplicationEvent(@NonNull ContextRefreshedEvent event) {
        loadIdempotentCache();
        loadIdempotentExceptionTranslator();
    }

    @Around("@annotation(idempotentAnnotation)")
    public Object around(ProceedingJoinPoint pjp, Idempotent idempotentAnnotation) throws Throwable {
        // Obtain idempotent identifier.
        String idempotentKey = generateIdempotentKey(pjp, idempotentAnnotation);

        // Verify if the current request is a duplicate request.
        boolean cacheSuccess = cache.cacheIdempotent(idempotentKey, getDuration(idempotentAnnotation));
        if (!cacheSuccess) {
            IdempotentException ex = new IdempotentException(getIdempotentFailedMessage(idempotentAnnotation));
            if (idempotentExceptionTranslator != null) {
                throw idempotentExceptionTranslator.translate(ex);
            }
            throw ex;
        }

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

    /**
     * Return the nanosecond value of the control gap for idempotent keys. If no annotation is
     * provided, use the globally configured {@link IdempotentGlobalConfiguration#getDuration()}.
     * @param idempotentAnnotation the idempotent annotation.
     * @return the idempotent key cache duration nanos.
     */
    private long getDuration(Idempotent idempotentAnnotation) {
        long duration = idempotentAnnotation.duration();
        if (duration == -1 && globalConfigExist()) {
            duration = globalConfiguration.getDuration();
        }
        return idempotentAnnotation.timeUnit().toNanos(duration);
    }

    /**
     * Return a reminder message after idempotent parity failure. If no annotation is provided,
     * use the global configuration {@link IdempotentGlobalConfiguration#getMessage()}.
     * @param idempotentAnnotation the idempotent annotation.
     * @return the reminder message for idempotent key verification failure.
     */
    private String getIdempotentFailedMessage(Idempotent idempotentAnnotation) {
        String message = idempotentAnnotation.message();
        if ("".equals(message) && globalConfigExist()) {
            message = globalConfiguration.getMessage();
        }
        return message;
    }

    /**
     * @return The {@code boolean} flag that {@link #globalConfiguration} not {@literal null}.
     */
    private boolean globalConfigExist() {
        return globalConfiguration != null;
    }

    /**
     * Load the highest priority {@link IdempotentCache} in the Spring container,
     * If it does not exist in the container, the default {@link IdempotentCaffeineCache}
     * will be used.
     */
    private void loadIdempotentCache() {
        List<IdempotentCache> caches
                = new ArrayList<>(applicationContext.getBeansOfType(IdempotentCache.class).values());
        if (caches.isEmpty()) {
            cache = new IdempotentCaffeineCache();
        }
        else {
            if (caches.size() > 1) {
                AnnotationAwareOrderComparator.sort(caches);
            }
            cache = caches.get(0);
        }
    }

    /**
     * @return The {@link IdempotentCache} instance after loading.
     */
    public IdempotentCache getCache() {
        return cache;
    }

    /**
     * Load the highest priority {@link IdempotentExceptionTranslator} in the Spring container.
     */
    public void loadIdempotentExceptionTranslator() {
        List<IdempotentExceptionTranslator> translators
                = new ArrayList<>(applicationContext.getBeansOfType(IdempotentExceptionTranslator.class).values());
        if (translators.isEmpty()) {
            return;
        }
        if (translators.size() > 1) {
            AnnotationAwareOrderComparator.sort(translators);
        }
        idempotentExceptionTranslator = translators.get(0);
    }

    /**
     * Returns an idempotent key string generated based on idempotent annotation information
     * and slice method information.
     * @param pjp the aspectj sectional information object.
     * @param idempotentAnnotation the idempotent annotation.
     * @return the idempotent key.
     */
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

        StandardEvaluationContext context
                = new JoinPointMethodBasedEvaluationContext(pjp, parameterNameDiscoverer, beanResolver);

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
