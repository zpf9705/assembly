package top.osjf.assembly.simplified.sdk;

/**
 * The encapsulated SDK related attributes are mainly used in {@link top.osjf.assembly.sdk.process.Request} calls.
 *
 * @author zpf
 * @since 1.1.0
 */
public interface SdkEnum {

    /**
     * Obtain the true request address of the SDK, using HTTP as an example, which is
     * an HTTP protocol address that can be curled.
     * <p>For RPC, it is estimated to only be the server address and port number.</p>
     *
     * @param host The host name of the SDK.
     * @return The request address for the SDK.
     */
    String getUlr(String host);

    /**
     * The name of the SDK request, which is a unique identifier name to distinguish
     * between successful analysis or failure in the future, is not recommended to be {@literal null}.
     *
     * @return If it is an enumeration, simply rewrite {@link Enum#name()}, and the rest can be customized.
     */
    String name();
}
