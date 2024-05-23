package top.osjf.assembly.simplified.support;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.cglib.proxy.Callback;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.lang.Nullable;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
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
     * The target type of dynamic proxy.
     */
    private Class<T> type;

    /**
     * Is the object managed by this factory a singleton.
     */
    public boolean isSingleton = true;

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
     * Set the object managed by this factory a singleton.
     *
     * @param singleton the object managed by this factory a singleton.
     */
    public void setSingleton(boolean singleton) {
        isSingleton = singleton;
    }

    @Override
    public Class<?> getObjectType() {
        return this.type;
    }

    @Override
    public boolean isSingleton() {
        return isSingleton;
    }

    @Nullable
    @Override
    public T getObject() {
        return getObject0();
    }

    /**
     * Create different proxy objects based on different proxy models.
     * @see AbstractCglibProxySupport#createProxy(Class, Callback)
     * @see AbstractJdkProxySupport#createProxy(Class, InvocationHandler)
     * @return different proxy models.
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
