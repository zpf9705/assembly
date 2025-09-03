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


package top.osjf.cron.datasource.driven.scheduled.external.file;

import top.osjf.cron.datasource.driven.scheduled.DataSourceDrivenException;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Logger;

/**
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.1
 */
final class LoaderLifecycle {

    /**
     * Support lifecycle methods for handling native annotations {@link PostConstruct}
     * and {@link PreDestroy} given {@link ExternalFileTaskElementLoader}.
     * @param loader the {@link ExternalFileTaskElementLoader} instance.
     */
    static void handleLoaderLifecycle(ExternalFileTaskElementLoader<?> loader) {
        Objects.requireNonNull(loader, "loader");

        Set<Method> initMethods  = new HashSet<>();
        Set<Method> destroyMethods  = new HashSet<>();

        // Only supports the execution of public permission methods.
        for (Method md : loader.getClass().getMethods()) {

            // Init Methods annotated javax.annotation.PostConstruct.
            if (md.isAnnotationPresent(PostConstruct.class)) {
                initMethods.add(md);
            }

            // Destroy Methods annotated javax.annotation.PreDestroy.
            if (md.isAnnotationPresent(PreDestroy.class)) {
                destroyMethods.add(md);
            }
        }

        // The initialization method performs initialization operations.
        if (!initMethods.isEmpty()) {
            for (Method initMethod : initMethods) {
                try {
                    initMethod.invoke(loader);
                }
                catch (Exception ex) {
                    throw new DataSourceDrivenException("Failed to execute init method "
                            + initMethod.getDeclaringClass() + "." + initMethod.getName(), ex);
                }
            }
        }

        // The destruction method is executed when registering the hook
        // function and shutting down the JVM.
        if (!destroyMethods.isEmpty()) {
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                for (Method destroyMethod : destroyMethods) {
                    try {
                        destroyMethod.invoke(loader);
                    }
                    catch (Exception ex) {
                        Logger.getLogger(loader.getClass().getName())
                                .severe("Failed to execute destroy method "
                                        + destroyMethod.getDeclaringClass() + "." + destroyMethod.getName()
                                        + " due to " + ex.getMessage());
                    }
                }
            }));
        }
    }
}
