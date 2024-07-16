package top.osjf.sdk.core.client;


import top.osjf.sdk.core.exception.SdkException;

import java.util.function.BiConsumer;

/**
 * Log consumers can easily specify log output for each {@link Client}
 * using the function {@link BiConsumer}.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.1.0
 */
public interface LoggerConsumer {

    /**
     * Regular log entries, user normal log output.
     *
     * @return For example: {@code  org.slf4j.Logger#info(String, Object...)}
     */
    BiConsumer<String, Object[]> normal();

    /**
     * {@link SdkException} Exclusive log item, log output for user SDK exceptions.
     *
     * @return For example: {@code org.slf4j.Logger#error(String, Object...)}
     */
    BiConsumer<String, Object[]> sdkError();

    /**
     * Abnormal log output except for SDK.
     *
     * @return For example: {@code org.slf4j.Logger#error(String, Object...)}
     */
    BiConsumer<String, Object[]> unKnowError();
}
