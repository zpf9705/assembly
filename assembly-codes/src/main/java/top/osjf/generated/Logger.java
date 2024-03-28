package top.osjf.generated;

import top.osjf.assembly.util.lang.ArrayUtils;

import javax.tools.Diagnostic;

/**
 * Provide special log output for annotation processors and method
 * classes for debugging system log printing.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.1.0
 */
public interface Logger {

    /** Placeholder for formatting log messages.*/
    String LOGGER_SEAT = "{}";

    /**
     * Output console logs at the {@link System} level.
     * @param kind    {@link System} level output type.
     * @param message The log message to be formatted.
     * @param args    The parameters to be formatted.
     * @see System#out
     * @see System#in
     */
    void log(SystemPrintKind kind, String message, Object... args);

    /**
     * Log message manager {@link javax.annotation.processing.Messager} level
     * log output during compilation.
     * @see javax.tools.Diagnostic.Kind
     * @param kind    {@link Diagnostic.Kind} level output type.
     * @param message The log message to be formatted.
     * @param args    The parameters to be formatted.
     */
    void log(Diagnostic.Kind kind, String message, Object... args);

    /**
     * The log information formatting method formats the placeholders
     * {@link #LOGGER_SEAT} in order as input parameters, using
     * {@link String#format(String, Object...)}.
     * @param message The log message to be formatted.
     * @param args    The parameters to be formatted.
     * @return The formatted log statement to be output.
     */
    static String loggerFormat(String message, Object... args) {
        if (ArrayUtils.isEmpty(args)) {
            return message;
        }
        return String.format(message.replace(LOGGER_SEAT, "%s"), args);
    }
}
