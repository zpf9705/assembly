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
public interface SshPasswordAuthClientSessionService extends SshClientSessionService {

    /**
     * {@inheritDoc}
     *
     * @return A password authentication {@link ClientSession}.
     */
    @Override
    ClientSession connect(String uri) throws IOException;

    /**
     * {@inheritDoc}
     *
     * @return A password authentication {@link ClientSession}.
     */
    @Override
    ClientSession connect(String username, String host, int port, AttributeRepository context,
                          SocketAddress localAddress) throws IOException;

    /**
     * {@inheritDoc}
     *
     * @return A password authentication {@link ClientSession}.
     */
    @Override
    ClientSession connect(String username, SocketAddress targetAddress, AttributeRepository context,
                          SocketAddress localAddress) throws IOException;

    /**
     * {@inheritDoc}
     *
     * @return A password authentication {@link ClientSession}.
     */
    @Override
    ClientSession connect(HostConfigEntry hostConfig, AttributeRepository context,
                          SocketAddress localAddress) throws IOException;
}
