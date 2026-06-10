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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.osjf.cron.core.lang.Nullable;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * An abstract class {@code PropertiesInitializeAble} initialized based on
 * the {@link InitializeProperties} instance.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.2
 */
public abstract class PropertiesInitializeAble implements InitializeAble {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Nullable private InitializeProperties initializeProperties;

    /**
     * Atomic flag to track whether the repository is initialized.
     */
    private final AtomicBoolean isInitialized = new AtomicBoolean(false);

    /**
     * Return the {@link InitializeProperties} instance of the setting.
     * @return the {@link InitializeProperties} instance.
     */
    @Nullable
    public InitializeProperties getInitializeProperties() {
        return initializeProperties;
    }

    /**
     * Set a {@link InitializeProperties} as Initialization reference.
     *
     * @param initializeProperties the {@link InitializeProperties} instance.
     */
    public void setInitializeProperties(@Nullable InitializeProperties initializeProperties) {
        this.initializeProperties = initializeProperties;
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
     */
    protected void ensureInitialized() {
        if (!isInitialized.get()) {
            throw new IllegalStateException(String.format(
                    "Repository(%s) has not been initialized yet, please initialize first!", getClass()
                            .getSimpleName()));
        }
    }
}
