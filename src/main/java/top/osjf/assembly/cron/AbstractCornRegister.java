package top.osjf.assembly.cron;

import copy.cn.hutool.v_5819.core.collection.CollectionUtil;
import copy.cn.hutool.v_5819.core.util.ArrayUtil;
import copy.cn.hutool.v_5819.logger.StaticLog;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import top.osjf.assembly.cron.annotation.Cron;
import top.osjf.assembly.cron.annotation.CronTaskRegister;
import top.osjf.assembly.cron.annotation.Type;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Abstract registration class, which includes the initial registration and runtime
 * of the timing method. By determining the current startup environment of Spring,
 * the specified environment can be started (directly registering without a specified environment).
 * <p>
 * At the beginning, parameter preparation is carried out, and after the program is started,
 * {@link ApplicationRunner#run(ApplicationArguments)} is used for loading registration.
 * <p>
 * The main purpose is to prevent loading order issues with container beans.
 *
 * @author zpf
 * @since 1.1.0
 */
public abstract class AbstractCornRegister implements InitializingBean, EnvironmentAware, ApplicationContextAware {

    private final List<String> profiles = new ArrayList<>();

    private ApplicationContext applicationContext;

    @Override
    public void setEnvironment(@NonNull Environment environment) {
        profiles.addAll(Arrays.asList(environment.getActiveProfiles()));
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterPropertiesSet() {
        String[] scanPackage = CronTaskRegister.getScanPackage();
        if (ArrayUtil.isEmpty(scanPackage)) {
            if (CronTaskRegister.isNoMethodDefaultStart()) {
                CronRegister.defaultStart();
            } else {
                StaticLog.info("No scanned package paths found");
            }
            return;
        }
        List<Method> methods = CronRegister.getScanMethodsWithCron(scanPackage);
        if (CollectionUtil.isEmpty(methods)) {
            if (CronTaskRegister.isNoMethodDefaultStart()) {
                CronRegister.defaultStart();
            } else {
                StaticLog.info("No method for annotating @Cron was found");
            }
            return;
        }
        //Filter according to the inclusion of the environment
        List<Method> filterMethods = methods.stream().filter(
                m -> {
                    Cron cron = m.getAnnotation(Cron.class);
                    if (ArrayUtil.isNotEmpty(cron.profiles())) {
                        return Arrays.stream(cron.profiles()).anyMatch(profiles::contains);
                    }
                    //no profile args direct register
                    return true;
                }
        ).collect(Collectors.toList());
        //If no methods that can be registered are found, no exceptions will be thrown but a log prompt will be given
        if (CollectionUtil.isEmpty(filterMethods)) {
            StaticLog.info("There is no standardized method for registering scheduled tasks");
            return;
        }
        //Only when there is a timing method will the listener be automatically registered.
        CronRegister.addListenerWithPackages(scanPackage);
        register(filterMethods);
    }

    /**
     * Timed custom registration abstract methods for method groups, implemented separately
     * based on the object's survival mode {@link Type}
     *
     * @param filterMethods Method for registering scheduled tasks for users
     */
    public abstract void register(@NonNull List<Method> filterMethods);

    /**
     * Get Spring Container Context
     *
     * @return {@link ApplicationContext}
     */
    @NonNull
    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    /**
     * Obtain the startup parameters passed by the main method of the Java program
     *
     * @return maybe is {@literal null}
     */
    @Nullable
    public String[] getMainStartupArgs() {
        ApplicationArguments arguments;
        try {
            arguments = getApplicationContext().getBean(ApplicationArguments.class);
        } catch (Exception e) {
            return null;
        }
        return arguments.getSourceArgs();
    }

    /**
     * Register a single scheduled task
     *
     * @param obj    calling Object
     * @param method calling method
     */
    public void singleRegister(Object obj, Method method) {
        CronRegister.register(obj, method);
    }

    /**
     * @see CronRegister#start(String...)
     */
    public void start() {
        CronRegister.start(getMainStartupArgs());
    }
}
