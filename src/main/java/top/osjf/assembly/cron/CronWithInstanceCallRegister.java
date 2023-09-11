package top.osjf.assembly.cron;

import copy.cn.hutool.v_5819.cron.CronException;
import org.springframework.lang.NonNull;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Start the scheduled task of registration in the form of instantiation of each object,
 * and the object of the registration method needs to meet the null parameter construction {@link Class#newInstance()}
 *
 * @author zpf
 * @since 1.1.0
 */
public class CronWithInstanceCallRegister extends AbstractCornRegister {

    @Override
    public void register(@NonNull List<Method> filterMethods) {
        for (Method method : filterMethods) {
            Object instance;
            try {
                instance = method.getDeclaringClass().newInstance();
            } catch (InstantiationException e) {
                throw new CronException("Class name {" + method.getDeclaringClass().getName() + "} not " +
                        "found empty parameter construct cannot be instantiated");
            } catch (IllegalAccessException e) {
                throw new CronException("Class name {" + method.getDeclaringClass().getName() + "} does " +
                        "not have permission to use. Please check the permission modifier so that we can use this class");
            }
            singleRegister(instance, method);
        }
        start();
    }
}
