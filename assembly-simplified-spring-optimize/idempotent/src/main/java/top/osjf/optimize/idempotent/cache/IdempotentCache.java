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


package top.osjf.optimize.idempotent.cache;

import javax.annotation.concurrent.ThreadSafe;

/**
 * The {@code IdempotentCache} control interface provides periodic control of idempotent
 * unique values, ensuring idempotent security for a certain period of time after idempotent
 * validation.
 *
 * <p>At the same time, open the idempotent unique value clearing scheme in a timely manner,
 * and users can also reasonably release idempotent control at the appropriate time.
 *
 * <p>NOTE:<strong>It should be noted that the implementation class must ensure thread safety
 * with idempotent control</strong>
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.4
 */
@ThreadSafe
public interface IdempotentCache {

    /**
     * After successfully setting the cache idempotent key within the specified
     * nanosecond time, the idempotent key cannot be used again within the specified
     * nanosecond time.
     *
     * <p>The success or failure of caching idempotent keys will be returned with
     * {@code Boolean} markers, and users can make control judgments based on the
     * return value.
     *
     * @param idempotentKey The idempotent key to cache.
     * @param nanosDuration The number of nanoseconds controlled by idempotency.
     * @return The {@code Boolean} that idempotent setting successful，{@code true}
     * successful,{@code false} otherwise.
     */
    boolean cacheIdempotent(String idempotentKey, long nanosDuration);

    /**
     * Proactively remove power-law values and release idempotent control.
     *
     * @param idempotentKey The idempotent key to be removed.
     */
    void removeIdempotent(String idempotentKey);
}
