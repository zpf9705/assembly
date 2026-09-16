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


package top.osjf.cron.core.repository;

/**
 * Extended {@link Version} interface for source metadata.
 *
 * <p>Provides additional metadata describing the source type and its corresponding
 * source‑level version. Inherits {@link #getVersion()} from {@link Version} which
 * represents the framework runtime version.
 * <ul>
 * <li>{@link #getSourceType()} identifies what kind of source this is.</li>
 * <li>{@link #getSourceVersion()} identifies the version of that source artifact.</li>
 * </ul>
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.2
 */
public interface SourceVersion extends Version {

    /**
     * Return the type identifier of the underlying source.
     * @return the source type identifier.
     */
    String getSourceType();

    /**
     * Return the version string of the underlying source artifact.
     * <p>Represents the version of source itself, different from framework
     * runtime version {@link #getVersion()}.
     * @return the source version string.
     */
    String getSourceVersion();
}
