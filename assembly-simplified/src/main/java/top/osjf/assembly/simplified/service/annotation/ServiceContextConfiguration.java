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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;
import top.osjf.assembly.simplified.service.context.ClassesServiceContext;
import top.osjf.assembly.simplified.service.context.ServiceContext;
import top.osjf.assembly.simplified.service.ServiceContextUtils;

/**
 * Service context configuration class.
 *
 * <p>This configuration class works inside the container and is not related to usage.
 * <p>The annotation {@link Role} is {@link BeanDefinition#ROLE_INFRASTRUCTURE}.
 * <p>For details, please refer to the specific annotations mentioned earlier.
 *
 * <p>Configure a {@link ServiceContext} container singleton object for {@link ClassesServiceContext},
 * which can be used by users.</p>
 *
 * <p>Apply to {@link top.osjf.assembly.simplified.service.annotation.EnableServiceCollection}.
 *
 * @author zpf
 * @since 2.0.4
 */
@Configuration(proxyBeanMethods = false)
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
public class ServiceContextConfiguration extends AbstractServiceCollectionConfiguration {

    @Bean(ServiceContextUtils.CONFIG_BEAN_NAME)
    public ServiceContext serviceContext() {
        return new ClassesServiceContext();
    }
}
