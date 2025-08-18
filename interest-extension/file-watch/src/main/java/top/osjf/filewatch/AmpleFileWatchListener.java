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


package top.osjf.filewatch;

import java.nio.file.WatchEvent;

/**
 * Abstract class of {@link FileWatchListener} that {@link #supports(WatchEvent)}
 * and {@link #onWatchEvent(WatchEvent)} method param be {@link AmapleWatchEvent}
 * to execute internal methods.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.1
 *
 * @see AmapleWatchEvent
 */
public abstract class AmpleFileWatchListener implements FileWatchListener<AmapleWatchEvent> {
}
