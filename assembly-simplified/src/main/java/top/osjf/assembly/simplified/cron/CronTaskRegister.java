package top.osjf.assembly.simplified.cron;

import org.springframework.boot.ConfigurableBootstrapContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;
import top.osjf.assembly.simplified.cron.annotation.Cron;
import top.osjf.assembly.simplified.cron.annotation.EnableCronTaskRegister;
import top.osjf.assembly.util.annotation.NotNull;

import java.util.Objects;

/**
 * Registration help class for scheduled tasks.
 *
 * <p>Describe the general implementation process: Based on the relevant registration information
 * provided by {@link EnableCronTaskRegister}, find the corresponding method for {@link Cron},
 * filter it, temporarily store the corresponding task method in {@link CronRegister}, wait for
 * the spring boot listener to call {@link SpringApplicationRunListener#starting(ConfigurableBootstrapContext)},
 * execute the {@link #starting(ConfigurableBootstrapContext)} method in this class, register the
 * scheduled thread for executing the task, and after completion, wait for the spring boot listener
 * to call the {@link SpringApplicationRunListener#running(ConfigurableApplicationContext)} method
 * to officially start the scheduled task.
 *
 * <p>Reason for using {@link SpringApplicationRunListener}:
 *
 * <p>1. There is a problem with the loading order of dependent beans.
 * If you directly load and use it, it may be that the method's beans cannot be registered due
 * to being loaded later.
 *
 * <p>2. If spring container is not added, timing support needs to be added. If static
 * {@link org.springframework.context.ApplicationContext} is used to obtain beans in the method,
 * it is likely to cause null pointer exceptions {@link NullPointerException} during registration.
 *
 * <p>In order to perfectly solve the above problems, a startup listener was selected to register
 * and start scheduled tasks in the start and run stages, respectively. At this time, all beans
 * have been registered in the container and there will be no loading order issues.
 *
 * <p>Implementation {@link EnvironmentPostProcessor} provides task methods to scan the application
 * default when the package path is not provided.{@link Class#getPackage()}</p>
 *
 * @deprecated See {@link top.osjf.assembly.simplified.cron.annotation.CronTaskRegisterPostProcessor}
 * @author zpf
 * @since 1.1.0
 */
@Deprecated
public class CronTaskRegister implements EnvironmentPostProcessor, EnvironmentAware, ImportSelector,
        SpringApplicationRunListener, Ordered {

    /**
     * The empty structure here is mainly used for configuration purposes.
     */
    public CronTaskRegister() {
    }

    /**
     * The necessary constructor for using {@link SpringApplicationRunListener}.
     *
     * @param application Spring's application startup class.
     * @param args        The startup parameters for the application startup class of Spring.
     */
    public CronTaskRegister(SpringApplication application, String[] args) {
    }

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        CronRegister.Actuator.setDefaultScannerPath(application.getMainApplicationClass());
    }

    @Override
    public void setEnvironment(@NotNull Environment environment) {
        CronRegister.Actuator.setProfiles(environment.getActiveProfiles());
    }

    @Override
    @NotNull
    public String[] selectImports(@NotNull AnnotationMetadata metadata) {
        AnnotationAttributes attributes =
                AnnotationAttributes.fromMap(metadata.getAnnotationAttributes(EnableCronTaskRegister.class.getName()));
        Objects.requireNonNull(attributes, EnableCronTaskRegister.class.getName() + " analysis failed.");
        CronRegister.Actuator.enableActuator();
        CronRegister.Actuator.setScanPackages(attributes.getStringArray("basePackages"));
        CronRegister.Actuator.setNoMethodDefaultStart(attributes.getBoolean("noMethodDefaultStart"));
        return new String[0];
    }

    @Override
    public void started(ConfigurableApplicationContext context) {
        CronRegister.Actuator.start(context);
    }

    @Override
    public void running(ConfigurableApplicationContext context) {
        CronRegister.Actuator.running(context);
    }

    @Override
    public int getOrder() {
        return Integer.MIN_VALUE + 10;
    }
}
