package top.osjf.assembly.util.rxjava;

import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Flowable;
import top.osjf.assembly.util.lang.StringUtils;
import top.osjf.assembly.util.system.SystemUtils;
import top.osjf.assembly.util.UtilException;
import top.osjf.assembly.util.annotation.CanNull;
import top.osjf.assembly.util.annotation.NotNull;

import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Method call inspector interface , abnormal situation of monitoring method operation.
 *
 * <p>Utilizing the {@link Flowable} specific properties of rxjava3.
 *
 * <p>Need to select appropriate back pressure parameters for its operation，It can be understood
 * as the handling strategy when the {@link BackpressureStrategy} upstream and downstream speeds
 * are inconsistent, You can also go to specific websites to learn more.
 * <a href="https://www.dazhuanlan.com/unclebill/topics/1173554">BackpressureStrategy introduce.</a>
 * @param <T> The type of runtime.
 * @author zpf
 * @since 1.0.0
 */
public interface Observer<T> {

    /**
     * Choose an appropriate backpressure {@link BackpressureStrategy} strategy for the operating method.
     *
     * @return Not be {@literal null}.
     */
    @NotNull
    BackpressureStrategy strategy();

    /**
     * Select the appropriate Thread pool {@link Executor} for production scheduling of producers.
     * If null, run in the main thread  , Please understand rxjava3 before use.
     * <p>
     * The producer's Thread pool is scheduled asynchronously. The consumer consumes on the producer's thread
     * and prints first----
     * <pre>
     *     {@code System.out.println(Thread.currentThread().getName());
     *         Disposable subscribe = Flowable.create(new FlowableOnSubscribe<String>() {
     *                     public void subscribe(@NonNull FlowableEmitter<String> emitter) throws Throwable {
     *                         System.out.println(Thread.currentThread().getName());
     *                         emitter.onNext("123");
     *                         emitter.onComplete();
     *                     }
     *                 }, BackpressureStrategy.DROP)
     *                 .subscribeOn(Schedulers.from(CustomThreadPool.getExecutor()))
     *                 .observeOn(Schedulers.trampoline())
     *                 .subscribe(new Consumer<String>() {
     *                     public void accept(String s) throws Throwable {
     *                         System.out.println(Thread.currentThread().getName());
     *                     }
     *                 }, e -> {
     *                     System.out.println(e.getMessage());
     *                 });
     *         System.out.println("----");}
     * </pre>
     *
     * @return Can be {@literal null}.
     */
    @CanNull
    default Executor subscribeExecutor() {
        return null;
    }

    /**
     * Select the appropriate Thread pool {@link Executor} for Consumer consumption scheduling.
     * If null, run in Producer's scheduling thread , Please understand rxjava3 before use.
     * <p>
     * The producer produces on the main thread, and the consumer Thread pool schedules consumption and prints----
     * <pre>
     *     {@code System.out.println(Thread.currentThread().getName());
     *         Disposable subscribe = Flowable.create(new FlowableOnSubscribe<String>() {
     *                     public void subscribe(@NonNull FlowableEmitter<String> emitter) throws Throwable {
     *                         System.out.println(Thread.currentThread().getName());
     *                         emitter.onNext("123");
     *                         emitter.onComplete();
     *                     }
     *                 }, BackpressureStrategy.DROP)
     *                 .subscribeOn(Schedulers.trampoline())
     *                 .observeOn(Schedulers.from(CustomThreadPool.getExecutor()))
     *                 .subscribe(new Consumer<String>() {
     *                     public void accept(String s) throws Throwable {
     *                         System.out.println(Thread.currentThread().getName());
     *                     }
     *                 }, e -> {
     *                     System.out.println(e.getMessage());
     *                 });
     *         System.out.println("----");}
     * </pre>
     *
     * @return Can be {@literal null}.
     */
    @CanNull
    default Executor observeExecutor() {
        return null;
    }

    /**
     * Set up exception format functions.
     *
     * @return {@link Function} to get message processing results.
     */
    default Function<Throwable, String> formatThrowFunction() {
        return Throwable::getMessage;
    }

