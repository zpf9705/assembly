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


package top.osjf.spring.autoconfigure.idempotent;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.util.Assert;
import top.osjf.optimize.idempotent.annotation.EnableIdempotent;
import top.osjf.optimize.idempotent.annotation.Idempotent;
import top.osjf.optimize.idempotent.aspectj.IdempotentMethodAspect;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for {@code Idempotent}.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.4
 */
@ConditionalOnClass(Idempotent.class)
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(IdempotentProperties.class)
@EnableIdempotent
@Import(IdempotentAutoConfiguration.IdempotentGlobalConfigInitializing.class)
public class IdempotentAutoConfiguration {

    /**
     * Bean used to Initialize setting idempotent global configuration that a {@link IdempotentMethodAspect}
     * exists and provide a more meaningful exception.
     */
    static class IdempotentGlobalConfigInitializing implements InitializingBean {

        private final ObjectProvider<IdempotentMethodAspect> provider;
        private final IdempotentProperties idempotentProperties;

        public IdempotentGlobalConfigInitializing(ObjectProvider<IdempotentMethodAspect> provider,
                                                  IdempotentProperties idempotentProperties) {
            this.provider = provider;
            this.idempotentProperties = idempotentProperties;
        }

        @Override
        public void afterPropertiesSet() {
            IdempotentMethodAspect aspect = provider.getIfAvailable();
            Assert.notNull(aspect,"No IdempotentMethodAspect could be auto-configured");
            aspect.setGlobalConfiguration(idempotentProperties.getGlobal());
        }
    }
}
