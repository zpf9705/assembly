///*
// * Copyright 2026-? the original author or authors.
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
//
//package top.osjf.cron.core.micrometer;
//
//import io.micrometer.core.instrument.Counter;
//import io.micrometer.core.instrument.MeterRegistry;
//import io.micrometer.core.instrument.Tags;
//import io.micrometer.core.instrument.Timer;
//import top.osjf.commons.ability.Nameable;
//
//import java.util.concurrent.TimeUnit;
//
///**
// * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
// * @since 3.0.2
// */
//public class CronTaskMicrometer {
//
//    /**
//     * Global meter registry instance, needs to be initialized by the framework when the project starts.
//     */
//    private static MeterRegistry meterRegistry;
//
//    private static final String METRIC_PREFIX = "cron.task";
//    private static final String TAG_REPOSITORY = "repository";
//    private static final String TAG_SUCCESS = "success";
//    private static final String TAG_EXCEPTION = "exception";
//
//    private CronTaskMicrometer() {
//        // Private constructor to prevent instantiation of utility class
//    }
//
//    /**
//     * Initialize the global monitoring registry, must be called once when the project starts.
//     *
//     * @param registry Global MeterRegistry instance
//     */
//    public static void init(MeterRegistry registry) {
//        CronTaskMicrometer.meterRegistry = registry;
//    }
//
//    /**
//     * Task registration metric reporting.
//     *
//     * @param nameable  Current repository instance that implements {@link Nameable}
//     * @param success   Whether the registration is successful
//     * @param throwable Exception thrown during registration, pass {@code null} if successful
//     */
//    public static void recordRegister(Nameable nameable, boolean success, Throwable throwable) {
//        if (meterRegistry == null) {
//            return;
//        }
//        Tags tags = buildBaseTags(nameable, success, throwable);
//        Counter.builder(METRIC_PREFIX + ".register")
//                .tags(tags)
//                .register(meterRegistry)
//                .increment();
//    }
//
//    /**
//     * Task expression update metric reporting.
//     *
//     * @param nameable  Current repository instance that implements {@link Nameable}
//     * @param success   Whether the update is successful
//     * @param throwable Exception thrown during update, pass {@code null} if successful
//     */
//    public static void recordUpdate(Nameable nameable, boolean success, Throwable throwable) {
//        if (meterRegistry == null) {
//            return;
//        }
//        Tags tags = buildBaseTags(nameable, success, throwable);
//        Counter.builder(METRIC_PREFIX + ".update")
//                .tags(tags)
//                .register(meterRegistry)
//                .increment();
//    }
//
//    /**
//     * Task delete metric reporting.
//     *
//     * @param nameable  Current repository instance that implements {@link Nameable}
//     * @param success   Whether the deletion is successful
//     * @param throwable Exception thrown during deletion, pass {@code null} if successful
//     */
//    public static void recordRemove(Nameable nameable, boolean success, Throwable throwable) {
//        if (meterRegistry == null) {
//            return;
//        }
//        Tags tags = buildBaseTags(nameable, success, throwable);
//        Counter.builder(METRIC_PREFIX + ".remove")
//                .tags(tags)
//                .register(meterRegistry)
//                .increment();
//    }
//
//    /**
//     * Illegal cron expression verification metric reporting.
//     *
//     * @param nameable Current repository instance that implements {@link Nameable}
//     */
//    public static void recordInvalidExpression(Nameable nameable) {
//        if (meterRegistry == null) {
//            return;
//        }
//        Tags tags = Tags.of(TAG_REPOSITORY, nameable.getName());
//        Counter.builder(METRIC_PREFIX + ".expression.invalid")
//                .tags(tags)
//                .register(meterRegistry)
//                .increment();
//    }
//
//    /**
//     * Record task execution time and execution result.
//     *
//     * @param nameable   Current repository instance that implements {@link Nameable}
//     * @param durationMs Task execution duration (milliseconds)
//     * @param success    Whether the task executes successfully
//     * @param throwable  Exception thrown during task execution, pass {@code null} if successful
//     */
//    public static void recordTaskExecute(Nameable nameable, long durationMs, boolean success, Throwable throwable) {
//        if (meterRegistry == null) {
//            return;
//        }
//        Tags tags = buildBaseTags(nameable, success, throwable);
//        Timer.builder(METRIC_PREFIX + ".execute.duration")
//                .tags(tags)
//                .register(meterRegistry)
//                .record(durationMs, TimeUnit.MILLISECONDS);
//    }
//
//    /**
//     * Record task execution timeout count.
//     *
//     * @param nameable Current repository instance that implements {@link Nameable}
//     */
//    public static void recordTaskTimeout(Nameable nameable) {
//        if (meterRegistry == null) {
//            return;
//        }
//        Tags tags = Tags.of(TAG_REPOSITORY, nameable.getName());
//        Counter.builder(METRIC_PREFIX + ".execute.timeout")
//                .tags(tags)
//                .register(meterRegistry)
//                .increment();
//    }
//
//    /**
//     * Build unified base tags: repository name, success flag, exception type.
//     *
//     * @param nameable  Nameable repository instance
//     * @param success   Operation result
//     * @param throwable Exception object
//     * @return Unified tags
//     */
//    private static Tags buildBaseTags(Nameable nameable, boolean success, Throwable throwable) {
//        Tags tags = Tags.of(TAG_REPOSITORY, nameable.getName(), TAG_SUCCESS, String.valueOf(success));
//        if (throwable != null) {
//            String exceptionName = throwable.getClass().getSimpleName();
//            tags = tags.and(TAG_EXCEPTION, exceptionName);
//        }
//        return tags;
//    }
//}
