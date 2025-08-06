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

import static java.util.Objects.requireNonNull;

/**
 /**
 * Enhanced watch event implementation that carries parent directory information.
 * <p>Wraps standard {@link WatchEvent} while adding parent path tracking capability.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.1
 */
public class AmapleWatchEvent implements WatchEvent<Path> {

    /** Parent directory where the event occurred */
    private final Path parent;

    /** Original watch event being wrapped */
    private final WatchEvent<Path> event;

    /**
     * Constructs enhanced watch event.
     *
     * @param parent parent directory path.
     * @param event original watch event.
     * @throws NullPointerException if any parameter is null.
     */
    public AmapleWatchEvent(Path parent, WatchEvent<Path> event) {
        this.parent = requireNonNull(parent, "parent");
        this.event = requireNonNull(event, "event");;
    }
    /**
     * Gets parent directory of the event
     * @return parent directory path.
     */
    public Path getParent() {
        return parent;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Kind<Path> kind() {
        return event.kind();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public int count() {
        return event.count();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public Path context() {
        return event.context();
    }
}
