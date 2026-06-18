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

/**
 * Contextual interface for scheduled task storage capacity.
 *
 * <p>As a pluggable capability interface, it provides quick access to various
 * storage operations within the task execution context. After implementing this
 * interface, subdivision storage interfaces for different responsibilities can
 * be directly obtained through built-in default methods, with internal safe type
 * conversions based on {@link CronTaskRepository}.
 *
 * <p>Separate and refine each intelligent subdivision interface of {@link Repository},
 * and by default, choose the inherited implementation of {@link CronTaskRepository}.
 * From the perspective of extension, allow subclasses to override and independently
 * implement relevant resource responsibilities, such as implementing the timeout
 * judgment capability of {@link #runTimeout()} independently as needed, specifically
 * based on the relevant framework or internal structure of the component.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.2
 */
public interface RepositoryContext {

    /**
     * @return An independently implemented {@link GeneralRegistrarRepository}, defaults to {@link CronTaskRepository}.
     */
    default GeneralRegistrarRepository general() {
        return getRepository();
    }

    /**
     * @return An independently implemented {@link RunTimesRegistrarRepository}, defaults to {@link CronTaskRepository}.
     */
    default RunTimesRegistrarRepository runTimes() {
        return getRepository();
    }

    /**
     * @return An independently implemented {@link RunTimeoutRegistrarRepository}, defaults to {@link CronTaskRepository}.
     */
    default RunTimeoutRegistrarRepository runTimeout() {
        return getRepository();
    }

    /**
     * @return An independently implemented {@link ModifiableRepository}, defaults to {@link CronTaskRepository}.
     */
    default ModifiableRepository modifiable() {
        return getRepository();
    }

    /**
     * @return An independently implemented {@link ListableRepository}, defaults to {@link CronTaskRepository}.
     */
    default ListableRepository listable() {
        return getRepository();
    }

    /**
     * @return An independently implemented {@link LifecycleRepository}, defaults to {@link CronTaskRepository}.
     */
    default LifecycleRepository lifecycle() {
        return getRepository();
    }

    /**
     * @return An independently implemented {@link CronListenerRepository}, defaults to {@link CronTaskRepository}.
     */
    default CronListenerRepository listener() {
        return getRepository();
    }

    /**
     * Get the aggregated root repository of all cron task persistent capabilities.
     *
     * <p>All fine-grained repository capabilities are aggregated in this single entry,
     * all semantic shortcut methods inside this interface rely on this instance for
     * type casting.
     *
     * @return aggregated cron task repository instance.
     */
    CronTaskRepository getRepository();
}
