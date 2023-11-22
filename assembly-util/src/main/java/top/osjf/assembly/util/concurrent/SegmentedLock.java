package top.osjf.assembly.util.concurrent;

import top.osjf.assembly.util.annotation.KeepThreadSafe;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * A thread safe lock designed for multi object and multithreaded access,
 * based on {@link Lock}, ensures that multiple objects can have one to ensure
 * thread safety for their own access.
 *
 * <p>Compatible with all APIs of {@link Lock}, provided it carries known
 * objects and can call known methods.
 *
 * <pre>
 *     public class Test {
 *
 *     static final SegmentedLock&lt;String&gt; lock = new ObjectSegmentedLock&lt;&gt;();
 *
 *     public String test0(String id) throws NoSegmentedLockException {
 *
 *         //using same as lock
 *         lock.addDefaultLockIfAbsent(id);
 *
 *         if (lock.tryLock(id)) {
 *
 *             try {
 *
 *                 // you do anyTing ;
 *
 *             }finally {
 *
 *                 //must remember unlock
 *
 *                 lock.unLock(id);
 *             }
 *         }
 *
 *         return id;
 *     }
 * }
 * </pre>
 * @see Lock
 * @param <T> The object type granting a singleton lock.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.7
 */
@KeepThreadSafe
public interface SegmentedLock<T> {

    /**
     * Add a singleton lock to the incoming object.
     *
     * @param t    The key obtained by the lock,
     *             must not be {@literal null}.
     * @param lock The single lock of the incoming object,
     *             must not be {@literal null}.
     */
    void addLock(T t, Lock lock);

    /**
     * Add a default singleton lock to the incoming object.
     *
     * @param t The key obtained by the lock,
     *          must not be {@literal null}.
     */
    void addDefaultLock(T t);

    /**
     * Add a singleton lock to the incoming object.
     * <p>If there is a singleton lock, it is not responsible for adding it.
     *
     * @param t    The key obtained by the lock,
     *             must not be {@literal null}.
     * @param lock The single lock of the incoming object,
     *             must not be {@literal null}.
     */
    void addLockIfAbsent(T t, Lock lock);

    /**
     * Add a default singleton lock to the incoming object.
     * <p>If there is a singleton lock, it is not responsible for adding it.
     *
     * @param t The key obtained by the lock,
     *          must not be {@literal null}.
     */
    void addDefaultLockIfAbsent(T t);

    /**
     * Add a singleton lock to the incoming object and
     * return lock.
     * <p>If a singleton lock for this object already exists,
     * no new one will be added and the old one will be returned.
     *
     * @param t    The key obtained by the lock,
     *             must not be {@literal null}.
     * @param lock The single lock of the incoming object,
     *             must not be {@literal null}.
     * @return The singleton lock currently held by the incoming object.
     */
    Lock addLockReturned(T t, Lock lock);

    /**
     * Add a default singleton lock to the incoming
     * object and return lock.
     * <p>If a singleton lock for this object already exists,
     * no new one will be added and the old one will be returned.
     *
     * @param t The key obtained by the lock,
     *          must not be {@literal null}.
     * @return The singleton lock currently held by the incoming object.
     */
    Lock addDefaultLockReturned(T t);

    /**
     * Obtain its unique singleton lock based on the provided
     * object.
     * <p>When the lock is not present, an exception that the
     * lock was not discovered will be thrown.
     *
     * @param t The key obtained by the lock,
     *          must not be {@literal null}.
     * @return The singleton lock currently held by the incoming object.
     * @throws NoSegmentedLockException An undetected abnormality
     *                                  in the lock.
     */
    Lock getLock(T t) throws NoSegmentedLockException;

    /**
     * Obtain the singleton lock of the incoming object,
     * and return the default lock when it is not obtained.
     *
     * @param t           The key obtained by the lock,
     *                    must not be {@literal null}.
     * @param defaultLock The default returned lock.
     * @return The singleton lock currently held by the incoming object.
     */
    Lock getOrDefaultLock(T t, Lock defaultLock);

    /**
     * Obtain the corresponding singleton lock based on the
     * object and lock it.
     * <p>The prerequisite for using is to ensure that a
     * corresponding lock already exists for this object,
     * otherwise an exception such as {@link NoSegmentedLockException}
     * will be thrown that the lock was not discovered.
     *
     * @param t The key obtained by the lock,
     *          must not be {@literal null}.
     * @throws NoSegmentedLockException An undetected abnormality
     *                                  in the lock.
     * @see Lock#lock()
     */
    void lock(T t) throws NoSegmentedLockException;

