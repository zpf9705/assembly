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

package top.osjf.cron.core.repository;

import top.osjf.cron.core.util.ReflectUtils;

import java.lang.reflect.Method;

/**
 * The {@code CronMethodRunnable} class implements the Runnable interface,
 * allowing specified methods to be called on specified target objects
 * through reflection mechanisms.
 *
 * <p>This class is particularly suitable for scenarios where a certain object
 * method needs to be executed at regular intervals, such as when scheduling
 * tasks using cron expressions.
 *
 * <p>Copy from org.springframework.scheduling.support.ScheduledMethodRunnable.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.3
 */
public class CronMethodRunnable implements Runnable {

    private final Object target;

    private final Method method;


    /**
     * Create a {@code CronMethodRunnable} for the given target instance,
     * calling the specified method.
     *
     * @param target the target instance to call the method on
     * @param method the target method to call
     */
    public CronMethodRunnable(Object target, Method method) {
        this.target = target;
        this.method = method;
    }

    /**
     * Create a {@code CronMethodRunnable} for the given target instance,
     * calling the specified method by name.
     *
     * @param target     the target instance to call the method on
     * @param methodName the name of the target method
     * @throws NoSuchMethodException if the specified method does not exist
     */
    public CronMethodRunnable(Object target, String methodName) throws NoSuchMethodException {
        this.target = target;
        this.method = target.getClass().getMethod(methodName);
    }


    /**
     * Return the target instance to call the method on.
     *
     * @return the target instance to call the method on.
     */
    public Object getTarget() {
        return this.target;
    }

    /**
     * Return the target method to call.
     *
     * @return the target method to call.
     */
    public Method getMethod() {
        return this.method;
    }


    @Override
    public void run() {
        ReflectUtils.invokeMethod(this.target, this.method);
    }

    @Override
    public String toString() {
        return this.method.getDeclaringClass().getName() + "." + this.method.getName();
    }

}
