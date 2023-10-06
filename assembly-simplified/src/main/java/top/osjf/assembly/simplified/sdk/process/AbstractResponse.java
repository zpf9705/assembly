package top.osjf.assembly.simplified.sdk.process;


/**
 * <p>The abstract implementation of {@link Response} mainly focuses on default implementation of some
 * rules and methods of {@link Response}.</p>
 *
 * <p>The default implementation is to convert the format when {@link DefaultErrorResponse} users encounter
 * exceptions.</p>
 *
 * @author zpf
 * @since 1.1.0
 */
@SuppressWarnings("serial")
public abstract class AbstractResponse implements Response {

    private static final boolean DEFAULT_IS_SUCCESS = false;

    private static final String DEFAULT_MESSAGE = "UNKNOWN";

    @Override
    public boolean isSuccess() {
        return DEFAULT_IS_SUCCESS;
    }

    @Override
    public String getMessage() {
        return DEFAULT_MESSAGE;
    }

    @Override
    public void setErrorCode(Integer code) {
    }

    @Override
    public void setErrorMessage(String errorMessage) {
    }
}
