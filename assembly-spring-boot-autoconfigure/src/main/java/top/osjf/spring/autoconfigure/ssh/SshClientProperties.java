/*
 * Copyright 2025-? the original author or authors.
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

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for {@link org.apache.sshd.client.SshClient}.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.1
 */
@ConfigurationProperties(prefix = "ssh-client")
public class SshClientProperties {

    /**
     * Whether to fill all properties that are not explicitly set with default values.
     */
    private boolean isFillWithDefaultValues = true;

    public boolean isFillWithDefaultValues() {
        return isFillWithDefaultValues;
    }

    public void setFillWithDefaultValues(boolean fillWithDefaultValues) {
        isFillWithDefaultValues = fillWithDefaultValues;
    }
}
