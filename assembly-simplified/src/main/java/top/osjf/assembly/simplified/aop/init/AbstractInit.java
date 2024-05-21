package top.osjf.assembly.simplified.aop.init;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Abstract public initialization class, providing default
 * initialization confirmation methods and marking methods
 * after initialization.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 2.2.4
 */
public abstract class AbstractInit implements Init {

    /*** Has it been loaded? Each initialization class has an atomic switch,
     * which is {@code true} when initializing and {@code false} afterwards.*/
    private final AtomicBoolean inited = new AtomicBoolean(false);

    @Override
    public boolean canInit() {
        return (!inited.get() || supportRuntime()) && conditionalJudgment();
    }

    @Override
    public void actionInited() {
        if (!inited.get()) inited.set(true);
    }

    /**
     * This method marks a Boolean return value {@code true}, indicating that
     * the current initialization class can not only be initialized
     * and run, but can also be called freely at runtime.
     * It can be seen as not only an initialization class, but
     * also a regular method class at runtime.
     * Conversely, it is only used for initialization when called.
     *
     * @return Return {@code true} to be called arbitrarily,
     * otherwise only during initialization.
     */
    protected boolean supportRuntime() {
        return false;
    }

    /**
     * Add an executable condition that allows for
     * subsequent initialization operations only when
     * this condition is met.
     *
     * @since 2.2.5
     * @return Return {@code true} Indicates that the current
     * initialization operation can proceed logically.
     * otherwise indicates that initialization is not possible.
     */
    protected boolean conditionalJudgment() {
        return true;
    }
}
