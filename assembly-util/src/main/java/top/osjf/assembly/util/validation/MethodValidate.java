package top.osjf.assembly.util.validation;

import top.osjf.assembly.util.annotation.NotNull;

import java.util.function.Supplier;

/**
 * The object method self check entry needs to provide a {@link Supplier}
 * that the proxy executes this method, but does not want to throw an
 * exception midway.
 * <p>It only needs to return false when it is not satisfied, and true
 * when all are satisfied.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.4
 */
public interface MethodValidate {

    String name = "getValidate";

    @NotNull
    Supplier<Boolean> getValidate();
}
