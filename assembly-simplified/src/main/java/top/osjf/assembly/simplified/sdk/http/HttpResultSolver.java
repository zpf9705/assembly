package top.osjf.assembly.simplified.sdk.http;

import top.osjf.assembly.simplified.sdk.SdkException;
import top.osjf.assembly.simplified.sdk.process.Request;
import top.osjf.assembly.util.annotation.CanNull;

import java.util.function.Supplier;

/**
 * <p>The client implementation class in the form of HTTP implements
 * the phased solver definition.</p>
 *
 * @author zpf
 * @since 1.1.1
 */
public interface HttpResultSolver {

    /**
     * The type of {@link SdkException} exception that runs during the
     * request processing is usually manually checked and thrown in the
     * reference method {@link Request#validate()}.
     *
     * @param request Request parameters.
     * @param e       Exception {@link SdkException}.
     */
    void handlerSdkError(HttpRequest<?> request, SdkException e);

    /**
     * The type of {@link Throwable} exception that runs during the
     * request processing is usually an exception thrown by the requester
     * or other unknown exceptions such as network exceptions.
     *
     * @param request Request parameters.
     * @param e       UnKnow Exception {@link Throwable}.
     */
    void handlerUnKnowError(HttpRequest<?> request, Throwable e);

    /**
     * The final process is to call the interface implementation
     * class {@link ExecuteInfo} encapsulated by the metadata of
     * this request call in the finally of try catch.
     *
     * @param info {@link ExecuteInfo}.
     */
    void finallyHandler(ExecuteInfo info);

    /**
     * Usually collected for general information after execution.
     */
    interface ExecuteInfo {

        /**
         * @return Returns the number of milliseconds spent on this request.
         */
        long getSpendTotalTimeMillis();

        /**
         * @return Return the {@link Supplier} package type indicating whether the request was successful.
         */
        Supplier<Boolean> noHappenError();

        /**
         * @return Returns the parameters for this request.
         */
        HttpRequest<?> getHttpRequest();

        /**
         * @return Returns the body of this request.
         */
        String getResponse();

        /**
         * @return Returns the error response information for this request.
         */
        String getErrorMessage();
    }

    /**
     * The builder class for interface {@link ExecuteInfo}.
     */
    class ExecuteInfoBuild {

        long spendTotalTimeMillis;

        @CanNull
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
