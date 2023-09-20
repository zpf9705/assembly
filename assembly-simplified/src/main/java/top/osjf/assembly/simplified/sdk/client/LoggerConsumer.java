package top.osjf.assembly.simplified.sdk.client;

import java.util.function.BiConsumer;

/**
 * Log consumers can easily specify log output for each {@link Client} using the function {@link BiConsumer}.
 *
 * @author zpf
 * @since 1.1.0
 */
public interface LoggerConsumer {

    /**
     * Regular log entries, user normal log output.
     *
     * @return For example: {@link org.slf4j.Logger#info(String, Object...)}
     */
    BiConsumer<String, Object[]> normal();

    /**
     * {@link top.osjf.assembly.simplified.sdk.SdkException} Exclusive log item, log output for user SDK exceptions.
     *
     * @return For example: {@link org.slf4j.Logger#error(String, Object...)}
     */
    BiConsumer<String, Object[]> sdkError();

    /**
     * Abnormal log output except for SDK.
     *
     * @return For example: {@link org.slf4j.Logger#error(String, Object...)}
     */
    BiConsumer<String, Object[]> unKnowError();
}
