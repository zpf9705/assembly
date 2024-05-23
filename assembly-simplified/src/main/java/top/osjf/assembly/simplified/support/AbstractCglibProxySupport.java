package top.osjf.assembly.simplified.support;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.lang.Nullable;
import top.osjf.assembly.util.lang.ReflectUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * <p>If you want to standardize processing, you can access {@link AbstractMultipleProxySupport}.
 *
 * @param <T> The data type of the proxy class.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 2.2.5
 */
public abstract class AbstractCglibProxySupport<T> implements FactoryBean<T>, InvocationHandler {

    private Class<T> type;

    public void setType(Class<T> type) {
        this.type = type;
    }

    public Class<T> getType() {
        return type;
    }

    @Nullable
    @Override
    public Class<?> getObjectType() {
        return type;
    }

    @Override
    public T getObject() {
        return ReflectUtils.newProxyInstance(this, type);
    }

    @Override
    public abstract Object invoke(Object proxy, Method method, Object[] args);
}
