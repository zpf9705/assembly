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
import org.apache.sshd.client.config.hosts.HostConfigEntryResolver;
import org.apache.sshd.client.config.keys.ClientIdentityLoader;
import org.apache.sshd.client.keyverifier.ServerKeyVerifier;
import org.apache.sshd.common.Factory;
import org.apache.sshd.common.NamedFactory;
import org.apache.sshd.common.channel.ChannelFactory;
import org.apache.sshd.common.channel.RequestHandler;
import org.apache.sshd.common.channel.throttle.ChannelStreamWriterResolver;
import org.apache.sshd.common.cipher.Cipher;
import org.apache.sshd.common.compression.Compression;
import org.apache.sshd.common.config.keys.FilePasswordProvider;
import org.apache.sshd.common.file.FileSystemFactory;
import org.apache.sshd.common.forward.ForwarderFactory;
import org.apache.sshd.common.kex.KeyExchangeFactory;
import org.apache.sshd.common.kex.extension.KexExtensionHandler;
import org.apache.sshd.common.mac.Mac;
import org.apache.sshd.common.random.Random;
import org.apache.sshd.common.session.ConnectionService;
import org.apache.sshd.common.session.UnknownChannelReferenceHandler;
import org.apache.sshd.common.signature.Signature;
import org.apache.sshd.server.forward.ForwardingFilter;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * {@link org.springframework.boot.autoconfigure.EnableAutoConfiguration} for Apache ssh {@link SshClient}.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.2
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({SshClient.class})
public class SshClientAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(SshClient.class)
    public SshClient sshClient(ClientBuilder clientBuilder) {
        return clientBuilder.build(true);
    }

    @Bean
    public SshClientLifecycle sshClientLifecycle(SshClient sshClient) {
        return new SshClientLifecycle(sshClient);
    }

    @Bean
    public ClientBuilder sshclientBuilder(
            //org.apache.sshd.client.SshClient
            ObjectProvider<List<ServerKeyVerifier>> serverKeyVerifiers,
            ObjectProvider<List<HostConfigEntryResolver>> hostConfigEntryResolvers,
            ObjectProvider<List<ClientIdentityLoader>> clientIdentityLoaders,
            ObjectProvider<List<FilePasswordProvider>> filePasswordProviders,
            //org.apache.sshd.common.BaseBuilder
            ObjectProvider<List<Factory<SshClient>>> sshClientFactories,
            ObjectProvider<List<KeyExchangeFactory>> keyExchangeFactories,
            ObjectProvider<List<KexExtensionHandler>> kexExtensionHandlers,
            ObjectProvider<List<NamedFactory<Signature>>> signatureNamedFactories,
            ObjectProvider<List<Factory<Random>>> randomFactories,
            ObjectProvider<List<NamedFactory<Cipher>>> cipherNamedFactories,
            ObjectProvider<List<NamedFactory<Compression>>> compressionNamedFactories,
            ObjectProvider<List<NamedFactory<Mac>>> macNamedFactories,
            ObjectProvider<List<ChannelFactory>> channelFactories,
            ObjectProvider<List<FileSystemFactory>> fileSystemFactories,
            ObjectProvider<List<ForwardingFilter>> forwardingFilters,
            ObjectProvider<List<ForwarderFactory>> forwarderFactories,
            ObjectProvider<List<RequestHandler<ConnectionService>>> connectionServiceRequestHandlers,
            ObjectProvider<List<ChannelStreamWriterResolver>> channelStreamWriterResolvers,
            ObjectProvider<List<UnknownChannelReferenceHandler>> unknownChannelReferenceHandlers,
            //SshClientBuilderCustomizer
            ObjectProvider<List<SshClientBuilderCustomizer>> sshClientBuilderCustomizes) {
        ClientBuilder builder = ClientBuilder.builder();

        ServerKeyVerifier serverKeyVerifier = orderedStreamFirst(serverKeyVerifiers);
        if (serverKeyVerifier != null) {
            builder.serverKeyVerifier(serverKeyVerifier);
        }

        HostConfigEntryResolver hostConfigEntryResolver = orderedStreamFirst(hostConfigEntryResolvers);
        if (hostConfigEntryResolver != null) {
            builder.hostConfigEntryResolver(hostConfigEntryResolver);
        }

        ClientIdentityLoader clientIdentityLoader = orderedStreamFirst(clientIdentityLoaders);
        if (clientIdentityLoader != null) {
            builder.clientIdentityLoader(clientIdentityLoader);
        }

        FilePasswordProvider filePasswordProvider = orderedStreamFirst(filePasswordProviders);
        if (filePasswordProvider != null) {
            builder.filePasswordProvider(filePasswordProvider);
        }

        Factory<SshClient> sshClientFactory = orderedStreamFirst(sshClientFactories);
        if (sshClientFactory != null) {
            builder.factory(sshClientFactory);
        }

        List<KeyExchangeFactory> keyExchangeFactories0 = orderedStreamList(keyExchangeFactories);
        if (!CollectionUtils.isEmpty(keyExchangeFactories0)) {
            builder.keyExchangeFactories(keyExchangeFactories0);
        }

        KexExtensionHandler kexExtensionHandler = orderedStreamFirst(kexExtensionHandlers);
        if (kexExtensionHandler != null) {
            builder.kexExtensionHandler(kexExtensionHandler);
        }

        List<NamedFactory<Signature>> signatureNamedFactories0 = orderedStreamList(signatureNamedFactories);
        if (!CollectionUtils.isEmpty(signatureNamedFactories0)) {
            builder.signatureFactories(signatureNamedFactories0);
        }

        Factory<Random> randomFactory = orderedStreamFirst(randomFactories);
        if (randomFactory != null) {
            builder.randomFactory(randomFactory);
        }

        List<NamedFactory<Cipher>> cipherNamedFactories0 = orderedStreamList(cipherNamedFactories);
        if (!CollectionUtils.isEmpty(cipherNamedFactories0)) {
            builder.cipherFactories(cipherNamedFactories0);
        }

        List<NamedFactory<Compression>> compressionNamedFactories0 = orderedStreamList(compressionNamedFactories);
        if (!CollectionUtils.isEmpty(compressionNamedFactories0)) {
            builder.compressionFactories(compressionNamedFactories0);
        }

        List<NamedFactory<Mac>> macNamedFactories0 = orderedStreamList(macNamedFactories);
        if (!CollectionUtils.isEmpty(macNamedFactories0)) {
            builder.macFactories(macNamedFactories0);
        }

        List<ChannelFactory> channelFactories0 = orderedStreamList(channelFactories);
        if (!CollectionUtils.isEmpty(channelFactories0)) {
            builder.channelFactories(channelFactories0);
        }

        FileSystemFactory fileSystemFactory = orderedStreamFirst(fileSystemFactories);
        if (fileSystemFactory != null) {
            builder.fileSystemFactory(fileSystemFactory);
        }

        ForwardingFilter forwardingFilter = orderedStreamFirst(forwardingFilters);
        if (forwardingFilter != null) {
            builder.forwardingFilter(forwardingFilter);
        }

        ForwarderFactory forwarderFactory = orderedStreamFirst(forwarderFactories);
        if (forwarderFactory != null) {
            builder.forwarderFactory(forwarderFactory);
        }

        List<RequestHandler<ConnectionService>> connectionServiceRequestHandlers0
                = orderedStreamList(connectionServiceRequestHandlers);
        if (!CollectionUtils.isEmpty(connectionServiceRequestHandlers0)) {
            builder.globalRequestHandlers(connectionServiceRequestHandlers0);
        }

        ChannelStreamWriterResolver writerResolver = orderedStreamFirst(channelStreamWriterResolvers);
        if (writerResolver != null) {
            builder.channelStreamPacketWriterResolver(writerResolver);
        }

        UnknownChannelReferenceHandler referenceHandler = orderedStreamFirst(unknownChannelReferenceHandlers);
        if (referenceHandler != null) {
            builder.unknownChannelReferenceHandler(referenceHandler);
        }

        List<SshClientBuilderCustomizer> sshClientBuilderCustomizers0 = orderedStreamList(sshClientBuilderCustomizes);
        if (!CollectionUtils.isEmpty(sshClientBuilderCustomizers0)) {
            sshClientBuilderCustomizers0.forEach(c -> c.customize(builder));
        }

        return builder;
    }

    private <T> List<T> orderedStreamList(ObjectProvider<List<T>> provider) {
        return provider.orderedStream()
                .findFirst()
                .orElse(null);
    }

    private <T> T orderedStreamFirst(ObjectProvider<List<T>> provider) {
        List<T> beans = orderedStreamList(provider);
        if (CollectionUtils.isEmpty(beans)) {
            return null;
        }
        return beans.get(0);
    }
}
