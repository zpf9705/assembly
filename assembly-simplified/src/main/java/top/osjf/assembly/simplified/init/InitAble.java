package top.osjf.assembly.simplified.init;

/**
 * An interface that can be initialized, provide an initialization
 * method {@link #init()}, implement this interface, and hope that
 * the implementation class is an initialization class.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 2.2.4
 */
public interface InitAble {

    /*** Rewrite initialization logic.*/
    void init();
}
