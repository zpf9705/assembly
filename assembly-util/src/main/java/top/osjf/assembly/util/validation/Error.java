package top.osjf.assembly.util.validation;

/**
 * Throw a message to obtain the interface for self checking errors.
 * <p>When using the default, the default message {@link #DEFAULT} will be thrown.
 * <p>For specific usage, please refer to the {@link MethodValidateConstraintValidator}
 * code implementation.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.4
 */
@FunctionalInterface
public interface Error {

    String name = "getError";

    Error DEFAULT = () -> "An unknown error occurred during model validation.";

    String getError();
}
