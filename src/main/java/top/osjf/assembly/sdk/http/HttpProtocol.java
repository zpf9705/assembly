package top.osjf.assembly.sdk.http;

/**
 * API Request Address HTTP Protocol Header Enumeration Selection.
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
