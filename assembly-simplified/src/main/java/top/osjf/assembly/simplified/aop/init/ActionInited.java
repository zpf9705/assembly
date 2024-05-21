package top.osjf.assembly.simplified.aop.init;

/**
 * Whether to initialize the marking interface, specifically
 * how to mark the current initialization progress, and
 * define it in the implementation class.
 *
 * @see AbstractInit
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 2.2.4
 */
public interface ActionInited {

    /*** Rewrite the marking logic after initialization.*/
    void actionInited();
}
