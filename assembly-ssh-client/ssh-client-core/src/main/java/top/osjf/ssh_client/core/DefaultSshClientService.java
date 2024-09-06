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

package top.osjf.ssh_client.core;

import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.config.hosts.HostConfigEntry;
import org.apache.sshd.client.future.ConnectFuture;
import org.apache.sshd.common.AttributeRepository;

import java.io.IOException;
import java.net.SocketAddress;

/**
 * The default {@link SshClientService} implementation class depends on {@link SshClient}
 * for functionality implementation.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
public class DefaultSshClientService implements SshClientService<ConnectFuture> {

    private final SshClient sshClient;

    public DefaultSshClientService() {
        this.sshClient = SshClient.setUpDefaultClient();
    }

    public DefaultSshClientService(SshClient sshClient) {
        this.sshClient = sshClient;
    }

    @Override
    public ConnectFuture connect(String uri) throws IOException {
        return sshClient.connect(uri);
    }

    @Override
    public ConnectFuture connect(String username, String host, int port, AttributeRepository context, SocketAddress localAddress) throws IOException {
        return sshClient.connect(username, host, port, context, localAddress);
    }

    @Override
    public ConnectFuture connect(String username, SocketAddress targetAddress, AttributeRepository context, SocketAddress localAddress) throws IOException {
        return sshClient.connect(username, targetAddress, context, localAddress);
    }

    @Override
    public ConnectFuture connect(HostConfigEntry hostConfig, AttributeRepository context, SocketAddress localAddress) throws IOException {
        return sshClient.connect(hostConfig, context, localAddress);
    }

    @Override
    public void start() {
        sshClient.start();
    }

    @Override
    public void stop() {
        sshClient.stop();
    }

    @Override
    public boolean isRunning() {
        return sshClient.isStarted();
    }
}
