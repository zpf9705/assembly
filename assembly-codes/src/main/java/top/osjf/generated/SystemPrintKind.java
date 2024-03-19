package top.osjf.generated;

import java.io.PrintStream;

/**
 * The enumeration definition of the system stream output type.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.1.0
 */
public enum SystemPrintKind {

    OUT(System.out), ERROR(System.err);

    final PrintStream stream;

    SystemPrintKind(PrintStream stream) {
        this.stream = stream;
    }

    public void log(String message, Object... args) {
        stream.println(Logger.loggerFormat(message, args));
    }
}
