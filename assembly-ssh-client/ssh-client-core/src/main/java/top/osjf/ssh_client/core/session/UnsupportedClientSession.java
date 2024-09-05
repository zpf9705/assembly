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

package top.osjf.ssh_client.core.session;

import org.apache.sshd.client.ClientFactoryManager;
import org.apache.sshd.client.auth.AuthenticationIdentitiesProvider;
import org.apache.sshd.client.auth.UserAuthFactory;
import org.apache.sshd.client.auth.hostbased.HostBasedAuthenticationReporter;
import org.apache.sshd.client.auth.keyboard.UserInteraction;
import org.apache.sshd.client.auth.password.PasswordAuthenticationReporter;
import org.apache.sshd.client.auth.password.PasswordIdentityProvider;
import org.apache.sshd.client.auth.pubkey.PublicKeyAuthenticationReporter;
import org.apache.sshd.client.channel.*;
import org.apache.sshd.client.future.AuthFuture;
import org.apache.sshd.client.keyverifier.ServerKeyVerifier;
import org.apache.sshd.client.session.ClientProxyConnector;
import org.apache.sshd.client.session.ClientSession;
import org.apache.sshd.common.AttributeRepository;
import org.apache.sshd.common.NamedFactory;
import org.apache.sshd.common.PropertyResolver;
import org.apache.sshd.common.Service;
import org.apache.sshd.common.channel.ChannelListener;
import org.apache.sshd.common.channel.PtyChannelConfigurationHolder;
import org.apache.sshd.common.channel.throttle.ChannelStreamWriterResolver;
import org.apache.sshd.common.cipher.Cipher;
import org.apache.sshd.common.cipher.CipherInformation;
import org.apache.sshd.common.compression.Compression;
import org.apache.sshd.common.compression.CompressionInformation;
import org.apache.sshd.common.forward.PortForwardingEventListener;
import org.apache.sshd.common.future.CloseFuture;
import org.apache.sshd.common.future.GlobalRequestFuture;
import org.apache.sshd.common.future.KeyExchangeFuture;
import org.apache.sshd.common.future.SshFutureListener;
import org.apache.sshd.common.io.IoSession;
import org.apache.sshd.common.io.IoWriteFuture;
import org.apache.sshd.common.kex.KexProposalOption;
import org.apache.sshd.common.kex.KexState;
import org.apache.sshd.common.kex.KeyExchange;
import org.apache.sshd.common.kex.KeyExchangeFactory;
import org.apache.sshd.common.kex.extension.KexExtensionHandler;
import org.apache.sshd.common.keyprovider.KeyIdentityProvider;
import org.apache.sshd.common.mac.Mac;
import org.apache.sshd.common.mac.MacInformation;
import org.apache.sshd.common.session.ReservedSessionMessagesHandler;
import org.apache.sshd.common.session.SessionDisconnectHandler;
import org.apache.sshd.common.session.SessionListener;
import org.apache.sshd.common.session.UnknownChannelReferenceHandler;
import org.apache.sshd.common.session.helpers.TimeoutIndicator;
import org.apache.sshd.common.signature.Signature;
import org.apache.sshd.common.util.buffer.Buffer;
import org.apache.sshd.common.util.net.SshdSocketAddress;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.charset.Charset;
import java.security.KeyPair;
import java.security.PublicKey;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * All methods are not supported {@link ClientSession}.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
public class UnsupportedClientSession implements ClientSession {

    @Override
    public SocketAddress getConnectAddress() {
        throw new UnsupportedOperationException();
    }

    @Override
    public AttributeRepository getConnectionContext() {
        throw new UnsupportedOperationException();
    }

    @Override
    public AuthFuture auth() throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public PublicKey getServerKey() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ClientChannel createChannel(String type) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public ClientChannel createChannel(String type, String subType) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public ChannelShell createShellChannel(PtyChannelConfigurationHolder ptyConfig, Map<String, ?> env) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public ChannelExec createExecChannel(String command, Charset charset, PtyChannelConfigurationHolder ptyConfig, Map<String, ?> env) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public ChannelExec createExecChannel(byte[] command, PtyChannelConfigurationHolder ptyConfig, Map<String, ?> env) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public ChannelSubsystem createSubsystemChannel(String subsystem) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public ChannelDirectTcpip createDirectTcpipChannel(SshdSocketAddress local, SshdSocketAddress remote) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<ClientSessionEvent> getSessionState() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<ClientSessionEvent> waitFor(Collection<ClientSessionEvent> mask, long timeout) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Map<Object, Object> getMetadataMap() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ClientFactoryManager getFactoryManager() {
        throw new UnsupportedOperationException();
    }

