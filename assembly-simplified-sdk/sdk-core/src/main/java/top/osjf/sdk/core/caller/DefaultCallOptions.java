package top.osjf.sdk.core.caller;

import java.lang.annotation.Annotation;

/**
 * <p>
 * {@code DefaultCallOptions} is a class used to bridge annotation type data.
 * It implements the {@link CallOptions} annotation interface and provides default configuration options,
 * Allow users to customize these options through setter methods.
 * </p>
 * <p>
 * <strong>Attention:</strong>
 * The behavior of implementing annotation interfaces in this class is special, and it is generally not
 * recommended for regular classes to implement annotation interfaces.
 * This design is mainly used within the framework for dynamically generating or proxy annotation instances.
 * If you are developing your own code, please follow the Java standard and separate annotations and classes.
 * </p>
 *
 * <h2>Main purpose</h2>
 * <ul>
 * <li>As the default implementation of the {@code CallOptions} annotation,provides default configuration</li>
 * <li>Support dynamic modification of configuration options and updating property values through setter methods</li>
 * <li>Within the framework, it may be used to generate or proxy annotation instances</li>
 * </ul>
 *
 * <h2>Design Background</h2>
 * <p>
 * Annotation interfaces are typically used to declare metadata rather than providing logical implementation.
 * However, in some frameworks, a class may be required to dynamically generate annotation instances or provide
 * default values.This type is designed to meet this demand.
 * </p>
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.3
 */
public class DefaultCallOptions implements CallOptions {

    private int retryTimes = 1;
    private long retryIntervalMilliseconds = 1000;
    private Class<? extends ThrowablePredicate> retryThrowablePredicateClass = DefaultThrowablePredicate.class;
    private boolean whenResponseNonSuccessRetry = true;
    private boolean whenResponseNonSuccessFinalThrow = true;
    private Class<? extends Callback> callbackClass = DefaultCallback.class;
    private boolean onlyUseProvidedCallback = false;
    private Class<? extends AsyncPubSubExecutorProvider> pubSubExecutorProviderClass
            = DefaultAsyncPubSubExecutorProvider.class;

    /**
     * The default empty construct provides default properties for calling configuration,
     * but can still be modified for property configuration through the enumerated set method.
     */
    public DefaultCallOptions() {
    }

    /**
     * @param retryTimes {@link CallOptions#retryTimes()}
     */
    public void setRetryTimes(int retryTimes) {
        this.retryTimes = retryTimes;
    }

    /**
     * @param retryIntervalMilliseconds {@link CallOptions#retryIntervalMilliseconds()}
     */
    public void setRetryIntervalMilliseconds(long retryIntervalMilliseconds) {
        this.retryIntervalMilliseconds = retryIntervalMilliseconds;
    }

    /**
     * @param retryThrowablePredicateClass {@link CallOptions#retryThrowablePredicateClass()}
     */
    public void setRetryThrowablePredicateClass(Class<? extends ThrowablePredicate> retryThrowablePredicateClass) {
        this.retryThrowablePredicateClass = retryThrowablePredicateClass;
    }

    /**
     * @param whenResponseNonSuccessRetry {@link CallOptions#whenResponseNonSuccessRetry()}
     */
    public void setWhenResponseNonSuccessRetry(boolean whenResponseNonSuccessRetry) {
        this.whenResponseNonSuccessRetry = whenResponseNonSuccessRetry;
    }

    /**
     * @param whenResponseNonSuccessFinalThrow {@link CallOptions#whenResponseNonSuccessFinalThrow()}
     */
    public void setWhenResponseNonSuccessFinalThrow(boolean whenResponseNonSuccessFinalThrow) {
        this.whenResponseNonSuccessFinalThrow = whenResponseNonSuccessFinalThrow;
    }

    /**
     * @param callbackClass {@link CallOptions#callbackClass()}
     */
    public void setCallbackClass(Class<? extends Callback> callbackClass) {
        this.callbackClass = callbackClass;
    }

    /**
     * @param onlyUseProvidedCallback {@link CallOptions#onlyUseProvidedCallback() }
     */
    public void setOnlyUseProvidedCallback(boolean onlyUseProvidedCallback) {
        this.onlyUseProvidedCallback = onlyUseProvidedCallback;
    }

    /**
     * @param pubSubExecutorProviderClass {@link CallOptions#pubSubExecutorProviderClass()}
     */
    public void setPubSubExecutorProviderClass(Class<? extends AsyncPubSubExecutorProvider> pubSubExecutorProviderClass) {
        this.pubSubExecutorProviderClass = pubSubExecutorProviderClass;
    }

    @Override
    public int retryTimes() {
        return retryTimes;
    }

    @Override
    public long retryIntervalMilliseconds() {
        return retryIntervalMilliseconds;
    }

    @Override
    public Class<? extends ThrowablePredicate> retryThrowablePredicateClass() {
        return retryThrowablePredicateClass;
    }

    @Override
    public boolean whenResponseNonSuccessRetry() {
        return whenResponseNonSuccessRetry;
    }

    @Override
    public boolean whenResponseNonSuccessFinalThrow() {
        return whenResponseNonSuccessFinalThrow;
    }

    @Override
    public Class<? extends Callback> callbackClass() {
        return callbackClass;
    }

    @Override
    public boolean onlyUseProvidedCallback() {
        return onlyUseProvidedCallback;
    }

    @Override
    public Class<? extends AsyncPubSubExecutorProvider> pubSubExecutorProviderClass() {
        return pubSubExecutorProviderClass;
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return CallOptions.class;
    }
}
