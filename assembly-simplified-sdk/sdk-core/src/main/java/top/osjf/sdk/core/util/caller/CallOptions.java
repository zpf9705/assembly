package top.osjf.sdk.core.util.caller;

import java.lang.annotation.*;

/**
 * Call option annotation, used to configure the calling behavior of methods
 * or classes.
 *
 * <p>This annotation can be used on classes or methods to provide detailed
 * configuration on how to perform specific calls, such as network requests,
 * database operations, etc.
 * It allows developers to specify options such as retry times, retry intervals,
 * retry conditions, and callback handling
 *
 * <p>When this annotation is applied to a method, it will override any values
 * of the same annotation attribute applied to the class containing the method.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.2
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CallOptions {

    /**
     * Get the number of retries.
     *
     * <p>The number of times the call will be attempted to be re-executed when
     * it fails. The default value is 1, and if the conditions are met, retry again.
     *
     * @return The number of retries must be a non-negative integer.
     */
    int retryTimes() default 1;

    /**
     * Get retry interval (in milliseconds).
     *
     * <p>The waiting time between each retry.
     * The default value is 1000 milliseconds (i.e. 1 second).
     *
     * @return retry interval, in milliseconds.
     */
    long retryIntervalMilliseconds() default 1000;

    /**
     * Retrieve the exception condition class used to determine whether a
     * retry is necessary.
     *
     * <p>This is a class that implements the {@code ThrowablePredicate} interface to
     * determine which types of exceptions should trigger retries.
     * <p>
     * Developers can define more specific retry conditions by providing custom
     * {@code ThrowablePredicate} implementations.
     *
     * @return is an exception condition class used to determine whether
     * a retry is necessary.
     */
    Class<? extends ThrowablePredicate> retryThrowablePredicateClass() default ThrowablePredicate.class;

    /**
     * Whether to retry when the response is unsuccessful.
     * <p>
     * If the status code or content of the response indicates that the call
     * was unsuccessful (even if no exception was thrown), should we try to retry.
     * The default value is {@literal true}, which means to retry when the response
     * is unsuccessful.
     *
     * @return The flag indicating whether to retry when the response is unsuccessful.
     */
    boolean whenResponseNonSuccessRetry() default true;

    /**
     * Whether to throw an exception when the response is unsuccessful and
     * the retry attempts are exhausted.
     *
     * <p>If the response is unsuccessful and all configured retry attempts
     * have been made, should an exception be thrown.
     * The default value is {@literal true}, indicating that an exception is
     * thrown in this situation.
     *
     * @return is a flag indicating whether an exception is thrown when the
     * response is unsuccessful and the retry attempts have been exhausted.
     */
    boolean whenResponseNonSuccessFinalThrow() default true;

    /**
     * Get {@code Callback} processing class.
     *
     * <p>This is a class that implements the {@code Callback}  interface,
     * used to handle callback logic when calls succeed or fail.
     *
     * <p>Developers can define more specific callback behaviors by
     * providing custom {@code Callback} implementations.
     *
     * @return is a class used for callback processing.
     */
    Class<? extends Callback> callbackClass() default Callback.class;
}
