package top.osjf.assembly.sdk;

import org.springframework.util.Assert;
import top.osjf.assembly.sdk.process.AbstractRequestParams;
import top.osjf.assembly.sdk.process.Request;
import top.osjf.assembly.sdk.process.Response;
import top.osjf.assembly.support.AbstractJdkProxySupport;

import java.lang.reflect.Method;

/**
 * The unified parameter transformation processing of jdk proxy object method calls
 * abstracts and supports the class, and ultimately hands it over to the real processing class.
 *
 * @author zpf
 * @since 1.1.0
 */
public abstract class AbstractSdkProxyInvoker<T> extends AbstractJdkProxySupport<T> {

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        Assert.notEmpty(args, "Sdk way [" + method.getName() + "] this call no args");
        //This requires a single model parameter
        Object arg = args[0];
        //Determine if it is of type AbstractRequestParams
        Assert.isTrue((arg instanceof AbstractRequestParams),
                "Sdk way [" + method.getName() + "]  args no qualified");
        //Execute call API
        return doInvoke((Request<?>) arg, method.getName(), method.getReturnType());
    }

    /**
     * Pass parameters to execute the API and provide the call method name (logging) and response type.
     *
     * @param request      Request parameters {@link Request}.
     * @param methodName   Method Name {@link SdkEnum#name()}.
     * @param responseType Final convert response class type.
     * @return Sdk return value for {@link Response}.
     */
    public abstract Object doInvoke(Request<?> request, String methodName, Class<?> responseType);
}
