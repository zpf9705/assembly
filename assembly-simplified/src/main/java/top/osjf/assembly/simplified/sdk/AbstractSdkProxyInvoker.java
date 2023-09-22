package top.osjf.assembly.simplified.sdk;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.lang.NonNull;
import top.osjf.assembly.simplified.sdk.client.ClientExecutors;
import top.osjf.assembly.simplified.sdk.process.HostCapable;
import top.osjf.assembly.simplified.sdk.process.Request;
import top.osjf.assembly.simplified.sdk.process.Response;
import top.osjf.assembly.simplified.support.AbstractJdkProxySupport;

import java.lang.reflect.Method;

/**
 * Inheriting {@link AbstractJdkProxySupport} implements handing over the object of the jdk dynamic proxy
 * to the spring container for management.
 *
 * <p>When this object is called, the {@link #invoke(Object, Method, Object[])} method will be executed
 * and passed to this abstract class.</p>
 *
 * <p>This class takes on the common parameter processing and converts it into the parameters required for
 * SDK execution.</p>
 *
 * <p>The corresponding executor will be selected based on the full name of a single
 * {@link top.osjf.assembly.simplified.sdk.client.Client},
 * as shown in {@link Request#getClientCls()}.
 *
 * <p>Simply obtain the host parameter from the corresponding proxy class entity to complete the SDK request.</p>
 *
 * @author zpf
 * @since 1.1.0
 */
public abstract class AbstractSdkProxyInvoker<T> extends AbstractJdkProxySupport<T> implements HostCapable {

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        return invoke0(proxy, method, args);
    }

    public Object invoke0(Object proxy, Method method, Object[] args) {
        //Request parameters must be passed.
        if (ArrayUtils.isEmpty(args)) throw new IllegalArgumentException(
                String.format("The method %s of proxy class %s must pass the request parameter " +
                        "when executing the sdk option.", proxy.getClass().getName(), method.getName()));
        //Specify that only one request parameter of the encapsulation type is passed.
        Object arg = args[0];
        //Determine if it is of type AbstractRequestParams
        if (!(arg instanceof Request)) throw new IllegalArgumentException(
                "You must encapsulate your SDK request class to inherit from top.osjf.assembly" +
                        ".sdk.process.Request");
        return doNext((Request<?>) arg);
    }

    /**
     * Use {@link top.osjf.assembly.simplified.sdk.client.Client} for the next name routing operation.
     *
     * @param request Think of {@link Request#getClientCls()}.
     * @return The result set of this request is encapsulated in {@link Response}.
     */
    public Response doNext(@NonNull Request<?> request) {
        return ClientExecutors.executeRequestClient(this::getHost, request);
    }

    @Override
    @NonNull
    public abstract String getHost();
}
