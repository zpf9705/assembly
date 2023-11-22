package top.osjf.assembly.util.concurrent;

import java.util.concurrent.locks.Lock;

/**
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.7
 */
public class NoSegmentedLockException extends RuntimeException {

    private static final long serialVersionUID = 2932539610317483217L;

    private final Object obj;

    public NoSegmentedLockException(Object obj) {
        super(obj + " no having SegmentedLock");
        this.obj = obj;
    }

    public static Lock checkNon(Object obj, Lock lock) throws NoSegmentedLockException {
        if (lock == null) {
            throw new NoSegmentedLockException(obj);
        }
        return lock;
    }

    public Object getObj() {
        return obj;
    }
}
