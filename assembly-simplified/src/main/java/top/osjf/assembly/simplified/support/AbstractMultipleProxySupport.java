package top.osjf.assembly.simplified.support;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.cglib.proxy.Callback;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.lang.Nullable;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * The functionality of this abstract class is based on a fusion version
 * of {@link AbstractJdkProxySupport} and {@link AbstractCglibProxySupport}.
 *
 * <p>For the convenience of encapsulation, a unified function is written based
 * on the input {@link ProxyModel} model for unified routing creation. If you
 * need to understand the proxy model, you can go to the abstract class above
 * for specific understanding.
 *
 * <p>The callbacks for cglib and jdk dynamic proxies have all been integrated
 * into {@link ProxyHandler}, allowing both proxies to use the same callback,
 * facilitating subsequent proxy execution calls.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @see AbstractJdkProxySupport
 * @see AbstractCglibProxySupport
 * @since 2.2.5
 */
public abstract class AbstractMultipleProxySupport<T> implements FactoryBean<T>, MethodInterceptor,
        InvocationHandler, ProxyHandler {

    /**
     * The default proxy mode, JDK dynamic proxy.
     */
    private static final ProxyModel DEFAULT_PROXY_MODEL = ProxyModel.JDK;

    /**
     * Dynamic proxy model.
     */
    private ProxyModel proxyModel = DEFAULT_PROXY_MODEL;

    /**
     * The proxy object created.
     */
    private T proxy;

    /**
     * The target type of dynamic proxy.
     */
    private Class<T> type;

    /**
     * Is the object managed by this factory a singleton.
     */
    private boolean isSingleton = true;

    /**
     * Set the target type for this proxy creation.
     *
     * @param type the target type for this proxy creation.
     */
    public void setType(Class<T> type) {
        this.type = type;
    }

    /**
     * Return the target type created by this proxy.
     *
     * @return the target type created by this proxy.
     */
    public Class<T> getType() {
        return type;
    }

    /**
     * Set the model enumeration for this proxy.
     *
     * @param proxyModel the model enumeration for this proxy.
     */
    public void setProxyModel(ProxyModel proxyModel) {
        this.proxyModel = proxyModel;
    }

    /**
     * Return the model enumeration for this proxy.
     * @since 2.2.7
     * @return the model enumeration for this proxy.
     */
    public ProxyModel getProxyModel() {
        return proxyModel;
    }

    /**
     * Set the object managed by this factory a singleton.
     *
     * @param singleton the object managed by this factory a singleton.
     */
    public void setSingleton(boolean singleton) {
        isSingleton = singleton;
    }

    @Nullable
    @Override
    public Class<?> getObjectType() {
        return type;
    }

    @Override
    public boolean isSingleton() {
        return isSingleton;
    }

    @Nullable
    @Override
    public T getObject() {
        if (proxy != null) {
            return proxy;
        }
        proxy = getObject0();
        return proxy;
    }

    /**
     * Create different proxy objects based on different proxy models.
     *
     * @return different proxy models.
     * @see AbstractCglibProxySupport#createProxy(Class, Callback)
     * @see AbstractJdkProxySupport#createProxy(Class, InvocationHandler)
     */
    private T getObject0() {
        T proxy;
        switch (proxyModel) {
            case JDK:
                proxy = AbstractJdkProxySupport.createProxy(type, this);
                break;
            case SPRING_CJ_LIB:
                proxy = AbstractCglibProxySupport.createProxy(type, this);
                break;
            default:
                proxy = null;
                break;
        }
        return proxy;
    }

    @Override
    public Object intercept(Object proxy, Method method, Object[] args, MethodProxy methodProxy) {
        return handle(proxy, method, args);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        return handle(proxy, method, args);
    }

    @Override
    public abstract Object handle(Object obj, Method method, Object[] args);
}
