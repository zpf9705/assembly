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


package top.osjf.cron.quartz;

import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.spi.JobFactory;
import org.quartz.spi.TriggerFiredBundle;
import top.osjf.cron.core.util.ReflectUtils;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.3
 */
public class MethodLevelJobFactory implements JobFactory {

    private final Map<String, Job> JOB_CACHE = new ConcurrentHashMap<>(64);

    @Override
    public Job newJob(TriggerFiredBundle bundle, Scheduler scheduler) {
        JobDetail jobDetail = bundle.getJobDetail();
        if (!MethodLevelJob.class.isAssignableFrom(jobDetail.getJobClass())) {
            throw new UnsupportedOperationException("Only supports task execution of " +
                    "<top.osjf.cron.quartz.MethodLevelJob> type.");
        }
        JobKey key = jobDetail.getKey();
        String methodName = key.getName();
        String declaringClassName = key.getGroup();
        return getJob(declaringClassName, methodName);
    }

    protected Job getJob(String declaringClassName, String methodName) {
        final String cacheKey = methodName + "@" + declaringClassName;
        return JOB_CACHE.computeIfAbsent(cacheKey, s -> {
            Class<?> declaringClass = ReflectUtils.forName(declaringClassName);
            Object target = ReflectUtils.newInstance(declaringClass);
            Method method = ReflectUtils.getMethod(declaringClass, methodName);
            return new MethodLevelJob(target, method);
        });
    }
}