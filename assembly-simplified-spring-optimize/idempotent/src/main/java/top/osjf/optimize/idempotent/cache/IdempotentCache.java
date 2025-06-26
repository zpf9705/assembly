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

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import static java.util.Objects.requireNonNull;

/**
 * A {@code IdempotentCache} class that supports idempotent intervals, using {@link Cache}
 * to achieve idempotent control and removal.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.4
 */
public class IdempotentCache {

    private final IdempotentKeyExpiry expiry;

    private final Cache<String, String> cache;

    public IdempotentCache() {
        this.expiry = new IdempotentKeyExpiry();
        this.cache = Caffeine.newBuilder().expireAfter(expiry).build();
    }

    public boolean cacheIdempotent(String idempotentKey, long nanosDuration) {
        requireNonNull(idempotentKey, "idempotentKey");
        try {
            expiry.setDuration(nanosDuration);
            // Check if the key exists in the cache
            String value = this.cache.getIfPresent(idempotentKey);
            if (value == null) {
                // Store the idempotent key
                this.cache.put(idempotentKey, idempotentKey);
                // Key was successfully saved
                return true;
            }
            // Key already exists
            return false;
        } finally {
            expiry.setDuration(null);
        }
    }

    public void removeIdempotent(String idempotentKey) {
        // Invalidate (remove) the key from the cache
        this.cache.invalidate(idempotentKey);
    }
}
