package top.osjf.assembly.simplified.cron;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.Ordered;
import top.osjf.assembly.simplified.cron.annotation.Cron;
import top.osjf.assembly.util.annotation.CanNull;
import top.osjf.assembly.util.annotation.NotNull;

/**
 * Create a post processor for custom bean elements.
 *
 * <p>This post processor mainly monitors each step of obtaining
 * the container object that has already been created, and determines
 * whether it contains a method to wear {@link Cron} annotations.
 * If so, actively register a {@link Runnable} and skip it.
 *
 * <p>The end of execution of {@link #postProcessAfterInitialization(Object, String)}
 * also indicates that the spring container has been refreshed, and all
 * container beans have been placed properly.
 * At this time, it receives the notification of the refreshed event sent
 * by {@link ContextRefreshedEvent} to start the thread operation of the
 * scheduled task.
 *
 * @author zpf
 * @since 2.0.6
 */
public class CronTaskRegisterPostProcessor implements BeanPostProcessor, ApplicationListener<ContextRefreshedEvent>,
        ApplicationContextAware, Ordered {

    private ApplicationContext context;

    @Override
    public void setApplicationContext(@NotNull ApplicationContext context) throws BeansException {
        this.context = context;
    }

    @CanNull
    @Override
    public Object postProcessAfterInitialization(@NotNull Object bean, @NotNull String beanName) throws BeansException {
        CronRegister.registerWthBean(bean);
        return bean;
    }

    @Override
    public void onApplicationEvent(@NotNull ContextRefreshedEvent event) {
        if (context == event.getApplicationContext()) {
            CronRegister.start(context.getBean(ApplicationArguments.class).getSourceArgs());
        }
    }

    @Override
    public int getOrder() {
        return Integer.MIN_VALUE + 16;
    }
}
