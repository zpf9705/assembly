package top.osjf.sdk.core.enums;

/**
 * The interface for obtaining important attributes related to SDK,
 * and the dynamic group information requested by SDK will be defined here.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
public interface SdkEnum {

    /**
     * Returns the true request address of the SDK, using HTTP as
     * an example, which is an HTTP protocol address that can be curled.
     * <p>For RPC, it is estimated to only be the server address
     * and port number.</p>
     *
     * @param host The host name of the SDK.
     * @return The request address for the SDK.
     */
    String getUlr(String host);

    /**
     * The name of the SDK request, which is a unique identifier name
     * to distinguish between successful analysis or failure in the
     * future, is not recommended to be {@literal null}.
     *
     * @return If it is an enumeration, simply rewrite {@link Enum#name()},
     * and the rest can be customized.
     */
    String name();
}
