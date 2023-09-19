package top.osjf.assembly.sdk.http;

import java.io.IOException;


/**
 * {@link okhttp3.OkHttpClient} for failed to throw ex to notify anything.
 *
 * @author zpf
 * @since 1.1.1
 */
public class ResponseFailedException extends IOException {

    private static final long serialVersionUID = 2367808447118437245L;

    public ResponseFailedException(String message) {
        super(message);
    }
}
