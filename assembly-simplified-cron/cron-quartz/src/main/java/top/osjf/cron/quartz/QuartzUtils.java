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
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import top.osjf.cron.core.lang.Nullable;
import top.osjf.cron.core.util.ReflectUtils;
import top.osjf.cron.core.util.StringUtils;

import java.lang.reflect.Method;

/**
 * Coordinate Quartz's easy-to-use tool class and provide relevant
 * static tool methods.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.3
 */
public abstract class QuartzUtils {

    /**
     * This is a rule method check that specifies the framework setting {@link JobDetail#getJobClass()}.
     *
     * <p>This framework is limited by {@link MethodLevelJobFactory} for quartz's {@link Job}
     * factory, so the defined {@link JobDetail#getJobClass()} must be {@link MethodLevelJob} or its subclass.
     *
     * @param jobClass the subsequent execution defines the class object of class {@link Job}.
     * @throws NullPointerException     if input {@code jobClass} is null.
     * @throws IllegalArgumentException if input {@code jobClass} does not meet the above rules.¬
     */
    public static void checkJobClassRules(Class<? extends Job> jobClass) {
        if (!MethodLevelJob.class.isAssignableFrom(jobClass)) {
            throw new IllegalArgumentException
                    ("The attribute <org.quartz.JobDetail#getJobClass> must be " +
                            "<top.osjf.cron.quartz.MethodLevelJob> or its subclass.");
        }
    }

    /**
     * This is a rule method check that specifies the framework setting {@link JobKey}.
     *
     * <p>This framework is limited by {@link MethodLevelJobFactory} for quartz's {@link Job}
     * factory, so the defined {@link JobKey} must meet the following conditions:
     * <ul>
     * <li>{@link JobKey#getGroup()} must satisfy the fully qualified name of the defined class,
     * i.e. {@link Class#getName()}, and be a class that can currently be found by {@code classpath}
     * .</li>
     * <li>{@link JobKey#getName()} must be the name of the method that defines the class dependency,
     * i.e. {@link Method#getName()}, and is a method that can be found by the current defined class.
     * Please refer to {@link ReflectUtils#getMethod} for specific search rules.</li>
     * </ul>
     *
     * @param key the setting {@link JobKey}.
     * @throws NullPointerException     if input {@link JobKey} is null.
     * @throws IllegalArgumentException If input {@link JobKey} does not meet the above rules.
     * @throws IllegalStateException    If the relevant attributes of {@link JobKey} cannot be found.
     */
    public static void checkJobKeyRules(JobKey key) {
        String declaringClassName = key.getGroup();
        if (StringUtils.isBlank(declaringClassName)) {
            throw new IllegalArgumentException
                    ("The attribute <org.quartz.JobKey#group> of <org.quartz.JobKey> is required and is" +
                            " a fully qualified name for the existing class.");
        }
        String methodName = key.getName();
        if (StringUtils.isBlank(methodName)) {
            throw new IllegalArgumentException
                    ("The attribute <org.quartz.JobKey#name> of <org.quartz.JobKey> is required and is" +
                            " the executable method in class <" + declaringClassName + ">.");
        }
        Class<?> declaringClass;
        try {
            declaringClass = ReflectUtils.forName(declaringClassName);
        } catch (Exception e) {
            throw new IllegalStateException
                    ("The input requirement for the <org.quartz.JobKey#group> attribute of " +
                            "<org.quartz.JobKey> is the fully qualified name of the executing class.");
        }
        try {
            ReflectUtils.getMethod(declaringClass, methodName);
        } catch (Exception e) {
            throw new IllegalStateException
                    ("The input requirement for the <org.quartz.JobKey#name> attribute of " +
                            "<org.quartz.JobKey> is the executable method in class <" + declaringClassName + ">.");
        }
    }

    /**
     * Using the rules of this framework, create a standard {@link JobDetail} instance that meets the
     * requirements of methods {@link #checkJobClassRules} and {@link #checkJobKeyRules} mentioned above.
     *
     * @param methodName         the method name.
     * @param declaringClassName the declare class name.
     * @return {@link JobDetail} instance after standard build.
     */
    public static JobDetail buildStandardJobDetail(String methodName, String declaringClassName) {
        return JobBuilder.newJob(MethodLevelJob.class).withIdentity(methodName, declaringClassName).build();
    }

    /**
     * Retrieve the target object stored in {@link JobDetail#getJobDataMap()} based on the
     * value of {@link #getJobRunnableIdentity}.
     * <p>
     * Retrieve the basis for viewing {@code MethodLevelJobFactory#setJobData}.
     *
     * @param jobDetail the input resolve {@link JobDetail}.
     * @return The actual {@link Runnable} body for executing the scheduled task.
     * @throws NullPointerException if input {@code JobDetail} is {@literal null}.
     */
    public static Runnable getRunnable(JobDetail jobDetail) {
        JobKey key = jobDetail.getKey();
        String jobRunnableIdentity = getJobRunnableIdentity(key);
        return (Runnable) jobDetail.getJobDataMap().get(jobRunnableIdentity);
    }

    /**
     * Returns a unique identity string formatted according to {@link Job} runnable body.
     *
     * @param jobKey the input resolve {@link JobKey}.
     * @return Tag {@link Job} runnable body as a unique identity string.
     * @throws NullPointerException if input {@code JobKey} is {@literal null}.
     */
    public static String getJobRunnableIdentity(JobKey jobKey) {
        return QuartzUtils.getJobIdentity(jobKey) + "_runnable";
    }

    /**
     * Retrieve the target object stored in {@link JobDetail#getJobDataMap()} based on the
     * value of {@link JobKey#getGroup()}.
     * <p>
     * Retrieve the basis for viewing {@code MethodLevelJobFactory#setJobData}.
     *
     * @param jobDetail the input resolve {@link JobDetail}.
     * @return The actual target instance for executing the scheduled task.
     * @throws NullPointerException if input {@code JobDetail} is {@literal null}.
     */
    @Nullable
    public static Object getTarget(JobDetail jobDetail) {
        JobKey key = jobDetail.getKey();
        return jobDetail.getJobDataMap().get(key.getGroup());
    }

    /**
     * Retrieve the method object stored in {@link JobDetail#getJobDataMap()} based on the
     * value of {@link #getJobIdentity}.
     * <p>
     * Retrieve the basis for viewing {@code MethodLevelJobFactory#setJobData}.
     *
     * @param jobDetail the input resolve {@link JobDetail}.
     * @return The actual method instance for executing the scheduled task.
     * @throws NullPointerException if input {@code JobDetail} is {@literal null}.
     * @throws ClassCastException   if {@code JobDataMap} got obj is not a {@code Method}.
     */
    @Nullable
    public static Method getMethod(JobDetail jobDetail) {
        String jobIdentity = getJobIdentity(jobDetail.getKey());
        Object o = jobDetail.getJobDataMap().get(jobIdentity);
        return o != null ? (Method) o : null;
    }

    /**
     * Returns a unique identity string formatted according to {@link JobKey}.
     *
     * @param jobKey the input resolve {@link JobKey}.
     * @return Tag {@link Job} as a unique identity string.
     * @throws NullPointerException if input {@code JobKey} is {@literal null}.
     */
    public static String getJobIdentity(JobKey jobKey) {
        return jobKey.getName() + "@" + jobKey.getGroup();
    }
}
