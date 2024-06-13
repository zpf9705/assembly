package top.osjf.assembly.simplified.sdk.proxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.context.WebApplicationContext;
import top.osjf.assembly.simplified.sdk.SdkUtils;
import top.osjf.assembly.simplified.sdk.client.ClientExecutors;
import top.osjf.assembly.simplified.sdk.process.Request;
import top.osjf.assembly.simplified.sdk.process.RequestAttributes;
import top.osjf.assembly.simplified.sdk.process.Response;
import top.osjf.assembly.simplified.support.AbstractMultipleProxySupport;
import top.osjf.assembly.util.annotation.NotNull;
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
 * <p>For clearer meaning, it was renamed 'AbstractSdkProxyBean' since 2.2.5.
 *
 * @param <T> The data type of the proxy class.
 * @author zpf
 * @since 1.1.0
 */
public abstract class AbstractSdkProxyBean<T> extends AbstractMultipleProxySupport<T> implements RequestAttributes,
        InitializingBean, DisposableBean {

    private final Logger log = LoggerFactory.getLogger(getClass());

    /*** The host address when calling SDK.*/
    private String host;

    /**
     * The construction method called when defining the scope of a normal bean
     * , such as {@link org.springframework.beans.factory.config.BeanDefinition#SCOPE_PROTOTYPE}
     * {@link org.springframework.beans.factory.config.BeanDefinition#SCOPE_SINGLETON}.
     */
    public AbstractSdkProxyBean() {
    }

    /**
     * When defining a special scope bean, such as {@link WebApplicationContext#SCOPE_REQUEST}
     * {@link WebApplicationContext#SCOPE_APPLICATION} {@link WebApplicationContext#SCOPE_SESSION},
     * call this constructor to pass the type in advance.
     *
     * @param type When injecting beans, the type of teammates is required.
     *             {@link FactoryBean#getObjectType()}.
     */
    public AbstractSdkProxyBean(Class<T> type) {
        setType(type);
    }

    @Override
    public void setHost(String host) {
        if (StringUtils.isBlank(host)) {
            throw new IllegalArgumentException("SDK access host address cannot be empty!");
        }
        this.host = host;
    }

    @Override
    public String getHost() {
        return host;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        log.info("SDK proxy bean initialization action,please ask the subclass to rewrite it on its own.");
    }

    @Override
    public Object handle(Object proxy, Method method, Object[] args) {
        return handle0(proxy, method, args);
    }

    @Override
    public void destroy() throws Exception {
        log.info("SDK's proxy bean destruction action,please ask the subclass to rewrite it on its own.");
    }

    /**
     * The parameter processing logic for Jdk dynamic proxy callbacks can be
     * rewritten by subclasses, defined according to their own situation.
     * <p>Here, a default processing posture that conforms to SDK is provided.
     *
     * <p>Since version 2.2.6, support for {@link RequestParameter} and
     * {@link RequestParam} and
     * {@link ResponseData}has
     * been added.
     *
     * @param proxy  Proxy object.
     * @param method The method object executed by the proxy class.
     * @param args   The real parameters executed by the proxy method.
     * @return The result returned by the proxy execution method.
     */
    protected Object handle0(Object proxy, Method method, Object[] args) {
        if (!checkMethodCoverRanger(proxy, getType(), method, args))
            throw new UnsupportedSDKCallBackMethodException(method.getName());
        //Create a request class based on the extension.
        Request<?> request = SdkUtils.invokeCreateRequest(method, args);
        //Execute the request.
        return SdkUtils.getResponse(method, doRequest(request));
    }

    /**
     * Check the scope of direct methods that can be proxied.
     *
     * @param proxy      Proxy object.
     * @param targetType the target type created by this proxy.
     * @param method     The method object executed by the proxy class.
     * @param args       The real parameters executed by the proxy method.
     * @return Returns {@code true}, indicating that it can be executed
     * subsequently, otherwise it is not supported.
     * @since 2.2.5
     */
    protected boolean checkMethodCoverRanger(Object proxy, Class<T> targetType, Method method, Object[] args) {
        return true;
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
