package top.osjf.assembly.simplified.sdk.proxy;

import org.springframework.beans.factory.FactoryBean;
import top.osjf.assembly.simplified.sdk.annotation.EnableSdkProxyRegister;
import top.osjf.assembly.simplified.sdk.annotation.Sdk;
import top.osjf.assembly.simplified.sdk.client.ClientExecutors;
import top.osjf.assembly.simplified.support.ProxyModel;
import top.osjf.assembly.simplified.support.ScanningCandidateImportBeanDefinitionRegistrar;

/**
 * This class contains all the information about the composition of the SDK,
 * including the host address, the base of the Spring proxy bean implementation
 * class, and the final parameter convergence point of the JDK method dynamic
 * proxy.
 *
 * <p>It can be said to be a fusion point between the Spring framework and
 * our custom {@link top.osjf.assembly.simplified.sdk.client.Client} scheme.
 * <p>Here is an explanation of the main implementation idea: we first scan the
 * interface classes wearing {@link EnableSdkProxyRegister} in
 * {@link ScanningCandidateImportBeanDefinitionRegistrar} through{@link Sdk}'s
 * switch annotations,and then create dynamic implementation classes for these
 * interfaces through {@link top.osjf.assembly.simplified.sdk.annotation.SdkProxyBeanRegister}.
 *
 * <p>At this point, the implementation class is given to the proxy objects
 * created by the jdk dynamic proxy, and the proxy objects are handed over
 * to spring as the virtual implementation classes for these interfaces.
 *
 * <p>When these interfaces are called through the spring container, We will
 * uniformly bring the parameters to the proxy object and connect them to our
 * {@link ClientExecutors} processing through this class.
 *
 * <p>For clearer meaning, it was renamed 'SdkJDKProxyBean', which
 * means that this class implements JDK dynamic proxy to create objects.
 *
 * <p>When setting the scope of a special bean and inheriting this class, it is
 * necessary to override the constructor {@link #SdkJDKProxyBean(Class)} and provide
 * the corresponding type for setting {@link FactoryBean#getObjectType()}.
 *
 * @param <T> The data type of the proxy class.
 * @author zpf
 * @since 1.1.0
 */
public class SdkJDKProxyBean<T> extends AbstractSdkProxyBean<T> {

    /**
     * The construction method for setting the proxy model {@link ProxyModel} to cglib.
     */
    public SdkJDKProxyBean() {
        super();
        setProxyModel(ProxyModel.JDK);
    }

    public SdkJDKProxyBean(Class<T> type) {
        super(type);
        setProxyModel(ProxyModel.JDK);
    }
}
