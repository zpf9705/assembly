/*
 * Copyright 2026-? the original author or authors.
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


package top.osjf.cron.core.repository;

import top.osjf.commons.lang.OrderComparator;
import top.osjf.commons.util.Assert;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;

/**
 * Loader for discovering and selecting {@link CronTaskRepository} implementations via JDK {@link ServiceLoader}.
 * <p>The standard execution chain: {@code loading() -> configure() -> buildCronTaskRepository()}.
 * <ol>
 *     <li>{@link #loading()}: Discover all SPI implementations, sort by priority, pick the highest priority one.</li>
 *     <li>{@link #configure()}: Trigger lifecycle methods {@link CronTaskRepository#initialize()} and
 *     {@link CronTaskRepository#start()}.</li>
 *     <li>{@link #buildCronTaskRepository()}: Return the final ready-to-use repository instance.</li>
 * </ol>
 * <p>Typical usage example:
 * <pre>{@code
 * CronTaskRepository repository = CronTaskRepositoryLoader.byDefaultLoader()
 *         .loading()
 *         .configure()
 *         .buildCronTaskRepository();
 * }</pre>
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.2
 * @see CronTaskRepository#buildDefaultCronTaskRepository()
 */
class CronTaskRepositoryLoader {

    /** Selected highest priority cron task repository instance */
    private CronTaskRepository cronTaskRepository;

    /** Mark whether {@link #configure()} has been executed */
    private boolean configured;

    private CronTaskRepositoryLoader() {
    }

    /**
     * Create a new {@link CronTaskRepositoryLoader} instance.
     *
     * @return new {@link CronTaskRepositoryLoader}
     */
    public static CronTaskRepositoryLoader byDefaultLoader() {
        return new CronTaskRepositoryLoader();
    }

    /**
     * Load all {@link CronTaskRepository} implementations through {@link ServiceLoader}.
     * Sort all discovered instances using {@link OrderComparator}, then select the highest priority implementation.
     *
     * @return current loader instance for chained calls
     * @throws NoRepositoryFoundException if no {@link CronTaskRepository} implementation registered in SPI
     */
    public CronTaskRepositoryLoader loading() throws NoRepositoryFoundException {
        ServiceLoader<CronTaskRepository> serviceLoader = ServiceLoader.load(CronTaskRepository.class);
        Iterator<CronTaskRepository> iterator = serviceLoader.iterator();
        List<CronTaskRepository> repositories = new ArrayList<>();
        while (iterator.hasNext()) {
            repositories.add(iterator.next());
        }
        if (repositories.isEmpty()) {
            throw new NoRepositoryFoundException("No implementation of CronTaskRepository found by ServiceLoader");
        }
        OrderComparator.sort(repositories);
        cronTaskRepository = repositories.get(0);
        return this;
    }

    /**
     * Execute lifecycle initialization and startup logic for selected repository.
     * <p><strong>Must invoke {@link #loading()} before calling this method.</strong>
     *
     * @return current loader instance for chained calls
     * @throws IllegalStateException    if loading() has not been executed
     * @throws Exception any exception thrown during initialize or start execution
     */
    public CronTaskRepositoryLoader configure() throws Exception {
        Assert.state(cronTaskRepository != null,
                "Please execute loading() before configure()");
        cronTaskRepository.initialize();
        cronTaskRepository.start();
        configured = true;
        return this;
    }

    /**
     * Obtain the fully initialized and started {@link CronTaskRepository}.
     * <p><strong>Execution sequence reminder:</strong> loading() &gt; configure() &gt; buildCronTaskRepository().
     *
     * @return selected ready-to-use {@link CronTaskRepository}
     * @throws IllegalStateException    if loading() or configure() has not been executed
     */
    public CronTaskRepository buildCronTaskRepository() {
        Assert.state(cronTaskRepository != null,
                "Please execute loading() and configure() before buildCronTaskRepository()");
        Assert.state(configured,
                "Please execute configure() before buildCronTaskRepository()");

        return cronTaskRepository;
    }

}