    /**
     * Obtain the corresponding singleton lock based on the
     * object and lock it.
     * <p>The prerequisite for using is to ensure that a
     * corresponding lock already exists for this object,
     * otherwise an exception such as {@link NoSegmentedLockException}
     * will be thrown that the lock was not discovered.
     * <p>When calling this method for a lock operation and the
     * lock time is in a waiting state, the thread can
     * interrupt the waiting process and throw a
     * {@link InterruptedException} exception by calling
     * {@link Thread#interrupt()}.
     *
     * @param t The key obtained by the lock,
     *          must not be {@literal null}.
     * @throws NoSegmentedLockException An undetected abnormality
     *                                  in the lock.
     * @throws InterruptedException     if the current thread is interrupted
     *                                  while acquiring the lock (and interruption of lock
     *                                  acquisition is supported)
     * @see Lock#lockInterruptibly()
     */
    void lockInterruptibly(T t) throws NoSegmentedLockException, InterruptedException;

    /**
     * Obtain the singleton lock of the incoming object.
     * <p>If it is not obtained, throw an exception
     * {@link NoSegmentedLockException}.
     * <p>If it is obtained, try to apply the lock and return
     * a Boolean type indicating the locking result.
     *
     * @param t The key obtained by the lock,
     *          must not be {@literal null}.
     * @return If {@literal true} locked successfully, {@literal false} conversely.
     * @throws NoSegmentedLockException An undetected abnormality
     *                                  in the lock.
     * @see Lock#tryLock()
     */
    boolean tryLock(T t) throws NoSegmentedLockException;


    /**
     * Obtain the singleton lock of the incoming object.
     * <p>If it is not obtained, throw an exception
     * {@link NoSegmentedLockException}.
     * <p>If it is obtained, try to apply the lock and return
     * a Boolean type indicating the locking result.
     * <p>On top of {@link #tryLock(Object)}, a specified waiting
     * time has been added. If the waiting time has passed, it will
     * immediately return {@literal false}.
     * <p>If the waiting time is less than or equal to 0, it will not wait.
     *
     * @param t    The key obtained by the lock,
     *             must not be {@literal null}.
     * @param time the maximum time to wait for the lock.
     * @param unit the time unit of the {@code time} argument.
     * @return If {@literal true} locked successfully, {@literal false} conversely.
     * @throws NoSegmentedLockException An undetected abnormality
     *                                  in the lock.
     * @throws InterruptedException     if the current thread is interrupted
     *                                  while acquiring the lock (and interruption of lock
     *                                  acquisition is supported)
     * @see Lock#tryLock(long, TimeUnit)
     */
    boolean tryLock(T t, long time, TimeUnit unit) throws NoSegmentedLockException, InterruptedException;

    /**
     * Obtain a singleton lock for the incoming object and lock it.
     * <p>When a singleton lock is not obtained, it defaults to
     * adding a singleton lock for the object and locking it.
     * <p>The process of adding locks is thread safe.
     *
     * @param t The key obtained by the lock,
     *          must not be {@literal null}.
     */
    void lockIfAbsent(T t);

    /**
     * Obtain the singleton lock of the incoming object.
     * <p>If it is not obtained, use the provided lock.
     * <p>Note that if it is not obtained, the provided
     * lock cannot be empty, otherwise a null pointer
     * exception {@link NullPointerException} will be thrown.
     *
     * @param t            The key obtained by the lock,
     *                     must not be {@literal null}.
     * @param providerLock If the acquisition is empty, the lock
     *                     you can provide needs to be guaranteed
     *                     not to be {@literal null}.
     */
    void lockIfAbsent(T t, Lock providerLock);

    /**
     * Release the current lock for the incoming object.
     * <p>If the corresponding lock is not obtained, an
     * exception to undetected lock will be thrown.
     * <p>Of course, it is also necessary to ensure that
     * the locking of the object is in the locking stage,
     * otherwise {@link IllegalMonitorStateException } will
     * be thrown.
     *
     * @param t The key obtained by the lock,
     *          must not be {@literal null}.
     * @throws NoSegmentedLockException An undetected abnormality
     *                                  in the lock.
     */
    void unLock(T t) throws NoSegmentedLockException;

    /**
     * Obtain the wait notification component, which is bound to
     * the current lock.
     * <p>The current thread can only call the wait() method of the
     * component after obtaining the lock, and after calling, the
     * current thread will release the lock.
     *
     * @param t The key obtained by the lock,
     *          must not be {@literal null}.
     * @return A new {@link Condition} instance for this {@code Lock} instance.
     * @throws NoSegmentedLockException An undetected abnormality
     *                                  in the lock.
     * @see Lock#newCondition()
     */
    Condition newCondition(T t) throws NoSegmentedLockException;
}
