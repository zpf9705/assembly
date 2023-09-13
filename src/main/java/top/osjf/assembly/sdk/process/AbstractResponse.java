package top.osjf.assembly.sdk.process;


/**
 * <p>The abstract implementation of {@link Response} mainly focuses on default implementation of some
 * rules and methods of {@link Response}.</p>
 * <p>The default implementation is to convert the format when {@link DefaultResponse} users encounter exceptions.</p>
 *
 * @author zpf
 * @since 1.1.0
 */
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
}
