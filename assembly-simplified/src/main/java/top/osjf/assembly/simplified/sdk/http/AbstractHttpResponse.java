package top.osjf.assembly.simplified.sdk.http;

import top.osjf.assembly.simplified.sdk.process.AbstractResponse;
import top.osjf.assembly.simplified.sdk.process.DefaultErrorResponse;

import java.util.Objects;

/**
 * Http response abstract node class, used to define common states,
 * unknown error messages, success plans, etc.
 *
 * <p>You can check the example code:
 * <pre>
 * {@code
 * public class TestR extends AbstractHttpResponse {
 *
 *     private Boolean success;
 *
 *     private Integer code;
 *
 *     private String message;
 *
 *     private Object errors;
 *
 *     private List<Supplier> data;
 * }}
 * </pre>
 *
 * <p>Due to differences in encapsulation interfaces, public fields are not provided here.
 * If you need to default, please refer to {@link DefaultErrorResponse}.
 * <dl>
 *     <dt>{@link DefaultErrorResponse#buildSdkExceptionResponse(String)}</dt>
 *     <dt>{@link DefaultErrorResponse#buildUnknownResponse(String)}</dt>
 *     <dt>{@link DefaultErrorResponse#buildDataErrorResponse(String)}</dt>
 * </dl>
 *
 * <p>The prerequisite for use is to check if the field name is consistent
 * with yours, otherwise the default information in {@link AbstractResponse}
 * will be obtained.
 *
 * @author zpf
 * @since 1.1.1
 */
@SuppressWarnings("serial")
public abstract class AbstractHttpResponse extends AbstractResponse implements HttpResponse {

    public static final String SUCCESS_MESSAGE = "Congratulations";

    public static final String FAILED_MESSAGE = "Internal system error";

    /** {@code isSuccess} and {@code  getMessage} define http success situation.*/

    @Override
    public boolean isSuccess() {
        return Objects.equals(getCode(), SC_OK) || Objects.equals(getCode(),SC_OK0);
    }

    @Override
    public String getMessage() {
        return SUCCESS_MESSAGE;
    }

    /*
     * To avoid affecting the rewrite return type of subclasses, it is written as an object type.
     * */
    public Object getCode() {
        return SC_OK;
    }
}
