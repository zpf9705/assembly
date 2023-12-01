package top.osjf.assembly.simplified.sdk.rpc;

import top.osjf.assembly.simplified.sdk.client.AbstractClient;
import top.osjf.assembly.simplified.sdk.process.Request;
import top.osjf.assembly.simplified.sdk.process.Response;
import top.osjf.assembly.util.annotation.NotNull;

import java.util.function.BiConsumer;

/**
 * RPC based request client and now no support , if necessary, support will be added in the future.
 * @param <T> Implement a unified response class data type.
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
    @NotNull
    public String preResponseStrHandler(Request<T> request, String responseStr) {
        return "";
    }

    @Override
    @NotNull
    @SuppressWarnings("unchecked")
    public T convertToResponse(Request<T> request, String responseStr) {
        return (T) new DefaultRpcResponse();
    }

    public static class DefaultRpcResponse implements Response {

        private static final long serialVersionUID = 5606294990868314290L;

        @Override
        public boolean isSuccess() {
            return false;
        }

        @Override
        public String getMessage() {
            return null;
        }

        @Override
        public void setErrorCode(Object code) {
            
        }

        @Override
        public void setErrorMessage(String message) {

        }
    }
}
