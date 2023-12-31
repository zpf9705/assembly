package top.osjf.assembly.simplified.cron;

import cn.hutool.cron.CronException;
import cn.hutool.cron.CronUtil;
import org.springframework.boot.ApplicationArguments;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.support.CronExpression;
import top.osjf.assembly.simplified.cron.annotation.Cron;
import top.osjf.assembly.util.annotation.NotNull;
import top.osjf.assembly.util.io.ScanUtils;
import top.osjf.assembly.util.lang.ArrayUtils;
import top.osjf.assembly.util.lang.CollectionUtils;
import top.osjf.assembly.util.lang.ReflectUtils;
import top.osjf.assembly.util.system.DefaultConsole;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Timed task registrar, which relies on domestic Hutu {@code cn.hutool.Hutool} toolkit for implementation.
 *
 * @author zpf
 * @since 1.1.0
 */
public final class CronRegister {

    static final String second_match_key = "cron.match.second=";

    static final String thread_daemon_key = "cron.thread.daemon=";

    private CronRegister() {
    }

    //************************** Help class ******************************//

    /**
     * @since 2.0.3
     */
    protected static class Actuator {

        protected static boolean enable;

        protected static List<String> _profiles;

        /**
         * Scan path.
         */
        protected static String[] _scanPackages;

        /**
         * The collection of methods that currently need to be registered as a scheduled task.
         */
        protected static List<Method> _cronMethods;

        /**
         * Is there currently a scheduled task method that needs to be registered.
         * {@code true} representing existence and {@code false} representing does not exist.
         */
        protected static boolean _noRegisterCron;

        /**
         * When <pre>{@code noRegisterCron == true} </pre> is mentioned above, whether to start the
         * scheduled thread pool can be dynamically registered for scheduled tasks in the future.
         * <p>And when dynamically registering scheduled tasks, there is no need to manually start them.</p>
         */
        protected static boolean _noMethodDefaultStart;

        public static void enableActuator() {
            enable = true;
        }

        public static void setDefaultScannerPath(Class<?> clazz) {
            if (clazz == null) {
                return;
            }
            if (_scanPackages == null) _scanPackages = new String[]{clazz.getPackage().getName()};
        }

        protected static void setProfiles(String[] profiles) {
            _profiles = Arrays.asList(profiles);
        }

        protected static void setScanPackages(String[] scanPackages) {
            if (scanPackages != null) _scanPackages = scanPackages;
            scanAndFilterCronWithProfiles();
        }

        protected static void setNoMethodDefaultStart(boolean noMethodDefaultStart) {
            _noMethodDefaultStart = noMethodDefaultStart;
        }

        private static void scanAndFilterCronWithProfiles() {
            //scanner with path
            List<Method> methods = CronRegister.getScanMethodsWithCron(_scanPackages);
            //Filter methods based on the current spring startup environment and
            // the environment provided by the annotations.
            if (CollectionUtils.isNotEmpty(_profiles)) {
                _cronMethods = methods.stream().filter(
                        m -> {
                            Cron cron = m.getAnnotation(Cron.class);
                            if (ArrayUtils.isNotEmpty(cron.profiles())) {
                                return Arrays.stream(cron.profiles()).anyMatch(_profiles::contains);
                            }
                            //no profile args direct register
                            return true;
                        }
                ).collect(Collectors.toList());
            }
            _noRegisterCron = CollectionUtils.isEmpty(_cronMethods);
        }

        protected static void start(@NotNull ApplicationContext context) {
            if (!enable){
                return;
            }
            //The first step is to add a scheduled task.
            CronRegister.registerPrepareCronWithSpringContextOrNew(context, _cronMethods);
            //The second step is to add listeners for scheduled task execution and exceptions.
            //Scan Package Limitation Sentence: The package where the scheduled task is located.
            // If you do not want to use this package, you can manually add listeners through
            // the {top.osjf.assembly.simplified.cron.CronRegister#addListener(CronListener...)} method
            CronRegister.addListenerWithPackages(_scanPackages);
        }

        protected static void running(@NotNull ApplicationContext context) {
            if (!enable){
                clear();
                return;
            }
            //If the provided package path does not scan for the timing method that needs to be
            // registered, then the decision to continue starting is based on whether to default
            // to the startup parameters.
            if (_noRegisterCron) {
                if (_noMethodDefaultStart) {
                    //Start with the parameters of the main method.
                    CronRegister.start(context.getBean(ApplicationArguments.class).getSourceArgs());
                    DefaultConsole.info("The default start of the timing method was not " +
                            "found based on the given path.");
                } else {
                    DefaultConsole.info("The component did not help you start the scheduled task. " +
                            "If you need to start the thread pool after the program starts, please call " +
                            "{top.osjf.assembly.simplified.cron.CronRegister#start(String...)}." +
                            "The premise is that you did not manually call {@CronUtil} of the Hutu package " +
                            "for manual startup.");
                }
            } else {
                //Start with the parameters of the main method.
                //Start immediately if there is a registration method available.
                CronRegister.start(context.getBean(ApplicationArguments.class).getSourceArgs());
            }
            clear();
        }

        protected static void clear() {
            _profiles = null;
            _scanPackages = null;
            _cronMethods = null;
            _noRegisterCron = false;
            _noMethodDefaultStart = false;
        }
    }

