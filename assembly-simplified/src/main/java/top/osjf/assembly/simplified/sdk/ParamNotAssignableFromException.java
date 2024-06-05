package top.osjf.assembly.simplified.sdk;

import top.osjf.assembly.simplified.sdk.process.Request;
import top.osjf.assembly.simplified.sdk.process.Response;

/**
 * Used to indicate an exception thrown when checking proxy method
 * execution callbacks that do not satisfy subclasses with input
 * parameter {@link Request} and output subclasses with input
 * parameter {@link Response}.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 2.2.5
 */
public class ParamNotAssignableFromException extends SdkException {

    private static final long serialVersionUID = -229096675399838690L;

    public ParamNotAssignableFromException() {
        super("The SDK proxy method needs to satisfy that the input parameter " +
                "type is a subclass of " + Request.class.getName() + ", and the " +
                "output parameter is a subclass of " + Response.class.getName() + ".");
    }
}
