package top.osjf.assembly.simplified.sdk.process;

import top.osjf.assembly.simplified.sdk.proxy.SdkProxyBeanDefinition;

/**
 * Regarding the definition property of {@link Request},
 * the aware interface needs to be implemented to set and
 * obtain the corresponding request properties.
 * @see SdkProxyBeanDefinition
 * @author zpf
 * @since 1.1.0
 */
public interface RequestAttributes {

    /**
     * Set the host address required for the SDK request
     * domain, which can be a domain name or an IP address
     * containing a good port.
     * @param host Given request a host address.
     */
    void setHost(String host);

    /**
     * @return A request builder within a host.
     */
    String getHost();
}
