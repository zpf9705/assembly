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

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import top.osjf.sdk.core.lang.NotNull;
import top.osjf.sdk.core.lang.Nullable;
import top.osjf.sdk.core.util.ArrayUtils;
import top.osjf.sdk.core.util.internal.logging.InternalLogger;
import top.osjf.sdk.core.util.internal.logging.InternalLoggerFactory;
import top.osjf.sdk.proxy.ComprehensiveDelegationCallback;
import top.osjf.sdk.proxy.HandlerPostProcessor;
import top.osjf.sdk.proxy.ProxyModel;
import top.osjf.sdk.spring.SpringRequestCaller;
import top.osjf.sdk.spring.annotation.Sdk;
import top.osjf.sdk.spring.beans.DeterminantDisposableBean;
import top.osjf.sdk.spring.beans.DeterminantInitializingBean;
import top.osjf.sdk.spring.beans.DeterminantType;
import top.osjf.sdk.spring.runner.SdkExpressRunner;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * The SDK proxy factory Bean class is used to create SDK proxy
 * objects of a specified type.
 *
 * <p>The main purpose of this class is to create and manage SDK
 * proxy objects in the Spring container to support flexible calling
 * and extension of SDKs.
 *
 * <p>Each SDK proxy bean holds a proxy object creation policy model
 * {@link ProxyModel}, which is used to create a proxy object after
 * obtaining the proxy type and hand it over to the Spring container
 * management call. Its callback will be processed uniformly in the
 * {@link #callback} method of the parent class.
 *
 * <p>Due to the fact that the creation of this proxy bean belongs
 * to an internal class, it is not possible to manipulate the
 * initialization and destruction callbacks during the bean
 * creation process. Here, {@code DeterminantInitializingBean} and
 * {@code DeterminantDisposableBean} of specified types are provided
 * to assist in completing possible custom execution actions for the
 * proxy bean lifecycle. Simply create the corresponding spring bean
 * and specify {@link DeterminantType#getType()} as the current proxy
 * type to customize the lifecycle operations.
 *
 * <p>Support enhanced operations before and after SDK requests, that
 * is, automatically injecting {@link HandlerPostProcessor} interface
 * beans defined by developers, and enhancing the functionality before
 * and after calling the SDK callback method {@link #callback}.
 *
 * <p>Version 1.0.3 added a specification for the SDK proxy generation
 * environment, which is annotated with {@link Sdk#profile()} to compare
 * whether the current Spring's {@link Environment#getActiveProfiles()}
 * meets the scope of its activation environment. This is not equivalent
 * to distinguishing the generation environment of each bean, but rather
 * throwing {@link ProfileMismatchException} errors during callback runtime,
 * as shown in method {@link SdkProxyFactoryBean#callback}.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.2
 */
public class SdkProxyFactoryBean
        extends ComprehensiveDelegationCallback
        implements FactoryBean<Object>, EnvironmentAware, InitializingBean, DisposableBean {

    private final InternalLogger LOG = InternalLoggerFactory.getInstance(getClass());

    /**
     * The type of SDK proxy interface or abstract class.
     */
    private final Class<?> type;

    /**
     * Specify the proxy object created by the proxy model {@code ProxyModel}.
     */
    private Object proxy;

    /**
     * Enumeration of selected proxy models.
     */
    private ProxyModel proxyModel;

    /**
     * A specified SDK proxy type {@link InitializingBean}.
     */
    private DeterminantInitializingBean initializingBean;

    /**
     * A specified SDK proxy type {@link DisposableBean}.
     */
    private DeterminantDisposableBean disposableBean;

    /**
     * Adapt this {@code FactoryBean} to generate an array of environment
     * identifiers for available proxy objects.
     * @since 1.0.3
     */
    private String[] profiles;

    private Environment environment;

    /**
     * Specify whether the environment generated by this proxy bean
     * matches the activation environment?
     * @since 1.0.3
     */
    private boolean matchProfiles;

    /**
     * @see 1.0.3
     */
    private SdkExpressRunner sdkExpressRunner;

    /**
     * Construct a {@code SdkProxyFactoryBean} instance to create
     * a proxy Bean of the specified type.
     *
     * @param type the target type of the proxy bean.
     * @throws NullPointerException if input type is null.
     */
    public SdkProxyFactoryBean(@NotNull Class<?> type) {
        Objects.requireNonNull(type, "type == null");
        this.type = type;
    }

    /**
     * Set a {@code SpringRequestCaller} for this factory bean.
     *
     * @param requestCaller a {@code SpringRequestCaller}.
     */
    public void setRequestCaller(SpringRequestCaller requestCaller) {
        super.setRequestCaller(requestCaller);
    }

    /**
     * Set a {@code ProxyModel} for this factory bean.
     *
     * @param proxyModel a {@code ProxyModel}.
     */
    public void setProxyModel(ProxyModel proxyModel) {
        this.proxyModel = proxyModel;
    }

    /**
     * {@inheritDoc}
     *
     * <p>This rewriting method achieves automatic assembly through {@link Autowired}
     * annotation, finding the {@code HandlerPostProcessor} bean list defined in
     * the Spring container.
     *
     * <p><strong>Note:</strong></p>
     * If a bean depends on this bean {@code SdkProxyFactoryBean} and implements
     * {@code HandlerPostProcessor}, it is also a {@code HandlerPostProcessor} bean, which
     * will result in circular dependencies and program errors. It is recommended to
     * separate the logic extraction and processing.
     *
     * @param postProcessors The list of post-processors in Spring container.
     */
    @Override
    @Autowired(required = false)
    public void setPostProcessors(List<HandlerPostProcessor> postProcessors) {
        AnnotationAwareOrderComparator.sort(postProcessors);
        super.setPostProcessors(postProcessors);
    }

    /**
     * This method achieves automatic assembly through {@link Autowired} annotation,
     * finding the {@code DeterminantInitializingBean} bean list defined in the Spring
     * container.
     *
     * <p><strong>Note:</strong></p>
     * If a bean depends on this bean {@code SdkProxyFactoryBean} and implements
     * {@code DeterminantInitializingBean}, it is also a {@code DeterminantInitializingBean}
     * bean, which will result in circular dependencies and program errors. It is recommended to
     * separate the logic extraction and processing.
     *
     * @param initializingBeans The list of {@code DeterminantInitializingBean} in
     *                          Spring container.
     */
    @Autowired(required = false)
    public void setDeterminantInitializingBeans(List<DeterminantInitializingBean> initializingBeans) {
        this.initializingBean = getDeterminantPriority(initializingBeans);
    }

    /**
     * This method achieves automatic assembly through {@link Autowired} annotation,
     * finding the {@code DeterminantDisposableBean} bean list defined in the Spring
     * container.
     *
     * <p><strong>Note:</strong></p>
     * If a bean depends on this bean {@code SdkProxyFactoryBean} and implements
     * {@code DeterminantDisposableBean}, it is also a {@code DeterminantDisposableBean}
     * bean, which will result in circular dependencies and program errors. It is recommended to
     * separate the logic extraction and processing.
     *
     * @param disposableBeans The list of {@code DeterminantDisposableBean} in
     *                        Spring container.
     */
    @Autowired(required = false)
    public void setDeterminantDisposableBeans(List<DeterminantDisposableBean> disposableBeans) {
        this.disposableBean = getDeterminantPriority(disposableBeans);
    }

    /**
     * Set the current {@code FactoryBean} to allow the generation of an environment identity
     * array of available proxy beans for the proxy class.
     *
     * @param profiles an environment identity array.
     * @since 1.0.3
     */
    public void setProfiles(String[] profiles) {
        this.profiles = profiles;
    }

    /**
     * Set a {@code SdkExpressRunner} for this factory bean.
     *
     * @param sdkExpressRunner a {@code SdkExpressRunner}.
     * @since 1.0.3
     */
    public void setSdkExpressRunner(SdkExpressRunner sdkExpressRunner) {
        this.sdkExpressRunner = sdkExpressRunner;
    }

    /**
     * Return the bean with the highest {@link Order} priority
     * after filtering and sorting the subclass beans of {@code DeterminantType}.
     *
     * @param beans bean list to filter and sort.
     * @param <R>   the type of bean list.
     * @return bean list after filter and sort.
     */
    protected <R extends DeterminantType> R getDeterminantPriority(List<R> beans) {
        beans = beans.stream().filter(b -> Objects.equals(b.getType(), type)).collect(Collectors.toList());
        AnnotationAwareOrderComparator.sort(beans);
        return !beans.isEmpty() ? beans.get(0) : null;
    }

    @Override
    public void setEnvironment(@NotNull Environment environment) {
        this.environment = environment;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Using the proxy model, create a proxy object with the
     * proxy type {@link #type} and the current callback type
     * as the return object for this factory bean.
     *
     * @return {@inheritDoc}
     * @throws Exception {@inheritDoc}
     */
    @Nullable
    @Override
    public Object getObject() throws Exception {
        if (proxy != null) {
            return proxy;
        }
        try {
            proxy = proxyModel.newProxy(type, this);
        }
        catch (Exception e) {
            LOG.error("Failed to create proxy object of type {} through the proxy model of {}," +
                            "detailed error message is: {}",
                    type, proxyModel.name(), e.getMessage());
            throw e;
        }
        catch (Throwable e) {
            LOG.error("Failed to create proxy object of type {} through the proxy model of {}," +
                            "detailed error message is: {}",
                    type, proxyModel.name(), e.getMessage());
            throw new Exception(e);
        }
        //Add instruction call set.
        sdkExpressRunner.addFunction(type, proxy);
        return proxy;
    }

    /**
     * {@inheritDoc}
     * <p>
     * The returned type should be consistent with the
     * type of the proxy object created.
     *
     * @return {@inheritDoc}
     */
    @Nullable
    @Override
    public Class<?> getObjectType() {
        return type;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Execute {@code DeterminantInitializingBean} list for Spring {@link InitializingBean}.
     *
     * @throws Exception {@inheritDoc}
     */
    @Override
    public void afterPropertiesSet() throws Exception {

        //Does it match the specified environment.
        if (ArrayUtils.isNotEmpty(profiles)) {
            matchProfiles = environment.acceptsProfiles(Profiles.of(profiles));
        } else matchProfiles = true;

        if (initializingBean != null) {
            initializingBean.afterPropertiesSet();
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * Execute {@code DeterminantDisposableBean} list for Spring {@link DisposableBean}.
     *
     * @throws Exception {@inheritDoc}
     */
    @Override
    public void destroy() throws Exception {
        if (disposableBean != null) {
            disposableBean.destroy();
        }
    }

    /**
     * {@inheritDoc}
     * @since 1.0.3
     */
    @Override
    public Object callback(Method method, Object[] args, PeculiarProxyVariable variable) throws Throwable {
        if (!matchProfiles) {
            throw new ProfileMismatchException(String.format("Environment mismatch. Required: [%s], Actual: [%s]",
                    String.join(",", profiles),
                    String.join(",", environment.getActiveProfiles())));
        }
        return super.callback(method, args, variable);
    }

    @Override
    public String toString() {
        return String.format("Proxy info ( target type [%s] | proxy type [%s] | host [%s] | proxy model [%s] )",
                type.getName(), getClass().getName(), getHost(), proxyModel.name());
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, getHost(), proxyModel);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SdkProxyFactoryBean that = (SdkProxyFactoryBean) o;
        return Objects.equals(type, that.type)
                && Objects.equals(getHost(), that.getHost())
                && proxyModel == that.proxyModel;
    }
}
