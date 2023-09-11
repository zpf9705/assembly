package top.osjf.assembly.sdk.client;

/**
 * The type of information enumeration from the request type.
 * <p>Currently, please review {@link HttpClient} or {@link RpcClient}</p>
 *
 * @author zpf
 * @since 1.1.0
 */
public enum ClientType {

    HTTP(HttpClient.class), RPC(null);

    final Class<?> clazz;

    ClientType(Class<?> clazz) {
        this.clazz = clazz;
    }

    public Class<?> getClazz() {
        return clazz;
    }
}
