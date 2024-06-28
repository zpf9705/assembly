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

package top.osjf.assembly.simplified.service.annotation;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportAware;
import org.springframework.context.annotation.Role;
import org.springframework.core.type.AnnotationMetadata;
import top.osjf.assembly.simplified.service.ServiceContextUtils;
import top.osjf.assembly.simplified.service.context.ClassesServiceContext;
import top.osjf.assembly.simplified.service.context.ServiceContext;
import top.osjf.assembly.simplified.service.context.SimpleServiceContext;
import top.osjf.assembly.simplified.support.MappedAnnotationAttributes;
import top.osjf.assembly.util.annotation.NotNull;

import java.util.Objects;

/**
 * Select the processor for service context configuration.
 * <ul>
 *     <li>{@link ClassesServiceContext}
 *     <pre>&#064;EnableServiceCollection2@type=SIMPLE.</pre></li>
 *     <li>{@link SimpleServiceContext}
 *     <pre>&#064;EnableServiceCollection2@type=CLASSES.</pre></li>
 * </ul>
 *
 * @see EnableServiceCollection2
 * @author zpf
 * @since 2.0.6
 */
@Configuration(proxyBeanMethods = false)
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
public class ServiceContextSelectorConfiguration extends AbstractServiceCollectionConfiguration implements ImportAware {

    /**
     * The initialization object set when the type is simple is to call an
     * empty construct to add it to the collection of {@link BeanPostProcessor},
     * collect service objects, and then hand them over to the spring container for management.
     */
    private SimpleServiceContext context;

    private Type type;

    @Override
    public void setImportMetadata(@NotNull AnnotationMetadata metadata) {
        MappedAnnotationAttributes attributes = MappedAnnotationAttributes.of(metadata
                .getAnnotationAttributes(EnableServiceCollection2.class.getCanonicalName()));
        type = attributes.getEnum("type");
        if (Objects.equals(type, Type.SIMPLE)) {
            //Initialize the bean listener.
            context = new SimpleServiceContext();
        }
    }

    @Bean(ServiceContextUtils.SERVICE_CONTEXT_NAME)
    public ServiceContext serviceContext() {
        return ServiceContextUtils.newOrDefault(type, context);
    }
}
