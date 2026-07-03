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

import top.osjf.commons.util.Assert;

/**
 * Type-safe implementation of {@link RepositoryContext}.
 * <p>
 * Wraps a single underlying {@link Repository} instance and provides typed view conversion
 * for various sub-capability repository interfaces. Each access method will validate the actual
 * repository type at runtime and perform safe casting to return the specific repository subtype.
 * <p>
 * If the wrapped repository does not implement the target repository interface,
 * an {@link IllegalArgumentException} will be thrown to report the type mismatch.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.2
 */
public class TypedRepositoryContext implements RepositoryContext {

    private final Repository repository;

    /**
     * Create a {@code TypedRepositoryContext} wrapping the given root repository instance.
     * @param repository the underlying repository to wrap and convert to various typed views
     */
    public TypedRepositoryContext(Repository repository) {
        Assert.notNull(repository, "repository must not be null");
        this.repository = repository;
    }

    /**
     * {@inheritDoc}
     * @throws IllegalArgumentException If the type of {@link #repository} is not {@link GeneralRegistrarRepository}.
     */
    @Override
    public GeneralRegistrarRepository general() {
        return assertResourceConversion(GeneralRegistrarRepository.class);
    }

    /**
     * {@inheritDoc}
     * @throws IllegalArgumentException If the type of {@link #repository} is not {@link RunTimesRegistrarRepository}.
     */
    @Override
    public RunTimesRegistrarRepository runTimes() {
        return assertResourceConversion(RunTimesRegistrarRepository.class);
    }

    /**
     * {@inheritDoc}
     * @throws IllegalArgumentException If the type of {@link #repository} is not {@link RunTimeoutRegistrarRepository}.
     */
    @Override
    public RunTimeoutRegistrarRepository runTimeout() {
        return assertResourceConversion(RunTimeoutRegistrarRepository.class);
    }

    /**
     * {@inheritDoc}
     * @throws IllegalArgumentException If the type of {@link #repository} is not {@link ModifiableRepository}.
     */
    @Override
    public ModifiableRepository modifiable() {
        return assertResourceConversion(ModifiableRepository.class);
    }

    /**
     * {@inheritDoc}
     * @throws IllegalArgumentException If the type of {@link #repository} is not {@link ListableRepository}.
     */
    @Override
    public ListableRepository listable() {
        return assertResourceConversion(ListableRepository.class);
    }

    /**
     * {@inheritDoc}
     * @throws IllegalArgumentException If the type of {@link #repository} is not {@link LifecycleRepository}
     */
    @Override
    public LifecycleRepository lifecycle() {
        return assertResourceConversion(LifecycleRepository.class);
    }

    /**
     * {@inheritDoc}
     * @throws IllegalArgumentException If the type of {@link #repository} is not {@link CronListenerRepository}
     */
    @Override
    public CronListenerRepository listener() {
        return assertResourceConversion(CronListenerRepository.class);
    }

    /**
     * {@inheritDoc}
     * @throws IllegalArgumentException If the type of {@link #repository} is not {@link CronTaskRepository}
     */
    @Override
    public CronTaskRepository getRepository() {
        return assertResourceConversion(CronTaskRepository.class);
    }

    /**
     * Cast the held repository instance to the specified target repository type,
     * and validate type matching. An exception will be thrown if the type does not match.
     *
     * @param repositoryType the target {@code Class} type to cast the repository to
     * @param <T> the target repository generic type
     * @return the repository instance cast to the target type
     * @throws IllegalArgumentException if the held repository is not an instance of the specified target type.
     */
    private <T extends Repository>T assertResourceConversion(Class<T> repositoryType) {

        Assert.isTrue(repositoryType.isInstance(repository),
                String.format("Expected repository type: [%s], actual type: [%s]",
                        repositoryType.getName(), repository.getClass().getName()));

        return repositoryType.cast(repository);
    }
}
