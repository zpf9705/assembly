package top.osjf.assembly.simplified.sdk.process;

import top.osjf.assembly.simplified.sdk.annotation.SdkProxyBeanRegister;
import top.osjf.assembly.util.annotation.NotNull;

/**
 * When registering a proxy class to the spring container at the interface
 * level, the property fields placed in the host address acquisition interface
 * are completed in {@link SdkProxyBeanRegister#getBeanDefinitionHolder
 * (AnnotationAttributes, AnnotationMetadata)}.
 *
 * <p>When dynamic SDK calls are made, they are retrieved at any time for \
 * calling operations.
 *
 * @author zpf
 * @since 1.1.0
 */
public interface HostCapable {

    /**
     * Obtain the requested host address for each SDK.
     * @return Must not be {@literal  null}.
     */
    @NotNull
    String getHost();
}
