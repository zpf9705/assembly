package top.osjf.assembly.simplified.support;

import org.springframework.beans.factory.FactoryBean;
import top.osjf.assembly.util.lang.ReflectUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Abstract jdk dynamic proxy method/Spring dynamic registration support class.
 *
 * <p>The process is to first rely on the dynamic proxy {@link InvocationHandler} of
 * JDK to create a proxy object,hand it over to Spring, and when Spring calls the proxy
 * object, execute the {@link #invoke(Object, Method, Object[])} method,which performs
 * unified logical processing and distribution within this method,
 *
 * <p>The method entrusted to Spring to create bean objects is to implement the
 * {@link FactoryBean} interface and dynamically register beans through
 * {@link org.springframework.beans.factory.support.BeanDefinitionRegistry}
 *
 * @param <T> The data type of the proxy class.
 * @author zpf
 * @since 1.1.0
 */
public abstract class AbstractJdkProxySupport<T> extends AbstractClassSupport<T> implements InvocationHandler, FactoryBean<T> {

    @Override
    public boolean isSingleton() {
        return FactoryBean.super.isSingleton();
    }

    @Override
    public T getObject() {
        return ReflectUtils.newProxyInstance(this, getClazz());
    }

    @Override
    public Class<?> getObjectType() {
        return this.clazz;
    }

    @Override
    public abstract Object invoke(Object proxy, Method method, Object[] args) throws Throwable;
}
