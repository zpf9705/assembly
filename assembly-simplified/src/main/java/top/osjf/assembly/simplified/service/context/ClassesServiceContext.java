/*
 * Copyright 2023-2024 the original author or authors.
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

package top.osjf.assembly.simplified.service.context;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import top.osjf.assembly.simplified.service.ServiceContextUtils;
import top.osjf.assembly.simplified.service.annotation.EnableServiceCollection;
import top.osjf.assembly.simplified.service.annotation.EnableServiceCollection2;
import top.osjf.assembly.simplified.service.annotation.ServiceCollection;
import top.osjf.assembly.util.io.ScanUtils;
import top.osjf.assembly.util.lang.ArrayUtils;
import top.osjf.assembly.util.lang.CollectionUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Support implementation class for service context {@link ServiceContext}.
 *
 * <p>The implementation process is as follows.
 * <p>Register a bean with the name {@link ServiceContextUtils#CONFIG_BEAN_NAME} first, and after
 * the container is refreshed, notify {@link #onApplicationEvent(ContextRefreshedEvent)}
 * for unified service collection.
 * <p> The collected path is the springboot startup class.
 *
 * <p>After completion, the collection service can be refreshed and dynamically
 * transformed by dynamically passing in the path through {@link #reloadWithScanPackages(String...)}.
 *
 * <p>The configuration to trigger this type can be selected as {@link EnableServiceCollection}
 * or {@link EnableServiceCollection2}.<pre>&#064;EnableServiceCollection2@type=CLASSES</pre>
 *
 * @author zpf
 * @since 2.0.4
 */
public class ClassesServiceContext extends AbstractServiceContext {

    private String[] scanPackages;

    /**
     * Set the scanning path.
     *
     * @param packages Scanning packages , must not be {@literal null}.
     */
    private void setScanPackages(String... packages) {
        Objects.requireNonNull(packages, "Setting scanPackages no be null");
        this.scanPackages = packages;
    }

    /**
     * Add service parameters.
     *
     * @param contextMap Service name map , must not be {@literal null}.
     */
    public void addContextMap(Map<String, Object> contextMap) {
        Objects.requireNonNull(contextMap, "Setting contextMap no be null");
        getContextMap().putAll(contextMap);
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        super.onApplicationEvent(event);
        setScanPackages(getMainApplicationPackage());
        load(this);
        clearCache();
    }

    @Override
    public void reloadWithScanPackages(String... packages) {
        close();
        setScanPackages(packages);
        load(this);
    }

    @Override
    public void close() {
        super.close();
        this.scanPackages = null;
    }

    private void load(ClassesServiceContext contextBean) {
        Set<Class<Object>> serviceClasses = ScanUtils
                .getTypesAnnotatedWith(ServiceCollection.class, contextBean.scanPackages);
        if (CollectionUtils.isEmpty(serviceClasses)) {
            return;
        }
        ApplicationContext context = getApplicationContext();
        BeanDefinitionRegistry registry = getBeanDefinitionRegistry();
        for (Class<Object> serviceClass : serviceClasses) {
            Map<String, Object> beansMap;
            try {
                beansMap = context.getBeansOfType(serviceClass);
            } catch (BeansException ignored) {
                continue;
            }
            contextBean.addContextMap(beansMap);
        }
        Map<String, Object> temporary = new HashMap<>();
        Map<String, Object> contextMap = contextBean.getContextMap();
        if (CollectionUtils.isNotEmpty(contextMap)) {
            contextMap.forEach((name, bean) -> {
                String[] aliases = registry.getAliases(name);
                if (ArrayUtils.isNotEmpty(aliases)) {
                    for (String alias : aliases) {
                        temporary.putIfAbsent(alias, bean);
                    }
                }
            });
            contextMap.putAll(temporary);
        }
    }
}
