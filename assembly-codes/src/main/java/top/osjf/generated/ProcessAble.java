package top.osjf.generated;

/**
 * The final form of the annotation processor is the execution interface.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.1.3
 */
@FunctionalInterface
public interface ProcessAble {

    /** The execution of the final generated solution defines the method.*/
    void process();

    /**
     * The generated class must be called by the process after the
     * information is collected.
     * <p>Direct static method calls to avoid error-throwing handling
     * that generates exceptions.
     * @param able You need to deal with the generated final process.
     * @param logger Log output.
     */
    static void run(ProcessAble able, Logger logger) {
        try {
            able.process();
        } catch (Exception e) {
            logger.log(SystemPrintKind.OUT, "ProcessAble run error {}", e.getMessage());
        }
    }
}
