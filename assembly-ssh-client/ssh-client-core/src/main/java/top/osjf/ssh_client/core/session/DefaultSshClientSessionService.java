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
import org.apache.sshd.client.future.ConnectFuture;
import org.apache.sshd.client.session.ClientSession;
import org.apache.sshd.common.AttributeRepository;
import top.osjf.ssh_client.core.SshClientService;

import java.io.IOException;
import java.net.SocketAddress;

/**
 * The default {@link SshClientSessionService} implementation class depends on {@link SshClientService}
 * for functionality implementation.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
public class DefaultSshClientSessionService implements SshClientSessionService {

    private final SshClientService<ConnectFuture> sshClientService;

    public DefaultSshClientSessionService(SshClientService<ConnectFuture> sshClientService) {
        this.sshClientService = sshClientService;
    }

    @Override
    public ClientSession connect(String uri) throws IOException {
        return sshClientService.connect(uri).getClientSession();
    }

    @Override
    public ClientSession connect(String username, String host, int port, AttributeRepository context,
                                 SocketAddress localAddress) throws IOException {
        return sshClientService.connect(username, host, port, context, localAddress).getClientSession();
    }

    @Override
    public ClientSession connect(String username, SocketAddress targetAddress, AttributeRepository context,
                                 SocketAddress localAddress) throws IOException {
        return sshClientService.connect(username, targetAddress, context, localAddress).getClientSession();
    }

    @Override
    public ClientSession connect(HostConfigEntry hostConfig, AttributeRepository context, SocketAddress localAddress)
            throws IOException {
        return sshClientService.connect(hostConfig, context, localAddress).getClientSession();
    }

    @Override
    public void start() {
        sshClientService.start();
    }

    @Override
    public void stop() {
        sshClientService.stop();
    }

    @Override
    public boolean isRunning() {
        return sshClientService.isRunning();
    }
}
