package top.osjf.assembly.simplified.init;

/**
 * The post callback operation executed after the initialization
 * logic {@link InitAble#init()} is executed.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 2.2.4
 */
public interface InitAfter {

    /*** Rewrite post callback operation.*/
    void initAfter();
}
