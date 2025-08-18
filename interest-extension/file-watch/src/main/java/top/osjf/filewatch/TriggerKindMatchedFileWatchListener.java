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

import java.util.List;

/**
 * Abstract class of {@link FileWatchListener} that obtain the specified set of supported
 * {@link TriggerKind} and perform an include operation on {@link TriggerKind} at the current
 * notification time as the support result for {@link #supports(AmapleWatchEvent)}.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.1
 */
public abstract class TriggerKindMatchedFileWatchListener extends AmpleFileWatchListener {
    /**
     * {@inheritDoc}
     * @throws NullPointerException if {@link #getSupportTriggerKinds(AmapleWatchEvent)} return {@literal null}.
     */
    @Override
    public boolean supports(AmapleWatchEvent event) {
        // Verify whether the trigger type set in the current subclass file is
        // within the range of the parent listening trigger.
        return getSupportTriggerKinds(event).contains(event.getTriggerKind());
    }

    /**
     * Obtain supports {@code TriggerKind} list.
     * @param event the given {@link AmapleWatchEvent}.
     * @return list of support {@code TriggerKind}.
     */
    protected abstract List<TriggerKind> getSupportTriggerKinds(AmapleWatchEvent event);
}
