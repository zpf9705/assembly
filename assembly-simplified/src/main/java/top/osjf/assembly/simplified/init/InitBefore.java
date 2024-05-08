package top.osjf.assembly.simplified.init;

/**
 * The pre call operation performed before the initialization
 * logic {@link InitAble#init()} is executed.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 2.2.4
 */
public interface InitBefore {

    /*** Rewrite the pre initialization operation.*/
    void initBefore();
}
