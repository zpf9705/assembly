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
import org.apache.sshd.client.session.ClientSession;

import java.io.IOException;

/**
 * {@link ClientSession} that requires password method {@link ClientSession#addPasswordIdentity}
 * verification needs to obtain available {@link ClientSession} after providing password verification.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
public interface PasswordIdentityClientSession extends ClientSession {

    /**
     * Return {@link ClientSession} that has passed password verification.
     *
     * @param password Password to be added - may not be {@code null}/empty. <B>Note:</B> this password is <U>in
     *                 addition</U> to whatever passwords are available via the {@link PasswordIdentityProvider} (if
     *                 any).
     * @return The underlying {@link ClientSession} used.
     * @throws IOException If failed to create the connection future.
     */
    ClientSession getAfterPasswordAuthClientSession(String password) throws IOException;
}
