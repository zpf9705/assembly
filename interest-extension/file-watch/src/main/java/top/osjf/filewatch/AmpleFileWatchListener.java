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

import java.nio.file.Path;
import java.nio.file.WatchEvent;

/**
 * Abstract class of {@link FileWatchListener} that {@link #supports(WatchEvent)}
 * and {@link #onWatchEvent(WatchEvent)} method param must be {@link AmapleWatchEvent}
 * to execute internal methods.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.1
 */
public abstract class AmpleFileWatchListener implements FileWatchListener {
    /**
     * {@inheritDoc}
     * Be {@link AmapleWatchEvent} to execute {@link #supportsInternal(AmapleWatchEvent)}.
     */
    @Override
    public final boolean supports(WatchEvent<Path> event) {
        if (instanceofAmapleWatchEvent(event)) {
            return supportsInternal((AmapleWatchEvent) event);
        }
        return false;
    }
    /**
     * {@inheritDoc}
     * Be {@link AmapleWatchEvent} to execute {@link #onWatchEventInternal(AmapleWatchEvent)}.
     */
    @Override
    public final void onWatchEvent(WatchEvent<Path> event) {
        if (instanceofAmapleWatchEvent(event)) {
            onWatchEventInternal((AmapleWatchEvent) event);
        }
    }

    /**
     * Is instanceof {@link AmapleWatchEvent}?
     * @param event the given {@link WatchEvent}.
     * @return {@code true} instanceof {@link AmapleWatchEvent},{@code false} otherwise.
     */
    private boolean instanceofAmapleWatchEvent(WatchEvent<Path> event) {
        return event instanceof AmapleWatchEvent;
    }

    /**
     * Internal method of {@link #supports(WatchEvent)}.
     * @param event the converted {@link AmapleWatchEvent}.
     * @return {@link #supports(WatchEvent)}
     */
    protected abstract boolean supportsInternal(AmapleWatchEvent event);

    /**
     * Internal method of {@link #onWatchEvent(WatchEvent)}.
     * @param event the converted {@link AmapleWatchEvent}.
     */
    protected abstract void onWatchEventInternal(AmapleWatchEvent event);
}
