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
import org.springframework.core.env.Environment;
import org.springframework.util.CollectionUtils;
import top.osjf.cron.core.lang.Nullable;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

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
    public SshClient sshClient(ClientBuilder clientBuilder,
                               Environment environment) {
        return clientBuilder.build(environment.getProperty("ssh-client.builder.is-fill-with-default-values",
                boolean.class, true));
    }

    @Bean
    @ConditionalOnMissingBean
    public ClientBuilder clientBuilder(
            ObjectProvider<ServerKeyVerifier> ServerKeyVerifierObjectProvider,
            ObjectProvider<HostConfigEntryResolver> hostConfigEntryResolverObjectProvider,
            ObjectProvider<ClientIdentityLoader> clientIdentityLoaderObjectProvider,
            ObjectProvider<FilePasswordProvider> filePasswordProviderObjectProvider,
            ObjectProvider<Factory<SshClient>> sshClientFactoryObjectProvider,
            ObjectProvider<KeyExchangeFactory> keyExchangeFactoryObjectProvider,
            ObjectProvider<KexExtensionHandler> kexExtensionHandlerObjectProvider,
            ObjectProvider<NamedFactory<Signature>> signatureNamedFactoryObjectProvider,
            ObjectProvider<Factory<Random>> randomFactoryObjectProvider,
            ObjectProvider<NamedFactory<Cipher>> cipherNamedFactoryObjectProvider,
            ObjectProvider<NamedFactory<Compression>> compressionNamedFactoryObjectProvider,
            ObjectProvider<NamedFactory<Mac>> macNamedFactoryObjectProvider,
            ObjectProvider<ChannelFactory> channelFactoryObjectProvider,
            ObjectProvider<FileSystemFactory> fileSystemFactoryObjectProvider,
            ObjectProvider<ForwardingFilter> forwardingFilterObjectProvider,
            ObjectProvider<ForwarderFactory> forwarderFactoryObjectProvider,
            ObjectProvider<RequestHandler<ConnectionService>> connectionServiceRequestHandlerObjectProvider,
            ObjectProvider<ChannelStreamWriterResolverSupplier> ChannelStreamWriterResolverSupplierObjectProvider,
            ObjectProvider<UnknownChannelReferenceHandler> unknownChannelReferenceHandlerObjectProvider,
            ObjectProvider<SshClientBuilderCustomizer> sshClientBuilderCustomizerObjectProvider) {

        ClientBuilder builder = ClientBuilder.builder();

        ServerKeyVerifier serverKeyVerifier = orderedStreamFirst(ServerKeyVerifierObjectProvider);
        if (serverKeyVerifier != null) builder.serverKeyVerifier(serverKeyVerifier);

        HostConfigEntryResolver hostConfigEntryResolver = orderedStreamFirst(hostConfigEntryResolverObjectProvider);
        if (hostConfigEntryResolver != null) builder.hostConfigEntryResolver(hostConfigEntryResolver);

        ClientIdentityLoader clientIdentityLoader = orderedStreamFirst(clientIdentityLoaderObjectProvider);
        if (clientIdentityLoader != null) builder.clientIdentityLoader(clientIdentityLoader);

        FilePasswordProvider filePasswordProvider = orderedStreamFirst(filePasswordProviderObjectProvider);
        if (filePasswordProvider != null) builder.filePasswordProvider(filePasswordProvider);

        Factory<SshClient> sshClientFactory = orderedStreamFirst(sshClientFactoryObjectProvider);
        if (sshClientFactory != null) builder.factory(sshClientFactory);

        List<KeyExchangeFactory> keyExchangeFactories = orderedStreamList(keyExchangeFactoryObjectProvider);
        if (!CollectionUtils.isEmpty(keyExchangeFactories)) builder.keyExchangeFactories(keyExchangeFactories);

        KexExtensionHandler kexExtensionHandler = orderedStreamFirst(kexExtensionHandlerObjectProvider);
        if (kexExtensionHandler != null) builder.kexExtensionHandler(kexExtensionHandler);

        List<NamedFactory<Signature>> signatureNamedFactories = orderedStreamList(signatureNamedFactoryObjectProvider);
        if (!CollectionUtils.isEmpty(signatureNamedFactories)) builder.signatureFactories(signatureNamedFactories);

        Factory<Random> randomFactory = orderedStreamFirst(randomFactoryObjectProvider);
        if (randomFactory != null) builder.randomFactory(randomFactory);

        List<NamedFactory<Cipher>> cipherNamedFactories = orderedStreamList(cipherNamedFactoryObjectProvider);
        if (!CollectionUtils.isEmpty(cipherNamedFactories)) builder.cipherFactories(cipherNamedFactories);

        List<NamedFactory<Compression>> compressionNamedFactories = orderedStreamList(compressionNamedFactoryObjectProvider);
        if (!CollectionUtils.isEmpty(compressionNamedFactories))
            builder.compressionFactories(compressionNamedFactories);

        List<NamedFactory<Mac>> macNamedFactories = orderedStreamList(macNamedFactoryObjectProvider);
        if (!CollectionUtils.isEmpty(macNamedFactories)) builder.macFactories(macNamedFactories);

        List<ChannelFactory> channelFactories = orderedStreamList(channelFactoryObjectProvider);
        if (!CollectionUtils.isEmpty(channelFactories)) builder.channelFactories(channelFactories);

        FileSystemFactory fileSystemFactory = orderedStreamFirst(fileSystemFactoryObjectProvider);
        if (fileSystemFactory != null) builder.fileSystemFactory(fileSystemFactory);

        ForwardingFilter forwardingFilter = orderedStreamFirst(forwardingFilterObjectProvider);
        if (forwardingFilter != null) builder.forwardingFilter(forwardingFilter);

        ForwarderFactory forwarderFactory = orderedStreamFirst(forwarderFactoryObjectProvider);
        if (forwarderFactory != null) builder.forwarderFactory(forwarderFactory);

        List<RequestHandler<ConnectionService>> connectionServiceRequestHandlers = orderedStreamList
                (connectionServiceRequestHandlerObjectProvider);
        if (!CollectionUtils.isEmpty(connectionServiceRequestHandlers))
            builder.globalRequestHandlers(connectionServiceRequestHandlers);

        ChannelStreamWriterResolverSupplier writerResolverSupplier
                = orderedStreamFirst(ChannelStreamWriterResolverSupplierObjectProvider);
        if (writerResolverSupplier != null)
            builder.channelStreamPacketWriterResolver(writerResolverSupplier.get());

        UnknownChannelReferenceHandler referenceHandler = orderedStreamFirst(unknownChannelReferenceHandlerObjectProvider);
        if (referenceHandler != null) builder.unknownChannelReferenceHandler(referenceHandler);

        List<SshClientBuilderCustomizer> sshClientBuilderCustomizers = orderedStreamList
                (sshClientBuilderCustomizerObjectProvider);
        if (!CollectionUtils.isEmpty(sshClientBuilderCustomizers))
            sshClientBuilderCustomizers.forEach(c -> c.customize(builder));

        return builder;
    }

    @Bean
    @ConditionalOnMissingBean
    public SshClientLifecycle sshClientLifecycle(SshClient sshClient) {
        return new SshClientLifecycle(sshClient);
    }

    public interface ChannelStreamWriterResolverSupplier extends Supplier<ChannelStreamWriterResolver> { }

    @Nullable
    private <T> T orderedStreamFirst(ObjectProvider<T> provider) {
        List<T> beans = orderedStreamList(provider);
        if (CollectionUtils.isEmpty(beans)) {
            return null;
        }
        return beans.get(0);
    }

    private <T> List<T> orderedStreamList(ObjectProvider<T> provider) {
        return provider.orderedStream().collect(Collectors.toList());
    }
}
