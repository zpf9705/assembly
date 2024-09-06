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

/**
 * The Lifecycle interface defines the lifecycle management method for SSH clients.
 *
 * <p>Through this interface, you can control the start and stop status of SSH clients and
 * check if they are currently running.
 *
 * <p>This is a universal lifecycle management interface designed to enhance the flexibility
 * and usability of SSH client management.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
public interface Lifecycle {

    /**
     * Start the SSH client.
     * This method initializes and starts the SSH client, enabling subsequent connections and operations.
     * If the client is already running, the specific implementation of this method may need to handle
     * this situation (e.g., by throwing an exception or simply ignoring it).
     */
    void start();

    /**
     * Stop the SSH client.
     * This method closes all connections of the SSH client and releases related resources.
     * After stopping, the client will no longer be available until it is restarted by calling the
     * start method again.
     */
    void stop();

    /**
     * Checks if the SSH client is currently running.
     * This method returns a boolean value indicating the current state of the SSH client.
     * It returns true if the client is started and running; otherwise, it returns false.
     *
     * @return true if the SSH client is running; false otherwise.
     */
    boolean isRunning();

}
