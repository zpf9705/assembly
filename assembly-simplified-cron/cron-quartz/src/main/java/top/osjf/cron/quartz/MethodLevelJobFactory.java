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

import org.quartz.*;
import org.quartz.spi.JobFactory;
import org.quartz.spi.TriggerFiredBundle;
import top.osjf.cron.core.util.ReflectUtils;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Method identity record {@link JobDetail#getKey()} level {@link Job} instance
 * production factory class.
 *
 * <p>The production factory requires that the {@link JobDetail} attribute {@link JobKey}
 * be set when using the {@link Scheduler#scheduleJob} API, with the following setting rules:
 * <ul>
 * <li>{@link JobKey#getName()} set as the name of the execution method.</li>
 * <li>{@link JobKey#getGroup()} set as fully qualified name of the class defining
 * the execution method.</li>
 * </ul>
 *
 * <p>Use cache {@link #JOB_CACHE} for directed caching of {@link Job}, ensuring that object
 * caching is used for subsequent execution after initialization to save memory space. See
 * method {@link #getJob} for details. This method is extensible for subclasses and supported
 * by singleton frameworks.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.3
 */
public class MethodLevelJobFactory implements JobFactory {

    /**
     * Job instance cache.
     * <p>Key is <strong>declaringClassName + @ + methodName</strong>
     * <p>value is {@code Job} instance.
     */
    private final Map<String, Job> JOB_CACHE = new ConcurrentHashMap<>(64);

    @Override
    public final Job newJob(TriggerFiredBundle bundle, Scheduler scheduler) {
        JobDetail jobDetail = bundle.getJobDetail();
        // MethodJob assignable is method level scheduled task
        //Dynamically create beans and execute them
        QuartzUtils.checkJobClassRules(jobDetail.getJobClass());
        JobKey key = jobDetail.getKey();
        //JobKey.name is method name.
        String methodName = key.getName();
        //JobKey.group is declaring class name.
        String declaringClassName = key.getGroup();
        return getJob(declaringClassName, methodName);
    }

    /**
     * Gets a {@code Job} instance by given {@code declaringClassName}
     * and {@code methodName}.
     *
     * <p>Loading condition: Calculate and create only when <strong>declaringClassName + @ + methodName</strong>
     * does not exist, and read the rest directly from the cache {@link #JOB_CACHE}.
     *
     * @param declaringClassName the declaring class name.
     * @param methodName         the method name.
     * @return a {@code Job} instance gets from {@link #JOB_CACHE}.
     */
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