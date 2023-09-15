package top.osjf.assembly.sdk.rpc;

import org.springframework.lang.NonNull;
import top.osjf.assembly.sdk.client.AbstractClient;
import top.osjf.assembly.sdk.process.Request;
import top.osjf.assembly.sdk.process.Response;

import java.util.function.BiConsumer;

/**
 * RPC based request client and now no support , if necessary, support will be added in the future.
 *
 * @author zpf
 * @since 1.1.0
 */
public class DefaultRpcClient<T extends Response> extends AbstractClient<T> {

    private static final long serialVersionUID = 8405526950849385906L;

    public DefaultRpcClient(String url) {
        super(url);
    }

    @Override
    public T request() {
        throw new UnsupportedOperationException("There is as yet no via RPC framework to call API, " +
                "if there is a will give support here");
    }

    @Override
    public BiConsumer<String, Object[]> normal() {
        return null;
    }

    @Override
    public BiConsumer<String, Object[]> sdkError() {
        return null;
    }

    @Override
    public BiConsumer<String, Object[]> unKnowError() {
        return null;
    }

    @Override
    @NonNull
    public String preResponseStrHandler(Request<T> request, String responseStr) {
        return "";
    }

    @Override
    @NonNull
    @SuppressWarnings("unchecked")
    public T convertToResponse(Request<T> request, String responseStr) {
        return (T) new DefaultRpcResponse();
    }

    public static class DefaultRpcResponse implements Response {

        @Override
        public boolean isSuccess() {
            return false;
        }

        @Override
        public String getMessage() {
            return null;
        }
    }
}
