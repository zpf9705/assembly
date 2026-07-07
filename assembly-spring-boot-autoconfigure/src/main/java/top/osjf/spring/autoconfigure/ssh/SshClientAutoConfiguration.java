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

package top.osjf.spring.autoconfigure.ssh;

import org.apache.sshd.client.ClientBuilder;
import org.apache.sshd.client.SshClient;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * {@link org.springframework.boot.autoconfigure.EnableAutoConfiguration} for Apache ssh {@link SshClient}.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.2
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({SshClient.class})
@EnableConfigurationProperties(SshClientProperties.class)
public class SshClientAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public SshClient sshClient(ClientBuilder clientBuilder, SshClientProperties properties) {
        return clientBuilder.build(properties.isFillWithDefaultValues());
    }

    @Bean
    public ClientBuilder clientBuilder(ObjectProvider<SshClientBuilderCustomizer> provider) {
        ClientBuilder builder = ClientBuilder.builder();
        provider.orderedStream().forEach(customizer -> customizer.customize(builder));
        return builder;
    }

    @Bean
    public SshClientLifecycle sshClientLifecycle(SshClient sshClient) {
        return new SshClientLifecycle(sshClient);
    }
}
