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
 *
 * @author zpf
 * @since 1.1.0
 */
public abstract class AbstractHttpResponse extends AbstractResponse {
}
