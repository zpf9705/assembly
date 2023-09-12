package top.osjf.assembly.cron;

import copy.cn.hutool.v_5819.core.collection.CollectionUtil;
import copy.cn.hutool.v_5819.core.exceptions.UtilException;
import copy.cn.hutool.v_5819.core.util.ArrayUtil;
import copy.cn.hutool.v_5819.core.util.ReflectUtil;
import copy.cn.hutool.v_5819.cron.CronException;
import copy.cn.hutool.v_5819.cron.CronUtil;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.support.CronExpression;
import top.osjf.assembly.cron.annotation.Cron;
import top.osjf.assembly.utils.ScanUtils;

import java.lang.reflect.Method;
import java.util.*;

/**
 * Timed task registrar, which relies on domestic Hutu {@code cn.hutool.Hutool} toolkit for implementation.
 * <p>
 * {@link copy.cn.hutool.v_5819} copy from {@code cn.hutool.Hutool} with version 5.8.19. Hereby, thank you.
 *
 * @author zpf
 * @since 1.1.0
 */
public final class CronRegister {

    static final String second_match_key = "cron.match.second=";

    static final String thread_daemon_key = "cron.thread.daemon=";

    static List<Method> cronTasks = new ArrayList<>();

    private CronRegister() {
    }

    public static void setPrepareCronList(List<Method> filterMethods) {
        if (CollectionUtil.isEmpty(filterMethods)) {
            return;
        }
        cronTasks.addAll(filterMethods);
    }

    public static void registerPrepareCronList(ApplicationContext context) {
        if (CollectionUtil.isEmpty(cronTasks)) {
            return;
        }
        Objects.requireNonNull(context, "ApplicationContext not be null");
        Map<Class<?>, Object> map = new LinkedHashMap<>();
        for (Method method : cronTasks) {
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
                                "not have permission to use. Please check the permission modifier so that we can use this class");
                    }
                }
                map.putIfAbsent(declaringClass, target);
            }
            Cron cron = method.getAnnotation(Cron.class);
            Object finalTarget = target;
            register(cron.express(), () -> ReflectUtil.invoke(finalTarget, method));
        }
        cronTasks = null;
    }

    public static void register(String express, Runnable runnable) {
        if (express == null || runnable == null) {
            return;
        }
        if (!CronExpression.isValidExpression(express)) {
            throw new UtilException("Provider " + express + "no a valid cron express");
        }
        //Register scheduled tasks
        CronUtil.schedule(express, runnable);
    }

    public static void addListenerWithPackages(String[] scanPackage) {
        if (ArrayUtil.isEmpty(scanPackage)) {
            return;
        }
        Set<Class<CronListener>> classes = ScanUtils.getSubTypesOf(CronListener.class, scanPackage);
        if (CollectionUtil.isEmpty(classes)) {
            return;
        }
        classes.forEach(c -> addListener(ReflectUtil.newInstance(c)));
    }

    public static void addListener(CronListener... cronListeners) {
        if (ArrayUtil.isEmpty(cronListeners)) {
            return;
        }
        for (CronListener cronListener : cronListeners) {
            //Register scheduled tasks Listener
            CronUtil.getScheduler().addListener(cronListener);
        }
    }

    public static void removeCornTask(String taskId){
        CronUtil.remove(taskId);
    }

    public static List<Method> getScanMethodsWithCron(String... scanPackage) {
        if (ArrayUtil.isEmpty(scanPackage)) {
            return Collections.emptyList();
        }
        return new ArrayList<>(ScanUtils.getMethodsAnnotatedWith(Cron.class, scanPackage));
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
        if (ArrayUtil.isEmpty(args)) {
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

    public static void start(boolean isMatchSecond, boolean isDaemon) {
        //Second matching support
        CronUtil.setMatchSecond(isMatchSecond);
        //Configurable to set up daemon threads
        CronUtil.start(isDaemon);
        //register info log
        CronTaskRegisterImport.getLogger().info("Cron register success : success num : {}",
                CronUtil.getScheduler().size());
    }

    public static void defaultStart() {
        //Second matching support
        CronUtil.setMatchSecond(true);
        //Configurable to set up daemon threads
        CronUtil.start();
        CronTaskRegisterImport.getLogger().info("Default to start cron");
    }
}
