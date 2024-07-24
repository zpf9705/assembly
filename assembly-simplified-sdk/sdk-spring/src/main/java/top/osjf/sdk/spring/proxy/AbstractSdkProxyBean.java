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

package top.osjf.sdk.spring.proxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.lang.NonNull;
import org.springframework.web.context.WebApplicationContext;
import top.osjf.sdk.core.client.ClientExecutors;
import top.osjf.sdk.core.process.*;
import top.osjf.sdk.core.util.StringUtils;
import top.osjf.sdk.http.HttpSdkSupport;
import top.osjf.sdk.spring.beans.DeterminantDisposableBean;
import top.osjf.sdk.spring.beans.DeterminantInitializingBean;
import top.osjf.sdk.spring.beans.HandlerPostProcessor;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Inheriting {@link ConcentrateProxySupport} implements handing over
 * the object of the jdk dynamic proxy to the spring container for management.
 *
 * <p>When this object is called, the {@link #invoke(Object, Method, Object[])}
 * method will be executed and passed to this abstract class.
 *
 * <p>This class takes on the common parameter processing and converts
 * it into the parameters required for SDK execution.
 *
 * <p>The corresponding executor will be selected based on the full name
 * of a single {@link top.osjf.sdk.core.client.Client},
 * as shown in {@link Request#getClientCls()}.
 *
 * <p>Simply obtain the host parameter from the corresponding proxy class
 * entity to complete the SDK request.
 *
 * @param <T> The data type of the proxy class.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
public abstract class AbstractSdkProxyBean<T> extends ConcentrateProxySupport<T> implements RequestAttributes,
        ApplicationContextAware, InitializingBean, DisposableBean {

    /*** SLF4J logs.*/
    private final Logger log = LoggerFactory.getLogger(getClass());

    /*** Spring container context object.*/
    private ApplicationContext applicationContext;

    /*** The host address when calling SDK.*/
    private String host;

    /*** Customized modifiers for the results of proxy processing.*/
    private final List<HandlerPostProcessor> postProcessors = new ArrayList<>();

    /*** Custom specified current type in proxy class destruction logic..*/
    private final List<DeterminantDisposableBean> disposableBeans = new ArrayList<>();

    /*** The return value {@link #toString()} needs to be formatted.*/
    private static final String TO_STR_FORMAT =
            "Proxy info ( target type [%s] | proxy type [%s] | host [%s] | proxy model [%s] )";

    /**
     * The construction method called when defining the scope of a normal bean
     * , such as {@link org.springframework.beans.factory.config.BeanDefinition#SCOPE_PROTOTYPE}
     * {@link org.springframework.beans.factory.config.BeanDefinition#SCOPE_SINGLETON}.
     */
    public AbstractSdkProxyBean() {
    }

    /**
     * When defining a special scope bean, such as {@link WebApplicationContext#SCOPE_REQUEST}
     * {@link WebApplicationContext#SCOPE_APPLICATION} {@link WebApplicationContext#SCOPE_SESSION},
     * call this constructor to pass the type in advance.
     *
     * @param type When injecting beans, the type of teammates is required.
     *             {@link FactoryBean#getObjectType()}.
     */
    public AbstractSdkProxyBean(Class<T> type) {
        setType(type);
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void setHost(String host) {
        if (StringUtils.isBlank(host)) {
            throw new IllegalArgumentException("SDK access host address cannot be empty!");
        }
        this.host = host;
    }

    @Override
    public String getHost() {
        return host;
    }

    @Override
    public void afterPropertiesSet() throws Exception {

        //Collect request post processors.
        getBeansOfType(HandlerPostProcessor.class,
                h -> h.appointTarget() != null && getType().isAssignableFrom(h.appointTarget()), postProcessors::add);

        //Collect the destruction logic collection of proxy beans of the specified type.
        getBeansOfType(DeterminantDisposableBean.class,
                d -> getType().isAssignableFrom(d.getType()), disposableBeans::add);

        //Collect the initialization logic collection of proxy beans of the specified type and execute it.
        for (DeterminantInitializingBean initializingBean : getBeansOfType(DeterminantInitializingBean.class,
                d -> getType().isAssignableFrom(d.getType()), null)) {
            initializingBean.afterPropertiesSet();
        }
    }

    /**
     * Retrieve the bean collection from the Spring container according to the
     * specified type and determine if it meets the criteria. After sorting,
     * it can be executed concurrently.
     *
     * @param requiredType        Find the type of bean.
     * @param assignableCondition Filter conditions.
     * @param exec                Execute the consumption function.
     * @param <R>                 Generic types.
     * @return The filtered collection of type entities.
     */
    <R> List<R> getBeansOfType(Class<R> requiredType, Predicate<R> assignableCondition,
                               Consumer<R> exec) {
        List<R> beans = new ArrayList<>();
        try {
            for (R bean : applicationContext.getBeansOfType(requiredType).values()) {
                if (assignableCondition.test(bean)) {
                    beans.add(bean);
                }
            }
        } catch (BeansException ignored) {
            if (log.isWarnEnabled()) {
                log.warn("No bean collection of type {} was found from the Spring container.",
                        requiredType.getName());
            }
            // ...Ignoring undefined issues
        }
        AnnotationAwareOrderComparator.sort(beans);
        if (exec != null) beans.forEach(exec);
        return beans;
    }

    @Override
    public Object handle(Object proxy, Method method, Object[] args) {
        return handle0(proxy, method, args);
    }

    @Override
    public void destroy() throws Exception {
        for (DeterminantDisposableBean disposableBean : disposableBeans) {
            disposableBean.destroy();
        }
    }

    /**
     * The parameter processing logic for Jdk dynamic proxy callbacks can be
     * rewritten by subclasses, defined according to their own situation.
     * <p>Here, a default processing posture that conforms to SDK is provided.
     *
     * <p>Support for {@link RequestParameter} and {@link RequestParam} and
     * {@link ResponseData} has been added.
     *
     * @param proxy  Proxy object.
     * @param method The method object executed by the proxy class.
     * @param args   The real parameters executed by the proxy method.
     * @return The result returned by the proxy execution method.
     */
    private Object handle0(@SuppressWarnings("unused") Object proxy,
                           Method method, Object[] args) {
        //Supports toString and returns proxy metadata.
        if ("toString".equals(method.getName())) return toString();
        /*
         * if (!checkMethodCoverRanger(proxy, getType(), method, args))
         *  throw new UnsupportedSDKCallBackMethodException(method.getName());
         */
        //Get target type.
        Class<T> targetType = getType();
        //Create a request class based on the extension.
        Request<?> request = HttpSdkSupport.invokeCreateRequest(method, args);
        //Dynamically customize request parameters.
        for (HandlerPostProcessor postProcessor : postProcessors) {
            request = postProcessor.postProcessRequestBeforeHandle(request, targetType,
                    method, args);
        }
        //Execute the request.
        Object result = HttpSdkSupport.getResponse(method, doRequest(request));
        //Dynamic customization of request response results.
        for (HandlerPostProcessor postProcessor : postProcessors) {
            result = postProcessor.postProcessResultAfterHandle(result, targetType,
                    method, args);
        }
        return result;
    }

    /**
     * Return relevant information about this agent.
     *
     * @return relevant information about this agent.
     * @see #TO_STR_FORMAT
     */
    @Override
    public String toString() {
        return String.format(TO_STR_FORMAT, getType().getName(),
                getClass().getName(), getHost(), getProxyModel().name());
    }

    /**
     * Use {@link top.osjf.sdk.core.client.Client} for the
     * next name routing operation.
     *
     * @param request Think of {@link Request#getClientCls()}.
     * @return The result set of this request is encapsulated in {@link Response}.
     */
    private Response doRequest(@NonNull Request<?> request) {
        //private perm
        //change protected
        //son class can use
        return ClientExecutors.executeRequestClient(this::getHost, request);
    }
}
