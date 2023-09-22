package top.osjf.assembly.simplified.cron;

import cn.hutool.cron.CronException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ConfigurableBootstrapContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.lang.NonNull;
import top.osjf.assembly.simplified.cron.annotation.Cron;
import top.osjf.assembly.simplified.cron.annotation.EnableCronTaskRegister;
import top.osjf.assembly.simplified.support.SourceEnvironmentPostProcessor;
import top.osjf.assembly.util.DefaultConsole;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
 * @author zpf
 * @since 1.1.0
 */
public class CronTaskRegisterImport implements ImportSelector, EnvironmentAware, SpringApplicationRunListener, Ordered {

    private SpringApplication application;

    private String[] args;

    /**
     * The environment in which the current springboot application is launched.
     */
    private final List<String> profiles = new ArrayList<>();

    /**
     * This automatic configuration does not return the beans that need to be assembled.
     */
    private final String[] empty = new String[0];

    /**
     * The empty structure here is mainly used for configuration purposes.
     */
    public CronTaskRegisterImport() {
    }

    /**
     * The necessary constructor for using a listener.
     *
     * @param application Spring's application startup class.
     * @param args        The startup parameters for the application startup class of Spring.
     */
    public CronTaskRegisterImport(SpringApplication application, String[] args) {
        this.application = application;
        this.args = args;
    }

    public SpringApplication getApplication() {
        return application;
    }

    public String[] getArgs() {
        return args;
    }

    @Override
    public void setEnvironment(@NonNull Environment environment) {
        profiles.addAll(Arrays.asList(environment.getActiveProfiles()));
    }

    @Override
    @NonNull
    public String[] selectImports(@NonNull AnnotationMetadata metadata) {
        //get Attributes for EnableCronTaskRegister
        AnnotationAttributes attributes =
                AnnotationAttributes.fromMap(metadata.getAnnotationAttributes(EnableCronTaskRegister.class.getName()));
        if (attributes == null) {
            throw new CronException("Analysis named" + EnableCronTaskRegister.class.getName() + "annotation " +
                    "to AnnotationAttributes failed");
        }
        String[] scanPackage = attributes.getStringArray("basePackages");
        if (ArrayUtils.isEmpty(scanPackage)) {
            scanPackage = SourceEnvironmentPostProcessor.findSpringbootPrimarySourcesPackages();
        }
        boolean noMethodDefaultStart = attributes.getBoolean("noMethodDefaultStart");
        //Scanning timing method
        List<Method> methods = CronRegister.getScanMethodsWithCron(scanPackage);
        if (CollectionUtils.isEmpty(methods)) {
            if (noMethodDefaultStart) {
                CronRegister.defaultStart();
            } else {
                DefaultConsole.info("The provided path {} did not find a method for @Cron.",
                        Arrays.toString(scanPackage));
            }
            return empty;
        }
        //Filter according to the inclusion of the environment
        List<Method> filterMethods = methods.stream().filter(
                m -> {
                    Cron cron = m.getAnnotation(Cron.class);
                    if (ArrayUtils.isNotEmpty(cron.profiles())) {
                        return Arrays.stream(cron.profiles()).anyMatch(profiles::contains);
                    }
                    //no profile args direct register
                    return true;
                }
        ).collect(Collectors.toList());
        //If no methods that can be registered are found, no exceptions will be thrown but a log prompt will be given
        if (CollectionUtils.isEmpty(filterMethods)) {
            if (noMethodDefaultStart) {
                CronRegister.defaultStart();
            } else {
                DefaultConsole.info("There is no standardized method for registering scheduled tasks");
            }
            return empty;
        }
        //Only when there is a timing method will the listener be automatically registered.
        CronRegister.setPrepareCronList(filterMethods);
        //Add listeners under this path
        CronRegister.addListenerWithPackages(scanPackage);
        return empty;
    }

    @Override
    public void started(ConfigurableApplicationContext context) {
        CronRegister.registerPrepareCronList(context);
    }

    @Override
    public void running(ConfigurableApplicationContext context) {
        CronRegister.start(context.getBean(ApplicationArguments.class).getSourceArgs());
    }

    @Override
    public int getOrder() {
        return Integer.MIN_VALUE + 10;
    }
}
