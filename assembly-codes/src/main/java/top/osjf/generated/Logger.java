package top.osjf.generated;

import top.osjf.assembly.util.lang.ArrayUtils;

import javax.tools.Diagnostic;

/**
 * Provide special log output for annotation processors and method
 * classes for debugging system log printing.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.1.0
 */
public interface Logger {

    String LOGGER_SEAT = "{}";

    void log(SystemPrintKind kind, String message, Object... args);

    void log(Diagnostic.Kind kind, String message, Object... args);

    static String loggerFormat(String logger, Object... args) {
        if (ArrayUtils.isEmpty(args)) {
            return logger;
        }
        return String.format(logger.replace(LOGGER_SEAT, "%s"), args);
    }
}
