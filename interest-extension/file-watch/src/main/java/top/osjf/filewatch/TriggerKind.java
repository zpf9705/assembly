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
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;

/**
 * The trigger of the enumeration class specifies the change type value
 * of the application enable command.
 *
 * <p>Each {@link TriggerKind} is mapped to a constant value of each
 * {@link java.nio.file.WatchEvent.Kind}, and then converted to each
 * other. Here, suitability represents selection.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.1
 */
public enum TriggerKind {
    /**
     * Directory entry created.
     *
     * <p> When a directory is registered for this event then the {@link WatchKey}
     * is queued when it is observed that an entry is created in the directory
     * or renamed into the directory. The event {@link WatchEvent#count count}
     * for this event is always {@code 1}.
     *
     * @see java.nio.file.StandardWatchEventKinds#ENTRY_CREATE
     */
    ENTRY_CREATE(StandardWatchEventKinds.ENTRY_CREATE),

    /**
     * Directory entry modified.
     *
     * <p> When a directory is registered for this event then the {@link WatchKey}
     * is queued when it is observed that an entry in the directory has been
     * modified. The event {@link WatchEvent#count count} for this event is
     * {@code 1} or greater.
     *
     * @see java.nio.file.StandardWatchEventKinds#ENTRY_MODIFY
     */
    ENTRY_MODIFY(StandardWatchEventKinds.ENTRY_MODIFY),

    /**
     * Directory entry deleted.
     *
     * <p> When a directory is registered for this event then the {@link WatchKey}
     * is queued when it is observed that an entry is deleted or renamed out of
     * the directory. The event {@link WatchEvent#count count} for this event
     * is always {@code 1}.
     *
     * @see java.nio.file.StandardWatchEventKinds#ENTRY_DELETE
     */
    ENTRY_DELETE(StandardWatchEventKinds.ENTRY_DELETE);

    /**
     * @see java.nio.file.WatchEvent.Kind
     */
    final WatchEvent.Kind<Path> kind;

    TriggerKind(WatchEvent.Kind<Path> kind) {
        this.kind = kind;
    }

    /**
     * @return a {@link java.nio.file.WatchEvent.Kind} map to {@link TriggerKind}.
     */
    public WatchEvent.Kind<Path> getKind() {
        return kind;
    }
}
