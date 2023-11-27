package top.osjf.assembly.simplified.sdk.http;

import top.osjf.assembly.util.exceptions.ExceptionUtils;

import java.util.function.Supplier;

/**
 * The default implementation class for the HTTP execution result
 * collection interface {@link HttpResultSolver.ExecuteInfo}.
 *
 * @author zpf
 * @since 1.1.1
 */
public class DefaultExecuteInfo implements HttpResultSolver.ExecuteInfo {

    private final long spendTotalTimeMillis;

    private final Throwable error;

    private final HttpRequest<?> httpRequest;

    private final String response;

    public DefaultExecuteInfo(long spendTotalTimeMillis, Throwable error, HttpRequest<?> httpRequest, String response) {
        this.spendTotalTimeMillis = spendTotalTimeMillis;
        this.error = error;
        this.httpRequest = httpRequest;
        this.response = response;
    }

    @Override
    public long getSpendTotalTimeMillis() {
        return spendTotalTimeMillis;
    }

    @Override
    public Supplier<Boolean> noHappenError() {
        return () -> error == null;
    }

    @Override
    public HttpRequest<?> getHttpRequest() {
        return httpRequest;
    }

    @Override
    public String getResponse() {
        return response;
    }

    @Override
    public String getErrorMessage() {
        return error == null ? null : ExceptionUtils.stacktraceToOneLineString(error);
    }
}
