package top.osjf.assembly.simplified.sdk.http;

/**
 * API Request Address HTTP Protocol Header Enumeration Selection.
 *
 * @author zpf
 * @since 1.1.1
 */
public enum HttpProtocol {

    HTTPS("https:"),

    HTTP("http:");

    private final String path;

    HttpProtocol(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
