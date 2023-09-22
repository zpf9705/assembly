package top.osjf.assembly.simplified.sdk;

import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.NonNull;

import java.io.Serializable;

/**
 * This class contains all the information about the composition of the SDK, including the host address,
 * the base of the Spring proxy bean implementation class, and the final parameter convergence point of
 * the JDK method dynamic proxy. It can be said to be a fusion point between the Spring framework and
 * our custom {@link top.osjf.assembly.simplified.sdk.client.Client} scheme.
 *
 * <p>Here is an explanation of the main implementation idea: we first scan the interface classes wearing
 * {@link top.osjf.assembly.simplified.sdk.annotation.EnableSdkProxyRegister} in
 * {@link top.osjf.assembly.simplified.support.AbstractProxyBeanInjectSupport} through
 * {@link top.osjf.assembly.simplified.sdk.annotation.Sdk}'s switch annotations,
 * and then create dynamic implementation classes for these interfaces through {}.
 *
 * <p>At this point, the implementation class is given to the proxy objects created by the jdk dynamic
 * proxy, and the proxy objects are handed over to spring as the virtual implementation classes for these interfaces.
 *
 * <p>When these interfaces are called through the spring container, We will uniformly bring the parameters
 * to the proxy object and connect them to our {@link top.osjf.assembly.simplified.sdk.client.ClientExecutors}
 * processing through this class.
 *
 * @author zpf
 * @since 1.1.0
 */
public class SdkProxyBeanDefinition<T> extends AbstractSdkProxyInvoker<T> implements Serializable {

    private static final long serialVersionUID = -4976006670359451017L;

    private String host;

    /**
     * Place the current request host address.
     *
     * @param host Host address no be {@literal null}.
     */
    public void setHost(String host) {
        if (StringUtils.isBlank(host)) throw new IllegalArgumentException(
                "The calling host address of the SDK proxy class cannot be empty");
        this.host = host;
    }

    @NonNull
    @Override
    public String getHost() {
        return host;
    }
}
