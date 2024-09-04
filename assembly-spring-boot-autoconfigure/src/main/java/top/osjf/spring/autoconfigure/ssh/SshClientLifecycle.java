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

import org.apache.sshd.client.SshClient;
import org.springframework.context.SmartLifecycle;

/**
 * Bean for intelligent lifecycle management of {@link SshClient}.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.2
 */
public class SshClientLifecycle implements SmartLifecycle {

    private final SshClient sshClient;

    public SshClientLifecycle(SshClient sshClient) {
        this.sshClient = sshClient;
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
