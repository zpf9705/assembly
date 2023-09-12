package top.osjf.assembly.cron;

import copy.cn.hutool.v_5819.cron.CronException;
import org.springframework.beans.BeansException;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import top.osjf.assembly.cron.annotation.Type;
import top.osjf.assembly.utils.rxjava.SpectatorUtils;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Start the registration timer task in the form of each object being retrieved from the Spring container,
 * and the object of the registration method needs to be added to the Spring container
 * <p>
 * There is a problem like this and a solution :
 * <p>
 * The query of bean objects in the spring proxy container adopts the method of loading failure
 * and reloading at intervals due to loading sequence issues. However, this method will not continue
 * to retry because the proxy method {@link Type}
 * does not include the calling object in the spring container, which also needs to be compatible.
 * Therefore, the maximum number of attempts to search for a single bean is proposed to be 5, with an
 * interval of 1 second each time, We also hope that users can adjust the loading order of proxy object beans
 * in proxy mode to the top.
 * <p>
 * I am now solving this problem by adding {@link Order} annotations to such assemblies, which default
 * to the maximum value of {@code int}. This ensures that the last bean is loaded and the bottom method
 * {@link SpectatorUtils#runWhileTrampolineNoConvert(Supplier, Class[], int, long)} is used once to prevent
 * the problem of calling the bean that cannot be found. If it cannot be found, an error prompt will be given
 *
 * @author zpf
 * @since 1.1.0
 */
public class CronWithBeanCallRegister extends AbstractCornRegister {

    private final Map<Class<?>, Object> proxyMap = new HashMap<>();

    @Override
    public void register(@NonNull List<Method> filterMethods) {
        filterMethods.forEach(method -> {
            Object proxy = proxyMap.get(method.getDeclaringClass());
            if (proxy == null) {
                try {
                    proxy = SpectatorUtils.runWhileTrampolineNoConvert(
                            () -> getApplicationContext().getBean(method.getDeclaringClass()),
                            null,
                            5,
                            1500);
                    proxyMap.putIfAbsent(method.getDeclaringClass(), proxy);
                } catch (BeansException e) {
                    throw new CronException("No beans corresponding to {" + method.getDeclaringClass() + "} " +
                            "were found in the spring container");
                }
            }
            singleRegister(proxy, method);
        });
        start();
    }
}
