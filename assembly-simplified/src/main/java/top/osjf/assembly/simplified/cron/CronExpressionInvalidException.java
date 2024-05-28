package top.osjf.assembly.simplified.cron;

import cn.hutool.cron.CronException;

/**
 * When the execution expression of the registered scheduled task
 * is invalid, an exception prompt is given.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 2024.05.28
 */
public class CronExpressionInvalidException extends CronException {

    private static final long serialVersionUID = -5171659277961608828L;

    private final String expression;

    public CronExpressionInvalidException(String expression) {
        super("The provided cron expression " + expression + " is not a valid value.");
        this.expression = expression;
    }

    /**
     * @return Returns an invalid cron expression.
     */
    public String getExpression() {
        return expression;
    }
}
