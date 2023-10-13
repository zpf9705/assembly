package top.osjf.assembly.simplified.cron.annotation;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;
import top.osjf.assembly.simplified.cron.CronRegister;
import top.osjf.assembly.util.annotation.CanNull;
import top.osjf.assembly.util.annotation.NotNull;

/**
 * Create a post processor for custom bean elements.
 *
 * <p>This post processor mainly monitors each step of obtaining the container
 * object that has already been created, and determines whether it contains a
 * method to wear {@link Cron} annotations.
 * If so, actively register a {@link Runnable} and skip it.
 *
 * <p>The end of execution of {@link #postProcessAfterInitialization(Object, String)}
 * also indicates that the spring container has been refreshed, and all container beans
 * have been placed properly.
 * At this time, it receives the notification of the refreshed event sent by {@link
 * ContextRefreshedEvent} to start the thread operation of the scheduled task.
 *
 * @author zpf
 * @since 2.0.6
 */
public class CronTaskRegisterPostProcessor implements BeanPostProcessor, ApplicationListener<ContextRefreshedEvent>,
        ApplicationContextAware, EnvironmentAware, Ordered {

    private final boolean noMethodDefaultStart;

    public CronTaskRegisterPostProcessor(boolean noMethodDefaultStart) {
        this.noMethodDefaultStart = noMethodDefaultStart;
    }

    private ApplicationContext context;

    private Environment environment;

    protected static final String NAME = "Simplified-CronTaskRegisterPostProcessor";

    @Override
    public void setApplicationContext(@NotNull ApplicationContext context) throws BeansException {
        this.context = context;
    }

    @Override
    public void setEnvironment(@NotNull Environment environment) {
        this.environment = environment;
    }

    @CanNull
    @Override
    public Object postProcessAfterInitialization(@NotNull Object bean, @NotNull String beanName) throws BeansException {
        CronRegister.registerWthBean(bean, environment);
        return bean;
    }

    @Override
    public void onApplicationEvent(@NotNull ContextRefreshedEvent event) {
        if (context == event.getApplicationContext()) {
            String[] sourceArgs = context.getBean(ApplicationArguments.class).getSourceArgs();
            if (CronRegister.registerZero()) {
                if (noMethodDefaultStart) {
                    CronRegister.start(sourceArgs);
                }
            } else {
                CronRegister.start(sourceArgs);
            }
        }
    }

    @Override
    public int getOrder() {
        return Integer.MIN_VALUE + 16;
    }
}