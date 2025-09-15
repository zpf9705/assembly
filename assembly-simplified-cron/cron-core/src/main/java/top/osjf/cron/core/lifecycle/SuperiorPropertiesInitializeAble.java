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


package top.osjf.cron.core.lifecycle;

import top.osjf.cron.core.lang.Nullable;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * An abstract class {@code SuperiorPropertiesInitializeAble} initialized based on
 * the {@link SuperiorProperties} instance.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.0
 */
public abstract class SuperiorPropertiesInitializeAble implements InitializeAble {

    private SuperiorProperties superiorProperties;

    /**
     * Atomic flag to track whether the repository is initialized.
     *
     * @since 3.0.1
     */
    private final AtomicBoolean isInitialized = new AtomicBoolean(false);

    /**
     * Return the {@link SuperiorProperties} instance of the setting.
     *
     * @return the {@link SuperiorProperties} instance.
     */
    @Nullable
    public SuperiorProperties getSuperiorProperties() {
        return superiorProperties;
    }

    /**
     * Set a {@link SuperiorProperties} as Initialization reference.
     *
     * @param superiorProperties the {@link SuperiorProperties} instance.
     */
    public void setSuperiorProperties(@Nullable SuperiorProperties superiorProperties) {
        this.superiorProperties = superiorProperties;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize() throws Exception {
        if (isInitialized.compareAndSet(false, true)) {
            return;
        }
        throw new IllegalStateException("this repository has initialized");
    }
    
    /**
     * ensure the repository has been initialized before providing service.
     * @since 3.0.1
     */
    protected void ensureInitialized() {
        if (!isInitialized.get()) {
            throw new IllegalStateException(String.format(
                    "Repository(%s) has not been initialized yet, please initialize first!", getClass()
                            .getSimpleName()));
        }
    }
}
