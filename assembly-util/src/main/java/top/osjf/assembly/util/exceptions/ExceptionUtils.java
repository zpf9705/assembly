package top.osjf.assembly.util.exceptions;

import cn.hutool.core.exceptions.ExceptionUtil;

/**
 * The Exception function is implemented in {@link ExceptionUtil}.
 * @author zpf
 * @since 1.0.3
 */
public final class ExceptionUtils extends ExceptionUtil {

    private ExceptionUtils() {
    }

    /**
     * Intercept fixed length stack trace information based on the given exception.
     * @param throwable Can analyze anomalies.
     * @return Track splicing information.
     */
    public static String getStackTrace(Throwable throwable) {
        return getStackTrace(throwable, true, 255);
    }

    /**
     * Intercept fixed length stack trace information based on the given exception.
     * @param throwable Can analyze anomalies.
     * @param length    Main information truncation length.
     * @return Track splicing information.
     */
    public static String getStackTrace(Throwable throwable, int length) {
        return getStackTrace(throwable, true, length);
    }

    /**
     * Intercept fixed length stack trace information based on the given exception.
     * @param throwable Can analyze anomalies.
     * @param isTrace   Whether to concatenate tracking information.
     * @return Track splicing information.
     */
    public static String getStackTrace(Throwable throwable, Boolean isTrace) {
        return getStackTrace(throwable, isTrace, 255);
    }

    /**
     * Intercept fixed length stack trace information based on the given exception.
     * @param throwable Can analyze anomalies.
     * @param isTrace   Whether to concatenate tracking information.
     * @param length    Main information truncation length.
     * @return Track splicing information.
     */
    public static String getStackTrace(Throwable throwable, Boolean isTrace, Integer length) {
        StringBuilder trace = new StringBuilder();
        trace.append(throwable.getMessage());
        if (isTrace) {
            for (StackTraceElement el : throwable.getStackTrace()) {
                trace.append(" | ").append(el.toString());
            }
        }
        return trace.length() > length ? trace.substring(0, length) : trace.toString();
    }
}
