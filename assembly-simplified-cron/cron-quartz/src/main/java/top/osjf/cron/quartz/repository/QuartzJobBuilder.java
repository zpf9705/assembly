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


package top.osjf.cron.quartz.repository;

import org.quartz.*;
import top.osjf.cron.core.repository.CronTaskRepository;

/**
 * Compatibility implementation of new {@link JobBuilder} inherited instances of
 * {@link CronTaskRepository} API, interfering with some of its method constructions
 * without affecting its functionality.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.2
 */
class QuartzJobBuilder extends JobBuilder {

    private final QuartzCronTaskRepository repository;

    public QuartzJobBuilder(QuartzCronTaskRepository repository) {
        super();
        this.repository = repository;
    }

    /**
     * Create a {@code QuartzJobBuilder} with which to define a <code>JobDetailWrapper</code>,
     * and set the class name of the <code>Job</code> to be executed.
     *
     * @return a new {@code QuartzJobBuilder}
     */
    public static JobBuilder newJob(Class <? extends Job> jobClass, QuartzCronTaskRepository repository) {
        QuartzJobBuilder b = new QuartzJobBuilder(repository);
        b.ofType(jobClass);
        return b;
    }

    /**
     * @return Return a wrapper for {@link JobDetail}, and the related methods of {@link JobDetail}
     * may be controlled and compatible by {@link QuartzCronTaskRepository}.
     */
    @Override
    public JobDetail build() {
        return new JobDetailWrapper(super.build(), repository);
    }

    private static class JobDetailWrapper implements JobDetail {

        private static final long serialVersionUID = 5705579476858697787L;

        private final QuartzCronTaskRepository repository;

        public JobDetail source;

        public JobDetailWrapper(JobDetail source, QuartzCronTaskRepository repository) {
            this.source = source;
            this.repository = repository;
        }

        @Override
        public JobKey getKey() {
            return source.getKey();
        }

        @Override
        public String getDescription() {
            return source.getDescription();
        }

        @Override
        public Class<? extends Job> getJobClass() {
            return source.getJobClass();
        }

        @Override
        public JobDataMap getJobDataMap() {
            return source.getJobDataMap();
        }

        @Override
        public boolean isDurable() {
            return source.isDurable();
        }

        @Override
        public boolean isPersistJobDataAfterExecution() {
            return source.isPersistJobDataAfterExecution();
        }

        /**
         * @return Directly use the behavior of prohibiting concurrent scheduling in
         * resource classes for judgment.
         */
        @Override
        public boolean isConcurrentExectionDisallowed() {
            try {
                String id = repository.identityMemory.getIdByJobKey(getKey());
                return repository.hasDisallowConcurrentExecution(id);
            }
            catch (IdentityMemoryException ex) {
                return false;
            }
        }

        @Override
        public boolean requestsRecovery() {
            return source.requestsRecovery();
        }

        @Override
        public Object clone() {
            return source.clone();
        }

        @Override
        public JobBuilder getJobBuilder() {
            return source.getJobBuilder();
        }
    }

}
