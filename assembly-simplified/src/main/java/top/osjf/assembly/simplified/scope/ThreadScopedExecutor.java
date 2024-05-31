package top.osjf.assembly.simplified.scope;

import java.util.concurrent.ExecutorService;

/**
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 2024.05.31
 */
public interface ThreadScopedExecutor extends ExecutorService {

    String getThreadNamePrefix();

    void setAfterExecute(Runnable clear);
}
