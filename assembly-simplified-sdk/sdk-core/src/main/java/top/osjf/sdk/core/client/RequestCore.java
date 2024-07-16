package top.osjf.sdk.core.client;

import top.osjf.sdk.core.process.Response;

/**
 * The request method function interface, {@link #request()}, is the
 * entry point for the request.
 *
 * <p>This method does not have design parameters. In order to facilitate
 * the construction of static schemes, the parameters were initially encapsulated
 * in the thread class to better manage the current parameters.
 *
 * <p>The current parameters can be obtained at any time in various parts
 * of the cycle, which facilitates subsequent parameter extension operations.
 *
 * @param <R> Implement a unified response class data type.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
@FunctionalInterface
public interface RequestCore<R extends Response> {

    /**
     * Can freely rewrite the method provided in the form of a request.
     *
     * @return Class object implemented on {@link Response}.
     */
    R request();
}
