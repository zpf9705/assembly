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


package top.osjf.optimize.idempotent.global.config;

/**
 * The idempotent global configuration option, when not indicated in the annotation
 * {@link top.osjf.optimize.idempotent.annotation.Idempotent}, follows this configuration.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.4
 */
public class IdempotentGlobalConfiguration {

    /**
     * The control duration of idempotent keys is used globally by default.
     */
    private long duration = 10;

    /**
     * Reminder message for idempotent parity failure, used globally by default.
     */
    private String message = "Repeated request, please try again later";

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