    @Override
    public KeyExchangeFuture switchToNoneCipher() throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public AuthenticationIdentitiesProvider getRegisteredIdentities() {
        throw new UnsupportedOperationException();
    }

    @Override
    public PasswordIdentityProvider getPasswordIdentityProvider() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setPasswordIdentityProvider(PasswordIdentityProvider provider) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addPasswordIdentity(String password) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String removePasswordIdentity(String password) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addPublicKeyIdentity(KeyPair key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public KeyPair removePublicKeyIdentity(KeyPair kp) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ServerKeyVerifier getServerKeyVerifier() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setServerKeyVerifier(ServerKeyVerifier serverKeyVerifier) {
        throw new UnsupportedOperationException();
    }

    @Override
    public UserInteraction getUserInteraction() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setUserInteraction(UserInteraction userInteraction) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PasswordAuthenticationReporter getPasswordAuthenticationReporter() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setPasswordAuthenticationReporter(PasswordAuthenticationReporter reporter) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PublicKeyAuthenticationReporter getPublicKeyAuthenticationReporter() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setPublicKeyAuthenticationReporter(PublicKeyAuthenticationReporter reporter) {
        throw new UnsupportedOperationException();
    }

    @Override
    public HostBasedAuthenticationReporter getHostBasedAuthenticationReporter() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setHostBasedAuthenticationReporter(HostBasedAuthenticationReporter reporter) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ClientProxyConnector getClientProxyConnector() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setClientProxyConnector(ClientProxyConnector proxyConnector) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<UserAuthFactory> getUserAuthFactories() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setUserAuthFactories(List<UserAuthFactory> userAuthFactories) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SshdSocketAddress startLocalPortForwarding(SshdSocketAddress local, SshdSocketAddress remote) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void stopLocalPortForwarding(SshdSocketAddress local) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public SshdSocketAddress startRemotePortForwarding(SshdSocketAddress remote, SshdSocketAddress local) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void stopRemotePortForwarding(SshdSocketAddress remote) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public SshdSocketAddress startDynamicPortForwarding(SshdSocketAddress local) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void stopDynamicPortForwarding(SshdSocketAddress local) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public KeyIdentityProvider getKeyIdentityProvider() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setKeyIdentityProvider(KeyIdentityProvider provider) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Buffer createBuffer(byte cmd, int estimatedSize) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Buffer prepareBuffer(byte cmd, Buffer buffer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public IoWriteFuture sendDebugMessage(boolean display, Object msg, String lang) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public IoWriteFuture sendIgnoreMessage(byte... data) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public IoWriteFuture writePacket(Buffer buffer) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public IoWriteFuture writePacket(Buffer buffer, long timeout, TimeUnit unit) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Buffer request(String request, Buffer buffer, long maxWaitMillis) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public GlobalRequestFuture request(Buffer buffer, String request, GlobalRequestFuture.ReplyHandler replyHandler) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void exceptionCaught(Throwable t) {
        throw new UnsupportedOperationException();
    }

    @Override
    public KeyExchangeFuture reExchangeKeys() throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T extends Service> T getService(Class<T> clazz) {
        throw new UnsupportedOperationException();
    }

    @Override
    public IoSession getIoSession() {
        throw new UnsupportedOperationException();
    }

    @Override
    public TimeoutIndicator getTimeoutStatus() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Duration getIdleTimeout() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Instant getIdleTimeoutStart() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Instant resetIdleTimeout() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Duration getAuthTimeout() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Instant getAuthTimeoutStart() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Instant resetAuthTimeout() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setAuthenticated() throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public KeyExchange getKex() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void disconnect(int reason, String msg) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void startService(String name, Buffer buffer) throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setUsername(String username) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addChannelListener(ChannelListener listener) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeChannelListener(ChannelListener listener) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ChannelListener getChannelListenerProxy() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ChannelStreamWriterResolver getChannelStreamWriterResolver() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setChannelStreamWriterResolver(ChannelStreamWriterResolver resolver) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addPortForwardingEventListener(PortForwardingEventListener listener) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removePortForwardingEventListener(PortForwardingEventListener listener) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PortForwardingEventListener getPortForwardingEventListenerProxy() {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<SshdSocketAddress> getStartedLocalPortForwards() {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<SshdSocketAddress> getBoundLocalPortForwards(int port) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Map.Entry<SshdSocketAddress, SshdSocketAddress>> getLocalForwardsBindings() {
        throw new UnsupportedOperationException();
    }

    @Override
    public NavigableSet<Integer> getStartedRemotePortForwards() {
        throw new UnsupportedOperationException();
    }

    @Override
    public SshdSocketAddress getBoundRemotePortForward(int port) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Map.Entry<Integer, SshdSocketAddress>> getRemoteForwardsBindings() {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<KeyExchangeFactory> getKeyExchangeFactories() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setKeyExchangeFactories(List<KeyExchangeFactory> keyExchangeFactories) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<NamedFactory<Cipher>> getCipherFactories() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setCipherFactories(List<NamedFactory<Cipher>> cipherFactories) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<NamedFactory<Compression>> getCompressionFactories() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setCompressionFactories(List<NamedFactory<Compression>> compressionFactories) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<NamedFactory<Mac>> getMacFactories() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setMacFactories(List<NamedFactory<Mac>> macFactories) {
        throw new UnsupportedOperationException();
    }

    @Override
    public KexExtensionHandler getKexExtensionHandler() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setKexExtensionHandler(KexExtensionHandler handler) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ReservedSessionMessagesHandler getReservedSessionMessagesHandler() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setReservedSessionMessagesHandler(ReservedSessionMessagesHandler handler) {
        throw new UnsupportedOperationException();
    }

    @Override
    public byte[] getSessionId() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isServerSession() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getClientVersion() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Map<KexProposalOption, String> getClientKexProposals() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getServerVersion() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Map<KexProposalOption, String> getServerKexProposals() {
        throw new UnsupportedOperationException();
    }

    @Override
    public KexState getKexState() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Map<KexProposalOption, String> getKexNegotiationResult() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getNegotiatedKexParameter(KexProposalOption paramType) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CipherInformation getCipherInformation(boolean incoming) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CompressionInformation getCompressionInformation(boolean incoming) {
        throw new UnsupportedOperationException();
    }

    @Override
    public MacInformation getMacInformation(boolean incoming) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isAuthenticated() {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T setAttribute(AttributeKey<T> key, T value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T removeAttribute(AttributeKey<T> key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clearAttributes() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getAttributesCount() {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T getAttribute(AttributeKey<T> key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<AttributeKey<?>> attributeKeys() {
        throw new UnsupportedOperationException();
    }

    @Override
    public CloseFuture close(boolean immediately) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addCloseFutureListener(SshFutureListener<CloseFuture> listener) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeCloseFutureListener(SshFutureListener<CloseFuture> listener) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isClosed() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isClosing() {
        throw new UnsupportedOperationException();
    }

    @Override
    public PropertyResolver getParentPropertyResolver() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Map<String, Object> getProperties() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getUsername() {
        throw new UnsupportedOperationException();
    }

    @Override
    public SessionDisconnectHandler getSessionDisconnectHandler() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setSessionDisconnectHandler(SessionDisconnectHandler handler) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addSessionListener(SessionListener listener) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeSessionListener(SessionListener listener) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SessionListener getSessionListenerProxy() {
        throw new UnsupportedOperationException();
    }

    @Override
    public UnknownChannelReferenceHandler getUnknownChannelReferenceHandler() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setUnknownChannelReferenceHandler(UnknownChannelReferenceHandler handler) {
        throw new UnsupportedOperationException();
    }

    @Override
    public UnknownChannelReferenceHandler resolveUnknownChannelReferenceHandler() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setSignatureFactories(List<NamedFactory<Signature>> factories) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<NamedFactory<Signature>> getSignatureFactories() {
        throw new UnsupportedOperationException();
    }
}
