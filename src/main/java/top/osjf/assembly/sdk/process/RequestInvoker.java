package top.osjf.assembly.sdk.process;

import org.springframework.lang.NonNull;

import java.util.function.Function;

/**
 * The proxy final execution interface of the {@link Request} parameter, in simple terms,
 * is a request passed in to obtain a response, which is placed in the final execution
 * class and combined with the host domain name for SDK calls.
 *
 * @author zpf
 * @since 1.1.0
 */
public interface RequestInvoker extends Function<Request<?>, Response> {

    @Override
    Response apply(@NonNull Request<?> request);
}
