package top.osjf.assembly.simplified.cron;

import cn.hutool.cron.CronUtil;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.scheduling.support.CronExpression;
import top.osjf.assembly.simplified.cron.annotation.Cron;
import top.osjf.assembly.simplified.cron.annotation.CronAnnotationAttributes;
import top.osjf.assembly.util.annotation.CanNull;
import top.osjf.assembly.util.lang.ArrayUtils;
import top.osjf.assembly.util.lang.CollectionUtils;
import top.osjf.assembly.util.lang.ReflectUtils;
import top.osjf.assembly.util.lang.StringUtils;
import top.osjf.assembly.util.system.DefaultConsole;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * The static method manager for scheduled tasks, derived from abandoned
 * {@code top.osjf.assembly.simplified.cron.CronRegister}, deleted outdated
 * related methods, and subsequent extensions related to scheduled tasks
 * will be recorded in this category.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 2.2.5
 */
public final class CronTaskManager {

    /*** Temporary cache pre added listener cache, used to prevent duplicate additions, cleared after startup.*/
    private static final List<CronListener> ANTI_DUPLICATE_CACHE = new CopyOnWriteArrayList<>();

    /**
     * Support timed second level for startup parameters.
     */
    public static final String second_match_key = "assembly.simplified.cron.match.second=";

    /**
     * Support thread daemon for startup parameters.
     */
    public static final String thread_daemon_key = "assembly.simplified.cron.thread.daemon=";

    private CronTaskManager() {
    }

    /**
     * By obtaining container beans, check if their built-in accessible methods contain
     * {@link Cron} annotations, in order to register and run them on a scheduled basis.
     *
     * <p>Before 2.1.7, there was no attention paid to the situation of dynamic proxies,
     * and support for dynamic proxy objects was added in version 2.1.8.
     *
     * @param bean           Container beans.
     * @param activeProfiles Spring environment.
     * @see AopUtils#isAopProxy(Object)
     */
    public static void registerCronTask(Object bean, @CanNull String[] activeProfiles) {
        Objects.requireNonNull(bean, "The registrant cannot be null.");

        //@Since 2.1.8
        //If it is a dynamic proxy object, first obtain the class object of
        // its target object, and obtain it directly without the proxy.
        Class<?> target;
        if (AopUtils.isAopProxy(bean)) {
            target = AopProxyUtils.ultimateTargetClass(bean);
        } else {
            target = bean.getClass();
        }
        Method[] methods = target.getDeclaredMethods();
        //Determine if there is a timing method.
        Arrays.stream(methods).filter(method -> {
            //Must meet the requirements of publicly available county-wide,
            // non-static methods, and include specified annotations.
            return method.isAnnotationPresent(Cron.class)
                    && !Modifier.isStatic(method.getModifiers())
                    && Modifier.isPublic(method.getModifiers());
        }).forEach(method -> {
            CronAnnotationAttributes cronAttribute = CronAnnotationAttributes.of(method);
            String expression = cronAttribute.getExpression();
            Runnable rab = () -> ReflectUtils.invoke(bean, method);
            if (ArrayUtils.isEmpty(activeProfiles)) {
                //When the environment is not activated, it indicates that
                // everything is applicable and can be registered directly.
                registerCronTask(expression, rab);
            } else {
                if (profilesCheck(cronAttribute.getProfiles(), activeProfiles)) {
                    registerCronTask(expression, rab);
                }
            }
        });
    }

    /**
     * Register a valid cron expression and runtime into the scheduled task pool.
     *
     * @param expression cron expressions.
     * @param runnable   Register the runtime.
     */
    public static void registerCronTask(String expression, Runnable runnable) {
        if (StringUtils.isBlank(expression) || runnable == null) {
            return;
        }
        if (!CronExpression.isValidExpression(expression)) {
            throw new CronExpressionInvalidException(expression);
        }
        //Register scheduled tasks
        CronUtil.schedule(expression, runnable);
    }

    /**
     * Check if the required operating environment is within the list of activated environments.
     *
     * @param providerProfiles The set of execution environment names that the
     *                         registration task aims to satisfy.
     * @param activeProfiles   The current set of activated environments.
     * @return if {@code true} allow registration ,otherwise no allowed.
     */
    public static boolean profilesCheck(String[] providerProfiles, String[] activeProfiles) {
        if (ArrayUtils.isEmpty(activeProfiles)) {
            //When the environment is not activated, it indicates that
            // everything is applicable and can be registered directly.
            return true;
        }
        if (ArrayUtils.isEmpty(providerProfiles)) {
            //When no running environment is provided, register directly.
            return true;
        }
        //Adaptation provides the presence of the required environment in the activated environment.
        return Arrays.stream(providerProfiles).anyMatch(p -> Arrays.asList(activeProfiles).contains(p));
    }

    /**
     * Add listeners for the start sequence of scheduled tasks.
     *
     * @param cronListeners listeners for the start sequence of scheduled tasks.
     * @since 2.2.5
     */
    public static void addCronListeners(List<CronListener> cronListeners) {
        if (CollectionUtils.isEmpty(cronListeners)) {
            return;
        }
        addCronListeners(cronListeners.toArray(new CronListener[]{}));
    }

    /**
     * Add listeners for the start sequence of scheduled tasks.
     *
     * @param cronListeners listeners for the start sequence of scheduled tasks.
     */
    public static void addCronListeners(CronListener... cronListeners) {
        if (ArrayUtils.isEmpty(cronListeners)) {
            return;
        }
        for (CronListener cronListener : cronListeners) {
            if (cronListener != null && !ANTI_DUPLICATE_CACHE.contains(cronListener)) {
                //Register scheduled tasks Listener
                CronUtil.getScheduler().addListener(cronListener);
                ANTI_DUPLICATE_CACHE.add(cronListener);
            }
        }
    }

    /**
     * Pause a scheduled task by deleting it through ID.
     *
     * @param taskId Unique ID for scheduled tasks.
     */
    public static void removeCornTask(String taskId) {
        CronUtil.remove(taskId);
    }

    /**
     * Obtain whether scheduled registration is an empty item.
     *
     * @return if {@code true} register item number is zero ,otherwise more than 0.
     */
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
        if (CronUtil.getScheduler().isStarted()) throw new CronIsStartedException();
        boolean secondMatch = true;
        boolean daemon = false;
        if (ArrayUtils.isNotEmpty(args)) {
            int config = 0;
            for (String arg : args) {
                if (config == 2) {
                    break;
                }
                if (arg.contains(second_match_key)) {
                    secondMatch = Boolean.parseBoolean(arg.split(second_match_key)[1]);
                    config++;
                } else if (arg.contains(thread_daemon_key)) {
                    daemon = Boolean.parseBoolean(arg.split(thread_daemon_key)[1]);
                    config++;
                }
            }
        }
        //Second matching support
        CronUtil.setMatchSecond(secondMatch);
        //Configurable to set up daemon threads
        CronUtil.start(daemon);
        if (!ANTI_DUPLICATE_CACHE.isEmpty()) ANTI_DUPLICATE_CACHE.clear();
        DefaultConsole.info("Cron register success task num : {}", CronUtil.getScheduler().size());
    }
}
