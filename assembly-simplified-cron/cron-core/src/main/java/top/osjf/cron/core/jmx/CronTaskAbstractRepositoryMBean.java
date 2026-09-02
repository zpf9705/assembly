/*
 * Copyright 2026-? the original author or authors.
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


package top.osjf.cron.core.jmx;

/**
 * MBean interface, which exposes the basic metadata of timed task resource library
 * instances to the outside world.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.2
 */
public interface CronTaskAbstractRepositoryMBean {

    /**
     * Gets the type of operation resource library.
     * @return the type of operation resource library.
     */
    String getType();

    /**
     * Gets the name of the operation resource library.
     * @return the name of the operation resource library.
     */
    String getName();

    /**
     * Gets the version of the resource repository.
     * @return the version of the resource repository.
     */
    String getVersion();

    /**
     * Gets the type of the underlying implementation of the operation resource library.
     * @return the type of the underlying implementation of the operation resource library.
     */
    String getSourceType();

    /**
     * Gets the version of the underlying implementation of the operation resource library.
     * @return the version of the underlying implementation of the operation resource library.
     */
    String getSourceVersion();

    /**
     * Returns a Boolean type marker indicating whether concurrent scheduling is supported.
     * @return {@code true} to indicate support, otherwise it is not supported.
     */
    boolean isSupportConcurrentExecution();

    /**
     * Get the type of {@link top.osjf.cron.core.repository.IDGenerator} for custom settings.
     * @return the type of {@link top.osjf.cron.core.repository.IDGenerator} for custom settings.
     */
    String getIDGeneratorType();

    /**
     * Get a summary of key configuration attributes in the resource library.
     * @return a summary of key configuration attributes in the resource library.
     */
    String getProperties();
}
