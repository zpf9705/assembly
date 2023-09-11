package top.osjf.assembly.cron;

import copy.cn.hutool.v_5819.core.collection.CollectionUtil;
import copy.cn.hutool.v_5819.core.exceptions.UtilException;
import copy.cn.hutool.v_5819.core.util.ArrayUtil;
import copy.cn.hutool.v_5819.core.util.ReflectUtil;
import copy.cn.hutool.v_5819.cron.CronException;
import copy.cn.hutool.v_5819.cron.CronUtil;
import copy.cn.hutool.v_5819.log.StaticLog;
import org.springframework.scheduling.support.CronExpression;
import top.osjf.assembly.cron.annotation.Cron;
import top.osjf.assembly.utils.ScanUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Timed task registrar, which relies on domestic Hutu {@code cn.hutool.Hutool} toolkit for implementation.
 * <p>
 * Thank you very much.
 *
 * @author zpf
 * @since 3.1.5
 */
public final class CronRegister {

    static final String second_match_key = "cron.match.second=";

    static final String thread_daemon_key = "cron.thread.daemon=";

    private CronRegister() {
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

    public static void register(Object targetObj, Method method) {
        if (targetObj == null || method == null) {
            return;
        }
        Cron cron = method.getAnnotation(Cron.class);
        if (!CronExpression.isValidExpression(cron.express())) {
            throw new UtilException("Provider " + cron.express() + "no a valid cron express");
        }
        //Register scheduled tasks
        CronUtil.schedule(cron.express(), (Runnable) () -> ReflectUtil.invoke(targetObj, method,
                (Object) cron.args()));
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
        StaticLog.info("Cron register success : success num : {}", CronUtil.getScheduler().size());
    }

    public static void defaultStart() {
        //Second matching support
        CronUtil.setMatchSecond(true);
        //Configurable to set up daemon threads
        CronUtil.start();
    }
}
