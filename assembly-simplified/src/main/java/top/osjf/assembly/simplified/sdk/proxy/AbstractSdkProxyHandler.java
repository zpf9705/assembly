package top.osjf.assembly.simplified.sdk.proxy;

import top.osjf.assembly.simplified.sdk.client.ClientExecutors;
import top.osjf.assembly.simplified.sdk.process.Request;
import top.osjf.assembly.simplified.sdk.process.RequestAttributes;
import top.osjf.assembly.simplified.sdk.process.Response;
import top.osjf.assembly.simplified.support.AbstractMultipleProxySupport;
import top.osjf.assembly.util.annotation.NotNull;
import top.osjf.assembly.util.lang.ArrayUtils;
import top.osjf.assembly.util.lang.StringUtils;

import java.lang.reflect.Method;

/**
 * Inheriting {@link AbstractMultipleProxySupport} implements handing over
 * the object of the jdk dynamic proxy to the spring container for management.
 *
 * <p>When this object is called, the {@link #invoke(Object, Method, Object[])}
 * method will be executed and passed to this abstract class.
 *
 * <p>This class takes on the common parameter processing and converts
 * it into the parameters required for SDK execution.
 *
 * <p>The corresponding executor will be selected based on the full name
 * of a single {@link top.osjf.assembly.simplified.sdk.client.Client},
 * as shown in {@link Request#getClientCls()}.
 *
 * <p>Simply obtain the host parameter from the corresponding proxy class
 * entity to complete the SDK request.
 *
 * <p>For clearer meaning, it was renamed 'AbstractSdkProxyHandler' since 2.2.5.
 *
 * @param <T> The data type of the proxy class.
 * @author zpf
 * @since 1.1.0
 */
public abstract class AbstractSdkProxyHandler<T> extends AbstractMultipleProxySupport<T> implements RequestAttributes {

    private String host;

    @Override
    public void setHost(String host) {
        if (StringUtils.isBlank(host)) throw new IllegalArgumentException(
                "The calling host address of the SDK proxy class cannot be empty");
        this.host = host;
    }

    @Override
    public String getHost() {
        return host;
    }

    @Override
    public Object handle(Object proxy, Method method, Object[] args) {
        return handle0(proxy, method, args);
    }

    /**
     * The parameter processing logic for Jdk dynamic proxy callbacks can be
     * rewritten by subclasses, defined according to their own situation.
     * <p>Here, a default processing posture that conforms to SDK is provided.
     *
     * @param proxy  Proxy obj.
     * @param method Access method.
     * @param args   transfer args.
     * @return Proxy method solve result.
     */
    protected Object handle0(@SuppressWarnings("unused") Object proxy, Method method, Object[] args) {
        //The method of the parent class Object is not given
        Class<T> type = getType();
        if (!type.getName().equals(method.getDeclaringClass().getName())) {
            throw new UnsupportedOperationException(
                    "Only methods defined by class " + type.getName() + " are supported.");
        }
        //The request parameter encapsulation must exist.
        if (ArrayUtils.isEmpty(args)) {
            throw new IllegalArgumentException(
                    "The input parameter of the SDK encapsulation class call method cannot be empty."
            );
        }
        //And all encapsulated as one parameter.
        Object param = args[0];
        //Determine whether it is a request parameter and whether the
        //class object that returns the value is a subclass of response.
        if (!(param instanceof Request) || !Response.class.isAssignableFrom(method.getReturnType())) {
            throw new IllegalArgumentException(
                    "Determine whether it is a request parameter and whether the" +
                            "class object that returns the value is a subclass of response"
            );
        }
        return doRequest((Request<?>) param);
    }

    /**
     * Use {@link top.osjf.assembly.simplified.sdk.client.Client} for the
     * next name routing operation.
     *
     * @param request Think of {@link Request#getClientCls()}.
     * @return The result set of this request is encapsulated in {@link Response}.
     */
    protected Response doRequest(@NotNull Request<?> request) {
        //private perm
        //change protected
        //son class can use
        return ClientExecutors.executeRequestClient(this::getHost, request);
    }
}
