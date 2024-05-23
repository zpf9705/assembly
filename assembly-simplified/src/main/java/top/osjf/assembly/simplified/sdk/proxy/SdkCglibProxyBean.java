package top.osjf.assembly.simplified.sdk.proxy;

import top.osjf.assembly.simplified.sdk.annotation.EnableSdkProxyRegister;
import top.osjf.assembly.simplified.sdk.annotation.Sdk;
import top.osjf.assembly.simplified.sdk.client.ClientExecutors;
import top.osjf.assembly.simplified.support.ProxyModel;
import top.osjf.assembly.simplified.support.ScanningCandidateImportBeanDefinitionRegistrar;

/**
 * Using the proxy model of cglib to create proxy beans for SDK, this
 * pattern can support types other than interfaces, and implementing
 * this feature is also based on the principle of spring's cglib.
 *
 * <p>It can be said to be a fusion point between the Spring framework and
 * our custom {@link top.osjf.assembly.simplified.sdk.client.Client} scheme.
 * <p>Here is an explanation of the main implementation idea: we first scan the
 * interface classes wearing {@link EnableSdkProxyRegister} in
 * {@link ScanningCandidateImportBeanDefinitionRegistrar} through{@link Sdk}'s
 * switch annotations,and then create dynamic implementation classes for these
 * interfaces through {@link top.osjf.assembly.simplified.sdk.annotation.SdkProxyBeanRegister}.
 *
 * <p>At this point, the implementation class is assigned a proxy object
 * created by the spring cglib dynamic proxy, and the proxy object is handed
 * over to spring as the virtual implementation class for these interfaces or
 * classes (abstract or simple).
 *
 * <p>When these interfaces are called through the spring container, We will
 * uniformly bring the parameters to the proxy object and connect them to our
 * {@link ClientExecutors} processing through this class.
 *
 * @param <T> The data type of the proxy class.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 2.2.5
 */
public class SdkCglibProxyBean<T> extends AbstractSdkProxyHandler<T> {

    /**
     * The construction method for setting the proxy model {@link ProxyModel} to cglib.
     */
    public SdkCglibProxyBean() {
        setProxyModel(ProxyModel.SPRING_CJ_LIB);
    }
}
