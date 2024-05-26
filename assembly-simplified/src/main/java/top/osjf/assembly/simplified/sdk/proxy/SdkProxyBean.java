package top.osjf.assembly.simplified.sdk.proxy;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.web.context.WebApplicationContext;
import top.osjf.assembly.simplified.sdk.annotation.EnableSdkProxyRegister;
import top.osjf.assembly.simplified.sdk.annotation.Sdk;
import top.osjf.assembly.simplified.sdk.client.ClientExecutors;
import top.osjf.assembly.simplified.support.ProxyModel;
import top.osjf.assembly.simplified.support.ScanningCandidateImportBeanDefinitionRegistrar;

/**
 * A fusion of {@link SdkJDKProxyBean} and {@link SdkCglibProxyBean} is used
 * to directly call the {@link #setProxyModel(ProxyModel)} method in
 * {@link BeanDefinitionBuilder#addPropertyValue(String, Object)} to
 * set the current proxy model.
 *
 * <p>It can be said to be a fusion point between the Spring framework and
 * our custom {@link top.osjf.assembly.simplified.sdk.client.Client} scheme.
 * <p>Here is an explanation of the main implementation idea: we first scan the
 * interface classes wearing {@link EnableSdkProxyRegister} in
 * {@link ScanningCandidateImportBeanDefinitionRegistrar} through{@link Sdk}'s
 * switch annotations,and then create dynamic implementation classes for these
 * interfaces through {@link top.osjf.assembly.simplified.sdk.annotation.SdkProxyBeanRegister}.
 *
 * <p>When these interfaces are called through the spring container, We will
 * uniformly bring the parameters to the proxy object and connect them to our
 * {@link ClientExecutors} processing through this class.
 *
 * <p>When setting the scope of a special bean and inheriting this class, it is
 * necessary to override the constructor {@link #SdkProxyBean(Class)} and provide
 * the corresponding type for setting {@link FactoryBean#getObjectType()}.
 *
 * @param <T> The data type of the proxy class.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 2.2.5
 */
public class SdkProxyBean<T> extends AbstractSdkProxyHandler<T> {

    /**
     * The construction method called when defining the scope of a normal bean
     * , such as {@link org.springframework.beans.factory.config.BeanDefinition#SCOPE_PROTOTYPE}
     * {@link org.springframework.beans.factory.config.BeanDefinition#SCOPE_SINGLETON}.
     */
    public SdkProxyBean() {
    }

    /**
     * When defining a special scope bean, such as {@link WebApplicationContext#SCOPE_REQUEST}
     * {@link WebApplicationContext#SCOPE_APPLICATION} {@link WebApplicationContext#SCOPE_SESSION},
     * call this constructor to pass the type in advance.
     *
     * @param type When injecting beans, the type of teammates is required.
     *             {@link FactoryBean#getObjectType()}.
     */
    public SdkProxyBean(Class<T> type) {
        setType(type);
    }
}