    public static void registerPrepareCronWithSpringContextOrNew(@NotNull ApplicationContext context,
                                                                 List<Method> cronMethods) {
        if (CollectionUtils.isEmpty(cronMethods)) {
            return;
        }
        Objects.requireNonNull(context, "ApplicationContext not be null");
        Map<Class<?>, Object> map = new LinkedHashMap<>();
        for (Method method : cronMethods) {
            Class<?> declaringClass = method.getDeclaringClass();
            Object target = map.get(declaringClass);
            if (target == null) {
                //Prioritize taking execution objects from the Spring container.
                try {
                    target = context.getBean(declaringClass);
                } catch (Throwable e) {
                    //Secondly, directly use empty construction to instantiate object execution methods.
                    try {
                        target = declaringClass.newInstance();
                    } catch (InstantiationException e0) {
                        throw new CronException("Class name {" + method.getDeclaringClass().getName() + "} not " +
                                "found empty parameter construct cannot be instantiated");
                    } catch (IllegalAccessException e01) {
                        throw new CronException("Class name {" + method.getDeclaringClass().getName() + "} does " +
                                "not have permission to use. Please check the permission modifier so that we can " +
                                "use this class");
                    }
                }
                map.putIfAbsent(declaringClass, target);
            }
            Cron cron = method.getAnnotation(Cron.class);
            Object finalTarget = target;
            register(cron.express(), () -> ReflectUtils.invoke(finalTarget, method));
        }
    }

    public static void registerWthBean(Object bean, Environment environment) {
        if (bean == null
                || ArrayUtils.isEmpty(bean.getClass().getMethods())
                || Arrays.stream(bean.getClass().getMethods()).allMatch(m -> m.getAnnotation(Cron.class) == null)) {
            return;
        }
        for (Method method : bean.getClass().getMethods()) {
            Cron cron = method.getAnnotation(Cron.class);
            if (cron != null) {
                if (profilesCheck(cron.profiles(), environment.getActiveProfiles())) {
                    CronRegister.register(cron.express(), () -> ReflectUtils.invoke(bean, method));
                }
            }
        }
    }

    private static boolean profilesCheck(String[] providerProfiles, String[] startUpProfiles) {
        if (ArrayUtils.isEmpty(startUpProfiles)) {
            throw new CronException("No startup env profiles");
        }
        if (ArrayUtils.isEmpty(providerProfiles)) {
            return true;
        }
        return Arrays.stream(providerProfiles).anyMatch(p -> Arrays.asList(startUpProfiles).contains(p));
    }

    public static void register(String express, Runnable runnable) {
        if (express == null || runnable == null) {
            return;
        }
        if (!CronExpression.isValidExpression(express)) {
            throw new CronException("Provider " + express + "no a valid cron express");
        }
        //Register scheduled tasks
        CronUtil.schedule(express, runnable);
    }

    public static void addListenerWithPackages(String[] scanPackage) {
        if (ArrayUtils.isEmpty(scanPackage)) {
            return;
        }
        Set<Class<CronListener>> classes = ScanUtils.getSubTypesOf(CronListener.class, scanPackage);
        if (CollectionUtils.isEmpty(classes)) {
            return;
        }
        classes.forEach(c -> addListener(ReflectUtils.newInstance(c)));
    }

    public static void addListener(CronListener... cronListeners) {
        if (ArrayUtils.isEmpty(cronListeners)) {
            return;
        }
        for (CronListener cronListener : cronListeners) {
            //Register scheduled tasks Listener
            CronUtil.getScheduler().addListener(cronListener);
        }
    }

    public static void removeCornTask(String taskId) {
        CronUtil.remove(taskId);
    }

    public static List<Method> getScanMethodsWithCron(String... scanPackage) {
        if (ArrayUtils.isEmpty(scanPackage)) {
            return Collections.emptyList();
        }
        return new ArrayList<>(ScanUtils.getMethodsAnnotatedWith(Cron.class, scanPackage));
    }

    public static boolean registerZero() {
        return CronUtil.getScheduler().isEmpty();
    }

    /**
     * Notify the system of the startup parameters passed in by the main method to
     * determine the second configuration {@link #second_match_key} for scheduled task startup
     * <pre>
     *     {@code --cron.match.second=true}
     * </pre>
     * <p>
     * and
     * <p>
     * the configuration of the daemon thread {@link #thread_daemon_key}
     * <pre>
     *     {@code --cron.thread.daemon=false}
     * </pre>
     * Default to support second matching and non daemon threads
     *
     * @param args Starting command passing in parameters
     */
    public static void start(String... args) {
        if (CronUtil.getScheduler().isStarted()) {
            throw new CronException("The start switch of cn.hutool.cron.CronUtil has been turned on before," +
                    " please check");
        }
        if (ArrayUtils.isEmpty(args)) {
            //empty args direct to matchSecond and thread daemon
            start(true, false);
        } else {
            int config = 0;
            String secondMatch = "true";
            String daemon = "false";
            for (String arg : args) {
                if (config == 2) {
                    break;
                }
                if (arg.contains(second_match_key)) {
                    secondMatch = arg.split(second_match_key)[1];
                    config++;
                } else if (arg.contains(thread_daemon_key)) {
                    daemon = arg.split(thread_daemon_key)[1];
                    config++;
                }
            }
            start(Boolean.parseBoolean(secondMatch), Boolean.parseBoolean(daemon));
        }
    }

    private static void start(boolean isMatchSecond, boolean isDaemon) {
        //Second matching support
        CronUtil.setMatchSecond(isMatchSecond);
        //Configurable to set up daemon threads
        CronUtil.start(isDaemon);
        //register info log
        DefaultConsole.info("Cron register success : success task num : {}", CronUtil.getScheduler().size());
    }
}
