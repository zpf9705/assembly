package top.osjf.assembly.util.system;

import top.osjf.assembly.util.lang.ArrayUtils;
import top.osjf.assembly.util.lang.StringUtils;

import java.time.LocalDateTime;


/**
 * The system defaults to console output.
 *
 * @author zpf
 * @since 1.0.0
 */
public final class DefaultConsole {

    private DefaultConsole() {
    }

    private static String formatJ = "%s  [ t.o.u.DefaultConsole ] - %s - %s : ";

    public static final String project_name_sign = "default.console.name";

    static {
        String property = SystemUtils.getProperty(project_name_sign);
        if (StringUtils.isNotBlank(property)) {
            formatJ = formatJ.replace("t.o.u.DefaultConsole", property);
        }
    }

    /**
     * Print information to the console.
     *
     * @param statement Formatting statements.
     * @param objs      Format parameters.
     */
    public static void info(String statement, Object... objs) {
        inputLogger(statement, "INFO", false, objs);
    }

    /**
     * Print information to the console.
     *
     * @param statement Formatting statements.
     * @param e         Exceptions.
     * @param objs      Format parameters.
     */
    public static void info(String statement, Throwable e, Object... objs) {
        inputLoggerException(statement, "INFO", e, objs);
    }

    /**
     * Print error information to the console.
     *
     * @param statement Formatting statements.
     * @param objs      Format parameters.
     */
    public static void error(String statement, Object... objs) {
        inputLogger(statement, "ERROR", false, objs);
    }

    /**
     * Print error information to the console.
     *
     * @param statement Formatting statements.
     * @param e         Exceptions.
     * @param objs      Format parameters.
     */
    public static void error(String statement, Throwable e, Object... objs) {
        inputLoggerException(statement, "ERROR", e, objs);
    }

    /**
     * Print warn information to the console.
     *
     * @param statement Formatting statements.
     * @param objs      Format parameters.
     */
    public static void warn(String statement, Object... objs) {
        inputLogger(statement, "WARN", false, objs);
    }

    /**
     * Print warn information to the console.
     *
     * @param statement Formatting statements.
     * @param e         Exceptions.
     * @param objs      Format parameters.
     */
    public static void warn(String statement, Throwable e, Object... objs) {
        inputLoggerException(statement, "WARN", e, objs);
    }

    /**
     * Print debug information to the console.
     *
     * @param statement Formatting statements.
     * @param objs      Format parameters.
     */
    public static void debug(String statement, Object... objs) {
        inputLogger(statement, "DEBUG", false, objs);
    }

    /**
     * Print debug information to the console.
     *
     * @param statement Formatting statements.
     * @param e         Exceptions.
     * @param objs      Format parameters.
     */
    public static void debug(String statement, Throwable e, Object... objs) {
        inputLoggerException(statement, "DEBUG", e, objs);
    }

    /**
     * Print trace information to the console.
     *
     * @param statement Formatting statements.
     * @param objs      Format parameters.
     */
    public static void trace(String statement, Object... objs) {
        inputLogger(statement, "TRACE", false, objs);
    }

    /**
     * Print trace information to the console.
     *
     * @param statement Formatting statements.
     * @param e         Exceptions.
     * @param objs      Format parameters.
     */
    public static void trace(String statement, Throwable e, Object... objs) {
        inputLoggerException(statement, "TRACE", e, objs);
    }

    /**
     * Loop output multiple parameters to the console.
     *
     * @param objects Format parameters.
     */
    public static void outArgs(Object... objects) {
        if (ArrayUtils.isEmpty(objects)) {
            return;
        }
        for (Object arg : objects) {
            System.out.println(arg);
        }
    }

    /**
     * Print the logger.
     *
     * @param statement   Formatting statements.
     * @param consoleType Output type.
     * @param objs        Format parameters.
     */
    private static void inputLogger(String statement, String consoleType, boolean error, Object... objs) {
        if (StringUtils.isBlank(statement)) {
            return;
        }
        String first = firstStatement(consoleType);
        if (!statement.contains("{}")) {
            outPut(error, first + statement);
        } else if (ArrayUtils.isNotEmpty(objs)) {
            statement = statement.replace("{}", "%s");
            outPut(error, first + (statement) + "%n", objs);
        }
    }

    /**
     * Print abnormal loggers.
     *
     * @param statement   Formatting statements.
     * @param consoleType Output type.
     * @param e           Exceptions.
     * @param objs        Output formatted objects.
     */
    private static void inputLoggerException(String statement, String consoleType,
                                             Throwable e, Object... objs) {
        inputLogger(statement, consoleType, true, objs);
        printStackTrace(e);
    }

    /**
     * Output type log.
     *
     * @param error   Is there an abnormal log.
     * @param formatS Formatting statements.
     * @param objs    Output formatted objects.
     */
    private static void outPut(boolean error, String formatS, Object... objs) {
        if (error) {
            if (ArrayUtils.isNotEmpty(objs)) {
                System.err.printf(formatS, objs);
            } else {
                System.err.println(formatS);
            }
        } else {
            if (ArrayUtils.isNotEmpty(objs)) {
                System.out.printf(formatS, objs);
            } else {
                System.out.println(formatS);
            }
        }
    }

    /**
     * Output exception stack information.
     *
     * @param e Exceptions.
     */
    private static void printStackTrace(Throwable e) {
        if (e == null) {
            return;
        }
        e.printStackTrace(System.err);
    }

    /**
     * Obtain the prefix logger statement.
     *
     * @return first logger
     */
    private static String firstStatement(String consoleType) {
        return String.format(formatJ,
                LocalDateTime.now(),
                consoleType,
                DefaultConsole.class.getName());
    }
}
