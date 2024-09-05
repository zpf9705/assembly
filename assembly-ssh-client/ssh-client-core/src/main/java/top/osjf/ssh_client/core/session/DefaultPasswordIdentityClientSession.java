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

import org.apache.sshd.client.session.ClientSession;

import java.io.IOException;

/**
 * The default {@link PasswordIdentityClientSession} implementation class depends
 * on {@link ClientSession} for functionality implementation.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
public class DefaultPasswordIdentityClientSession extends UnsupportedClientSession implements
        PasswordIdentityClientSession {

    private final ClientSession clientSession;

    public DefaultPasswordIdentityClientSession(ClientSession clientSession) {
        this.clientSession = clientSession;
    }

    @Override
    public ClientSession getAfterPasswordAuthClientSession(String password) throws IOException {
        clientSession.addPasswordIdentity(password);
        clientSession.auth().verify();
        return clientSession;
    }
}
