package top.osjf.assembly.sdk.http;

import org.springframework.lang.Nullable;
import top.osjf.assembly.sdk.SdkException;

import java.util.function.Supplier;

/**
 * <p>The client implementation class in the form of HTTP implements the phased solver definition.</p>
 *
 * @author zpf
 * @since 1.1.1
 */
public interface HttpResultSolver {

    void handlerSdkError(HttpRequest<?> request, SdkException e);

    void handlerUnKnowError(HttpRequest<?> request, Throwable e);

    void finallyHandler(ExecuteInfo info);

    /**
     * Usually collected for general information after execution.
     */
    interface ExecuteInfo {

        long getSpendTotalTimeMillis();

        Supplier<Boolean> noHappenError();

        HttpRequest<?> getHttpRequest();

        String getResponse();

        String getErrorMessage();
    }

    /**
     * The builder class for interface {@link ExecuteInfo}.
     */
    class ExecuteInfoBuild {

        long spendTotalTimeMillis;

        @Nullable
        Throwable error;

        HttpRequest<?> httpRequest;

        String response;

        public static ExecuteInfoBuild builder() {
            return new ExecuteInfoBuild();
        }

        public ExecuteInfoBuild spend(long spendTotalTimeMillis) {
            this.spendTotalTimeMillis = spendTotalTimeMillis;
            return this;
        }

        public ExecuteInfoBuild requestAccess(HttpRequest<?> httpRequest) {
            this.httpRequest = httpRequest;
            return this;
        }

        public ExecuteInfoBuild maybeError(Throwable error) {
            this.error = error;
            return this;
        }

        public ExecuteInfoBuild response(String response) {
            this.response = response;
            return this;
        }

        public DefaultExecuteInfo build() {
            return new DefaultExecuteInfo(
                    spendTotalTimeMillis,
                    error,
                    httpRequest,
                    response
            );
        }
    }
}
