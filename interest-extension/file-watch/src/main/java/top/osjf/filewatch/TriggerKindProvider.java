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

/**
 * {@code TriggerKind} provider that set the trigger range for file notifications.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.1
 */
public abstract class TriggerKindProvider extends BindingConfiguration {
    private static final long serialVersionUID = -2311030807517552722L;
    /**
     * <p>The range of this configuration selection {@link TriggerKind} must be bound within the
     * {@link FileWatchPath#getTriggerKinds()} configuration of the listening address {@link FileWatchPath#getPath()},
     * otherwise it will be considered an invalid configuration.
     */
    private TriggerKind[] triggerKinds
            = {TriggerKind.ENTRY_CREATE, TriggerKind.ENTRY_MODIFY, TriggerKind.ENTRY_DELETE};

    /**
     * @return The range of {@code TriggerKind}.
     */
    public TriggerKind[] getTriggerKinds() {
        return triggerKinds;
    }

    public void setTriggerKinds(TriggerKind[] triggerKinds) {
        this.triggerKinds = triggerKinds;
    }
}
