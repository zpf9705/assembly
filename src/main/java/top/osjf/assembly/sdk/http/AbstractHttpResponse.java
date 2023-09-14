package top.osjf.assembly.sdk.http;


import top.osjf.assembly.sdk.process.AbstractResponse;

/**
 * Http response abstract node class, used to define common states, unknown error messages, success plans, etc.
 * <p>
 * You can check the example code:
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
 * <p>
 * Due to differences in encapsulation interfaces, public fields are not provided here.
 * If you need to default, please refer to {@link top.osjf.assembly.sdk.process.DefaultResponse}
 * <dl>
 *     <dt>{@link top.osjf.assembly.sdk.process.DefaultResponse#buildSdkExceptionResponse(String)}</dt>
 *     <dt>{@link top.osjf.assembly.sdk.process.DefaultResponse#buildUnknownResponse(String)}</dt>
 *     <dt>{@link top.osjf.assembly.sdk.process.DefaultResponse#buildDataErrorResponse(String)}</dt>
 * </dl>
 * <p>
 * The prerequisite for use is to check if the field name is consistent with yours, otherwise the
 * default information in {@link AbstractResponse} will be obtained.
 *
 * @author zpf
 * @since 1.1.1
 */
public abstract class AbstractHttpResponse extends AbstractResponse {
}
