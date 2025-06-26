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

import com.github.benmanes.caffeine.cache.Expiry;
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.core.NamedThreadLocal;
import org.springframework.lang.Nullable;

import java.util.concurrent.TimeUnit;

/**
 * An expiration interface implementation class for {@link Expiry} that supports a
 * specific expiration control time for a single power equivalent.
 *
 * <p>This class provides a unit expiration time for each idempotent set key through
 * the thread value independence of {@link ThreadLocal}, and shows the remaining time
 * when accessed.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.4
 */
public class IdempotentKeyExpiry implements Expiry<String, String> {

    private final ThreadLocal<Long> durations = new NamedThreadLocal<>("Idempotent Key Duration");

    /**
     * Set or remove the effective time of the current idempotent thread.
     *
     * @param nanosDuration The idempotent maintains an effective interval
     *                      of nanoseconds.
     */
    public void setDuration(@Nullable Long nanosDuration) {
        if (nanosDuration == null) {
            durations.remove();
        }
        else {
            durations.set(nanosDuration);
        }
    }

    @Override
    public long expireAfterCreate(@NonNull String key, @NonNull String value, long currentTime) {
        Long duration = durations.get();
        return duration != null ? duration : TimeUnit.SECONDS.toNanos(60);
    }

    @Override
    public long expireAfterUpdate(@NonNull String key, @NonNull String value, long currentTime,
                                  @NonNegative long currentDuration) {
        throw new UnsupportedOperationException();
    }

    @Override
    public long expireAfterRead(@NonNull String key, @NonNull String value, long currentTime,
                                @NonNegative long currentDuration) {
        return currentDuration;
    }
}
