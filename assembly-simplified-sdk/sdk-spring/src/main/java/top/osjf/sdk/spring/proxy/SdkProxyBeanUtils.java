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

import org.springframework.aop.scope.ScopedProxyUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import top.osjf.sdk.core.util.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The registration tool class for SDK proxy beans.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
public abstract class SdkProxyBeanUtils {

    /***A collection of regular bean scopes.*/
    public static final List<String> ROUTINE_SCOPES = Stream.of(BeanDefinition.SCOPE_SINGLETON,
            BeanDefinition.SCOPE_PROTOTYPE, AbstractBeanDefinition.SCOPE_DEFAULT).collect(Collectors.toList());

    /*** The name suffix of the SDK proxy bean.*/
    public static final String BEAN_NAME_SUFFIX = "@sdk.proxy.bean";

    /**
     * When dynamically registering a bean, it is classified and registered based on
     * the scope set by the registered bean. If the current scope is within the
     * {@link #ROUTINE_SCOPES} range, annotation creation will be performed for
     * {@link BeanDefinitionHolder}. If it is a special scope outside of this range,
     * a proxy bean will be created first to meet the special scope.
     *
     * @param definition the builder of {@link BeanDefinitionBuilder}.
     * @param beanName   the name of the proxy bean.
     * @param alisa      the alias set of proxy beans.
     * @param registry   the bean definition registry.
     * @return The information registration body of {@link BeanDefinition}.
     * @see ScopedProxyUtils#createScopedProxy(BeanDefinitionHolder, BeanDefinitionRegistry, boolean)
     */
    public static BeanDefinitionHolder createBeanDefinitionHolderDistinguishScope(BeanDefinition definition,
                                                                                  String beanName,
                                                                                  String[] alisa,
                                                                                  BeanDefinitionRegistry registry) {
        BeanDefinitionHolder holder = new BeanDefinitionHolder(definition, beanName, alisa);
        if (ROUTINE_SCOPES.stream().anyMatch(sc -> Objects.equals(sc, definition.getScope()))) {
            return holder;
        }
        return ScopedProxyUtils.createScopedProxy(holder, registry, true);
    }

    /**
     * When no bean name is provided for the SDK proxy bean,
     * this method is used as an alternative.
     *
     * @param beanName  the defined bean name.
     * @param className the fully qualified name of the target class.
     * @return The name of the proxy bean.
     */
    public static String getTargetBeanName(String beanName, String className) {
        if (StringUtils.isNotBlank(beanName)) {
            return beanName + BEAN_NAME_SUFFIX;
        }
        if (StringUtils.isBlank(className))
            throw new IllegalArgumentException("When 'beanName' is not provided, please ensure that " +
                    "'className' is built as the default name and cannot be empty.");
        return className + BEAN_NAME_SUFFIX;
    }
}