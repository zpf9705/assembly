package top.osjf.assembly.simplified.init;

/**
 * Initialize the loading interface and execute it in stages.
 * Before and after the main initialization method is loaded,
 * logical execution can be defined sequentially.
 *
 * @see top.osjf.assembly.simplified.init.InitBefore
 * @see top.osjf.assembly.simplified.init.InitAble
 * @see top.osjf.assembly.simplified.init.ActionInited
 * @see top.osjf.assembly.simplified.init.InitAfter
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 2.2.4
 */
public interface Init extends InitBefore, InitAble, ActionInited, InitAfter {

    /*** Initialize the name of the main execution method.*/
    String MAIN_METHOD_NAME = "init";

    @Override
    default void init() {
        //Loop initialization is required for execution
        if (canInit()) init0();
    }

    /**
     * Confirm if this initialization can be executed.
     * @return if {@code true} can initialization,otherwise not.
     */
    default boolean canInit(){
        return true;
    }

    /**
     * The logic implementation method for initialization execution.
     * @see #init()
     */
    default void init0(){
        //When using the default logic of init, the logical content you need to implement.
    }

    /**
     * {@inheritDoc}
     */
    @Override
    default void initBefore(){
        // default no super logic.
    }

    /**
     * {@inheritDoc}
     */
    @Override
    default void actionInited(){
        // default no super logic.
    }

    /**
     * {@inheritDoc}
     */
    @Override
    default void initAfter(){
        // default no super logic.
    }
}
