package top.osjf.assembly.simplified.cron.annotation;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.MergedBeanDefinitionPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.boot.ApplicationArguments;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportAware;
import org.springframework.context.annotation.Role;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;
import top.osjf.assembly.simplified.cron.CronRegister;
import top.osjf.assembly.simplified.support.SmartContextRefreshed;
import top.osjf.assembly.util.annotation.CanNull;
import top.osjf.assembly.util.annotation.NotNull;

import java.util.Objects;

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
 * <p>Compared to {@link CronRegister}, it lacks non container object instantiation
 * operations and only supports timed processing of container objects.
 *
 * @author zpf
 * @since 2.0.6
 */
@Configuration(proxyBeanMethods = false)
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
public class CronTaskRegisterPostProcessor extends SmartContextRefreshed implements ImportAware,
        MergedBeanDefinitionPostProcessor, ApplicationContextAware, EnvironmentAware, Ordered {

    private ApplicationContext context;

    private Environment environment;

    private boolean noMethodDefaultStart;

    @Override
    public void setApplicationContext(@NotNull ApplicationContext context) throws BeansException {
        this.context = context;
    }

    @Override
    public void setEnvironment(@NotNull Environment environment) {
        this.environment = environment;
    }

    @Override
    public void setImportMetadata(@NotNull AnnotationMetadata metadata) {
        AnnotationAttributes attributes =
                AnnotationAttributes.fromMap(metadata
                        .getAnnotationAttributes(EnableCronTaskRegister2.class.getName()));
        Objects.requireNonNull(attributes, EnableCronTaskRegister2.class.getName()
                + " analysis failed.");
        noMethodDefaultStart = attributes.getBoolean("noMethodDefaultStart");
    }

    @Override
    public void postProcessMergedBeanDefinition(@NotNull RootBeanDefinition beanDefinition, @NotNull Class<?> beanType,
                                                @NotNull String beanName) {
    }

    @CanNull
    @Override
    public Object postProcessAfterInitialization(@NotNull Object bean, @NotNull String beanName) throws BeansException {
        CronRegister.registerWthBean(bean, environment);
        return bean;
    }

    @Override
    public void onApplicationEvent(@NotNull ContextRefreshedEvent event) {
        String[] sourceArgs = context.getBean(ApplicationArguments.class).getSourceArgs();
        if (CronRegister.registerZero()) {
            if (noMethodDefaultStart) {
                CronRegister.start(sourceArgs);
            }
        } else {
            CronRegister.start(sourceArgs);
        }
    }

    @NotNull
    @Override
    public ApplicationContext nowApplicationContext() {
        return context;
    }

    @Override
    public int getOrder() {
        return Integer.MIN_VALUE + 16;
    }
}
