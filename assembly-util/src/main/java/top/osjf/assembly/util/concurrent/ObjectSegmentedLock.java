package top.osjf.assembly.util.concurrent;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * An implementation of {@link SegmentedLock}.
 *
 * <p>Use {@link ConcurrentHashMap} to record the singleton lock of each
 * incoming object, and use {@link ConcurrentHashMap}'s multiple APIs to
 * provide a singleton lock for each incoming object while ensuring thread
 * safety.
 *
 * @param <T> The object type granting a singleton lock.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.7
 */
public class ObjectSegmentedLock<T> implements SegmentedLock<T>, Serializable {

    private static final long serialVersionUID = 8251075130892933342L;

    private final ConcurrentHashMap<T, Lock> delegate = new ConcurrentHashMap<>(16);

    public ObjectSegmentedLock() {
    }

    @Override
    public void addLock(T t, Lock lock) {
        delegate.put(t, lock);
    }

    @Override
    public void addDefaultLock(T t) {
        delegate.put(t, new ReentrantLock(true));
    }

    @Override
    public void addLockIfAbsent(T t, Lock lock) {
        delegate.putIfAbsent(t, lock);
    }

    @Override
    public void addDefaultLockIfAbsent(T t) {
        delegate.putIfAbsent(t, new ReentrantLock(true));
    }

    @Override
    public Lock addLockReturned(T t, Lock lock) {
        addLockIfAbsent(t, lock);
        return delegate.get(t);
    }

    @Override
    public Lock addDefaultLockReturned(T t) {
        addDefaultLockIfAbsent(t);
        return delegate.get(t);
    }

    @Override
    public void lock(T t) throws NoSegmentedLockException {
        Lock lock = NoSegmentedLockException.checkNon(t, delegate.get(t));
        lock.lock();
    }

    @Override
    public void lockInterruptibly(T t) throws NoSegmentedLockException, InterruptedException {
        Lock lock = NoSegmentedLockException.checkNon(t, delegate.get(t));
        lock.lockInterruptibly();
    }

    @Override
    public boolean tryLock(T t) throws NoSegmentedLockException {
        Lock lock = NoSegmentedLockException.checkNon(t, delegate.get(t));
        return lock.tryLock();
    }

    @Override
    public boolean tryLock(T t, long time, TimeUnit unit) throws NoSegmentedLockException, InterruptedException {
        Lock lock = NoSegmentedLockException.checkNon(t, delegate.get(t));
        return lock.tryLock(time, unit);
    }

    @Override
    public Lock getLock(T t) throws NoSegmentedLockException {
        return NoSegmentedLockException.checkNon(t, delegate.get(t));
    }

    @Override
    public Lock getOrDefaultLock(T t, Lock defaultLock) {
        return delegate.getOrDefault(t, defaultLock);
    }

    @Override
    public void lockIfAbsent(T t) {
        Lock lock = addDefaultLockReturned(t);
        lock.lock();
    }

    @Override
    public void lockIfAbsent(T t, Lock providerLock) {
        Lock lock = addLockReturned(t, providerLock);
        lock.lock();
    }


    @Override
    public void unLock(T t) throws NoSegmentedLockException {
        Lock lock = NoSegmentedLockException.checkNon(t, delegate.get(t));
        lock.unlock();
    }

    @Override
    public Condition newCondition(T t) throws NoSegmentedLockException {
        Lock lock = NoSegmentedLockException.checkNon(t, delegate.get(t));
        return lock.newCondition();
    }
}