    /**
     * Obtain the custom retry count. If it is not set in the system {@link SystemUtils},
     * take it from the system. If it is not set in the system, it defaults to {@code 2} retries.
     *
     * @return Retry times default {@code 2}.
     */
    int getRetryTimes();

    /**
     * Wait time for thread retry when an exception occurs
     *
     * @return wait times and Unit in milliseconds
     */
    long exceptionRetryRestTime();

    /**
     * Specify special retry exceptions. If left blank, all exceptions can be retried.
     *
     * @return Exception clazz array.
     */
    @CanNull
    Class<? extends Throwable>[] specialRetry();

    /**
     * Utilizing the Running Mechanism of rxJava {@link Flowable} , based on the provided operating parameters.
     *
     * <p>Implementation method operation, interface check, information collection, exception retry.
     * <p>If the retry count {@link #getRetryTimes()} is exceeded, the last exception will be thrown.
     *
     * @param run              Run Method Provider.
     * @param type             Success condition assertion.
     * @param check            Operational condition assertion.
     * @param simpleMsgHandler Exception occurrence collection function.
     * @return Ref to {@link Flowable}.
     */
    @NotNull
    default Flowable<T> run(Supplier<T> run, Class<T> type, Predicate<T> check, Function<T, String> simpleMsgHandler) {
        return Flowable.create(click -> {
                    click.onNext(checkValue(run, check, simpleMsgHandler));
                    click.onComplete();
                }, strategy())

                /*
                 * The producer calls the thread pool. If the consumer does not have it, the running
                 * function will be executed asynchronously. The producer thread has been consumed by
                 * the consumer; If the producer has no Thread pool call and the consumer has one, the
                 * running function will use the current thread, and the consumer will execute the Thread
                 * pool asynchronous thread; If neither of them has Thread pool scheduling, the current
                 * thread will be used; If both are available, Thread pool asynchronous operation
                 * is adopted respectively
                 */

                //The producer currently supports a schedule, so a
                .subscribeOn(SpectatorUtils.getSchedulers(subscribeExecutor() == null, subscribeExecutor()))
                //The consumer currently supports a thread scheduling option
                .observeOn(SpectatorUtils.getSchedulers(observeExecutor() == null, observeExecutor()))
                //retry times no need assign ex
                .retry(getRetryTimes(), this::retryWhen)
                //change type
                .ofType(type);
    }

    /**
     * When a method throws an exception during runtime, it can be determined whether
     * it is a specified exception {@link #specialRetry()} based on the provided exception
     * queue to determine whether it is a retry.
     * <p>If {@link #exceptionRetryRestTime()}the interval between exception
     * retries is provided, the thread will retry after resting for these times.
     *
     * @param e Exception thrown by method.
     * @return if {@code true} retry or no retry.
     */
    default boolean retryWhen(Throwable e) {
        return SpectatorUtils.specifyAnException(specialRetry(), exceptionRetryRestTime(), e.getClass());
    }

    /**
     * Handle exceptions uniformly, throw and give rxjava3 for retry.
     * <p>Retries for compatibility exceptions and non success calls.
     *
     * @param run              Run Method Provider {@link Supplier}.
     * @param check            Success condition assertion.
     * @param simpleMsgHandler Exception occurrence collection function.
     * @return Recycling objects.
     */
    default T checkValue(Supplier<T> run, Predicate<T> check, Function<T, String> simpleMsgHandler) {
        T t = null;
        String esg = null;
        try {
            //there is also as runway
            t = run.get();
            boolean test = check.test(t);
            //check no pass give ex
            if (!test) {
                // get no checked pass error msg
                esg = simpleMsgHandler.apply(t);
            }
        } catch (Throwable e) {
            // solve exception
            esg = formatThrowFunction().apply(e);
        }
        //if you have msg , so throw it with retry
        if (StringUtils.isNotBlank(esg)) {
            throw new UtilException(esg);
        }
        return t;
    }
}
