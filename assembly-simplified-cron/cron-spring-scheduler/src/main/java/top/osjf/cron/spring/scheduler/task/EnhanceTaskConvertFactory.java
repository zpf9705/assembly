///*
// * Copyright 2024-? the original author or authors.
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *      https://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//
//package top.osjf.cron.spring.scheduler.task;
//
///**
// * The {@code EnhanceTaskConvertFactory} interface defines a set of methods for
// * converting timed tasks in the Spring framework (such as {@link TriggerTask},
// * {@link CronTask}, {@link FixedDelayTask}, {@link FixedRateTask}) into enhanced
// * versions.
// *
// * <p>This transformation involves adding additional functionality, modifying the
// * behavior of task execution, or simply wrapping the original task for use in an
// * enhanced environment.
// *
// * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
// * @since 1.0.3
// */
//public interface EnhanceTaskConvertFactory {
//
//    /**
//     * Use Spring {@code TriggerTask} create new enhance {@link TriggerTask}.
//     *
//     * @param triggerTask Spring TriggerTask.
//     * @return Enhance version instance of {@link org.springframework.scheduling.config.TriggerTask}.
//     */
//    TriggerTask newTriggerTask(org.springframework.scheduling.config.TriggerTask triggerTask);
//
//    /**
//     * Use Spring {@code CronTask} create new enhance {@link CronTask}.
//     *
//     * @param cronTask Spring CronTask.
//     * @return Enhance version instance of {@link org.springframework.scheduling.config.CronTask}.
//     */
//    CronTask newCronTask(org.springframework.scheduling.config.CronTask cronTask);
//
//    /**
//     * Use Spring {@code FixedDelayTask} create new enhance {@link FixedDelayTask}.
//     *
//     * @param fixedDelayTask Spring FixedDelayTask.
//     * @return Enhance version instance of {@link org.springframework.scheduling.config.FixedDelayTask}.
//     */
//    FixedDelayTask newFixedDelayTask(org.springframework.scheduling.config.FixedDelayTask fixedDelayTask);
//
//    /**
//     * Use Spring {@code FixedRateTask} create new enhance {@link FixedRateTask}.
//     *
//     * @param fixedRateTask Spring FixedRateTask.
//     * @return Enhance version instance of {@link org.springframework.scheduling.config.FixedRateTask}.
//     */
//    FixedRateTask newFixedRateTask(org.springframework.scheduling.config.FixedRateTask fixedRateTask);
//}
