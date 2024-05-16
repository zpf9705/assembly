package top.osjf.assembly.simplified.aop.step;

/**
 * This enumeration is used to determine whether the parameter
 * implements or the current target class implements {@link Step}
 * during method execution.
 *
 * <p>If both are implemented, they can be executed together.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 2.2.5
 */
public enum StepSignature {

    /**
     * The parameters of the target method are the execution object that implements {@link Step}.
     */
    ARG,

    /**
     * The real target object is the target executor that achieves {@link Step}.
     */
    TARGET,

    /**
     * Both the parameters and the current target real object have achieved {@link Step} to
     * become the stage executor.
     */
    TOGETHER
}
