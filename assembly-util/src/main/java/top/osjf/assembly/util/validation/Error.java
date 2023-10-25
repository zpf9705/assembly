package top.osjf.assembly.util.validation;

/**
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 */
@FunctionalInterface
public interface Error {

    Error DEFAULT = () -> "An unknown error occurred during model validation.";

    String getError();
}
