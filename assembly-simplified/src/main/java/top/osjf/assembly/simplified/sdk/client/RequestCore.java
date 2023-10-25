package top.osjf.assembly.simplified.sdk.client;

import top.osjf.assembly.simplified.sdk.process.Response;

/**
 * The request method function interface, {@link #request()}, is the entry point for the request.
 *
 * <p>This method does not have design parameters. In order to facilitate the construction of static schemes,
 * the parameters were initially encapsulated in the thread class to better manage the current parameters.
 *
 * <p>The current parameters can be obtained at any time in various parts of the cycle, which facilitates
 * subsequent parameter extension operations
 * @param <R> Implement a unified response class data type.
 * @author zpf
 * @since 1.1.0
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
