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

package top.osjf.cron.core.listener;

import top.osjf.commons.ability.Nameable;

/**
 * The scheduled task callback interface defines callback methods for task start,
 * successful completion, and failure.
 *
 * <p>This interface aims to provide a standardized callback mechanism for timed task
 * frameworks, allowing developers to execute specific logic at different stages of
 * task execution.
 *
 * <p>By implementing this interface, the framework can trigger corresponding callback
 * methods at the beginning, success, or failure of tasks, allowing developers to implement
 * flexible and scalable task monitoring and processing mechanisms without affecting task
 * execution logic.
 *
 * <p>The three default methods in the interface are {@link #start}, {@link #success}, and
 * {@link #failed} provides developers with a convenient callback entry. These methods are
 * called at different stages of task execution and provide necessary context and error
 * information to the implementation class through the passed {@link ListenerContext} object
 * or exception object. Implementation classes can execute specific listening logic based on
 * this information, such as logging, status updates, notification sending, or error handling.
 *
 * <p>In addition, the interface also defines three abstract methods ({@link #startWithId},
 * {@link #successWithId}, and {@link #failedWithId}), these methods need to be implemented by
 * the implementation class itself. These methods receive the unique identifier (or ID) of the
 * task and (in case of failure) the exception object as parameters, allowing the implementation
 * class to execute more specific listening logic based on the task ID and error information.
 *
 * <p>When designing and implementing a scheduled task system, this interface provides key
 * information transmission and callback execution mechanisms. By implementing this interface,
 * developers can create custom listener classes and trigger corresponding callback methods at
 * different stages of task execution, thereby achieving flexible task monitoring and processing
 * strategies.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
public interface CronListener extends Nameable {

    /**
     * The default callback method executed at the beginning of a
     * scheduled task.
     *
     * <p>This method triggers callback logic by calling the {@link #startWithId}
     * abstract method and passing the unique identifier of the context object.
     *
     * <p>It provides developers with a convenient way to listen for the
     * start event of scheduled tasks without directly implementing the method.
     *
     * @param context listen to the context {@code ListenerContext} object.
     */
    default void start(ListenerContext context) {
        startWithId(context.getID());
    }

    /**
     * The default callback method executed when a scheduled task is successfully
     * completed.
     *
     * <p>This method triggers callback logic by calling the {@link #successWithId}
     * abstract method and passing the unique identifier of the context object.
     *
     * <p>It provides developers with a convenient way to listen for the succeeded
     * event of scheduled tasks without directly implementing the method.
     *
     * @param context listen to the context {@code ListenerContext} object.
     */
    default void success(ListenerContext context) {
        successWithId(context.getID());
    }

    /**
     * The default callback method executed when a scheduled task fails.
     *
     * <p>This method triggers callback logic by calling the {@link #failedWithId}
     * abstract method and passing the unique identifier of the context object and
     * exception object.
     *
     * <p>It provides developers with a convenient way to listen for the failed event
     * of scheduled tasks without directly implementing the method.
     *
     * @param context   listen to the context {@code ListenerContext} object.
     * @param exception the exception object of the failed callback contains specific
     *                  failure information.
     */
    default void failed(ListenerContext context, Throwable exception) {
        failedWithId(context.getID(), exception);
    }

    /**
     * The default callback method executed at the beginning of a scheduled task needs to be
     * defined by the implementation framework's own {@link ListenerContext#getID()}.
     *
     * <p>This method receives a unique identifier as a parameter to identify the timed
     * task that triggers the callback. The implementation class should define specific
     * listening logic in this method, such as logging, updating status, or triggering
     * other related operations.
     *
     * @param id unique identifier for scheduled tasks.
     */
    default void startWithId(String id) { }

    /**
     * The default callback method executed upon successful completion of scheduled tasks requires
     * the implementation framework to define its own {@link ListenerContext#getID()}.
     *
     * <p>This method receives a unique identifier as a parameter to identify the timed task
     * that triggers the callback. The implementation class should define specific listening
     * logic in this method, such as logging, updating status, sending notifications, or
     * triggering other related operations
     *
     * @param id unique identifier for scheduled tasks.
     */
    default void successWithId(String id) { }

    /**
     * The default callback method executed when a scheduled task fails requires the
     * implementation framework to define its own {@link ListenerContext#getID()}.
     *
     * <p>This method receives a unique identifier and an exception message as parameters
     * to identify the timed task that triggers the callback and provide detailed error
     * information. The implementation class should define specific listening logic in
     * this method, such as recording error logs, sending alerts, updating status, or
     * performing other error handling operations.
     *
     * @param id        unique identifier for scheduled tasks.
     * @param exception the exception object of the failed callback contains specific
     *                  failure information.
     */
    default void failedWithId(String id, Throwable exception) { }

    /**
     * Fallback callback executed when the {@link #failed(ListenerContext, Throwable)} method itself throws
     * an exception.
     * <p>
     * This is the final safety barrier to avoid the original task failure being concealed or lost due to
     * runtime exceptions within the normal failure callback logic.
     * <p>
     * To guarantee the highest execution success rate of this fallback method, only the exception object
     * is passed in, without relying on any business context objects that may be abnormal or uninitialized.
     * It is strongly recommended to only implement lightweight logic such as error log recording and emergency
     * operation alerts, and avoid heavy operations including database writing, remote RPC calls and complex
     * business processing.
     *
     * @param exception the exception thrown during the execution of the {@code failed} callback method
     * @since 3.0.2
     */
    default void failedFallback(Throwable exception) { }

    /**
     * Returns an exception propagation strategy for when a listener process encounters an error. The
     * default is {@link ListenerErrorPropagateStrategy#ISOLATE}, which means the listener handles the
     * related errors internally and does not broadcast them to other listeners.
     *
     * @return the exception propagation strategy when an error occurs in the listener process.
     * @since 3.0.2
     */
    default ListenerErrorPropagateStrategy getListenerErrorPropagateStrategy() {
        return ListenerErrorPropagateStrategy.ISOLATE;
    }

    /**
     * Returns the custom tag name of this listener, which defaults to {@link Class#getName()}.
     * @return the custom tag name of this listener.
     * @since 3.0.2
     */
    default String getName() {
        return getClass().getName();
    }
}
