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

import org.apache.sshd.client.auth.password.PasswordIdentityProvider;
import org.apache.sshd.client.config.hosts.HostConfigEntry;
import org.apache.sshd.client.session.ClientSession;
import org.apache.sshd.common.AttributeRepository;

import java.io.IOException;
import java.net.SocketAddress;

/**
 * This interface divides the returned value into {@link ClientSession} with verified keys.
 * By calling the API of this interface, remote server operations can be performed directly.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
public interface SshPasswordIdentityClientSessionService extends SshClientSessionService {

    /**
     * On the basis of {@link #connect}, perform server password verification for {@link ClientSession}.
     * @param username     The intended username.
     * @param host         The target host name/address - never {@code null}/empty.
     * @param port         The target port.
     * @param context      An optional &quot;context&quot; to be attached to the established session if successfully
     *                     connected.
     * @param localAddress The local address to use - if {@code null} an automatic ephemeral port and bind address is
     *                     used.
     * @param password Password to be added - may not be {@code null}/empty. <B>Note:</B> this password is <U>in
     *                 addition</U> to whatever passwords are available via the {@link PasswordIdentityProvider} (if
     *                 any)
     * @return A password authentication {@link ClientSession}.
     * @throws IOException If failed to resolve the effective target or connect to it.
     */
    ClientSession connect(String username, String host, int port, AttributeRepository context,
                          SocketAddress localAddress, String password) throws IOException;

    /**
     * On the basis of {@link #connect}, perform server password verification for {@link ClientSession}.
     * @param username      The intended username.
     * @param targetAddress The intended target {@link SocketAddress} - never {@code null}. If this is an
     *                      {@link java.net.InetSocketAddress} then the <U>effective</U> {@link HostConfigEntry} is
     *                      resolved and used.
     * @param context       An optional &quot;context&quot; to be attached to the established session if successfully
     *                      connected.
     * @param localAddress  The local address to use - if {@code null} an automatic ephemeral port and bind address is
     *                      used.
     * @param password Password to be added - may not be {@code null}/empty. <B>Note:</B> this password is <U>in
     *                 addition</U> to whatever passwords are available via the {@link PasswordIdentityProvider} (if
     *                 any)
     * @return A password authentication {@link ClientSession}.
     * @throws IOException If failed to resolve the effective target or connect to it.
     */
    ClientSession connect(String username, SocketAddress targetAddress, AttributeRepository context,
                          SocketAddress localAddress, String password) throws IOException;

    /**
     * On the basis of {@link #connect}, perform server password verification for {@link ClientSession}.
     * @param hostConfig   The effective {@link HostConfigEntry} to connect to - never {@code null}.
     * @param context      An optional &quot;context&quot; to be attached to the established session if successfully
     *                     connected.
     * @param localAddress The local address to use - if {@code null} an automatic ephemeral port and bind address is
     *                     used.
     * @param password Password to be added - may not be {@code null}/empty. <B>Note:</B> this password is <U>in
     *                 addition</U> to whatever passwords are available via the {@link PasswordIdentityProvider} (if
     *                 any)
     * @return A password authentication {@link ClientSession}.
     * @throws IOException If failed to resolve the effective target or connect to it.
     */
    ClientSession connect(HostConfigEntry hostConfig, AttributeRepository context,
                          SocketAddress localAddress, String password) throws IOException;
}
