package top.osjf.assembly.sdk.http;

/**
 * Enums of http types with apache or ok.
 *
 * @author zpf
 * @since 1.1.1
 */
public enum Type implements HttpMethod.InstanceCapable {

    APACHE_HTTP {
        @Override
        public HttpMethod getInstance() {
            return ApacheHttpMethod.INSTANCE;
        }
    }, OK_HTTP {
        @Override
        public HttpMethod getInstance() {
            return OkHttpMethod.INSTANCE;
        }
    }
}
