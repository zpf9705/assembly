package top.osjf.assembly.sdk.process;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.lang.NonNull;
import top.osjf.assembly.sdk.annotation.SdkProxyBeanDefinitionRegister;

/**
 * When registering a proxy class to the spring container at the interface level,
 * the property fields placed in the host address acquisition interface are completed
 * in {@link SdkProxyBeanDefinitionRegister#beanRegister(AnnotationAttributes, BeanDefinitionRegistry, AnnotationMetadata)}.
 * <p>When dynamic SDK calls are made, they are retrieved at any time for calling operations.</p>
 *
 * @author zpf
 * @since 1.1.0
 */
public interface HostCapable {

    /**
     * Obtain the requested host address for each SDK.
     *
     * @return Must not be {@literal  null}.
     */
    @NonNull
    String getHost();
}
