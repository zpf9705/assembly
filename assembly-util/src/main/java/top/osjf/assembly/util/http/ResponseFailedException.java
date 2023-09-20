package top.osjf.assembly.util.http;

import java.io.IOException;


/**
 * {@link okhttp3.OkHttpClient} for failed to throw ex to notify anything.
 *
 * @author zpf
 * @since 2.0.0
 */
public class ResponseFailedException extends IOException {

    private static final long serialVersionUID = 2367808447118437245L;

    public ResponseFailedException(String message) {
        super(message);
    }
}
