package top.osjf.assembly.sdk;

import com.alibaba.fastjson.JSON;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;
import top.osjf.assembly.sdk.client.AbstractResponse;
import top.osjf.assembly.sdk.client.Request;
import top.osjf.assembly.sdk.client.Response;

import java.io.Serializable;

/**
 * This class contains all the information about the composition of the SDK, including the host address,
 * the base of the Spring proxy bean implementation class, and the final parameter convergence point of
 * the JDK method dynamic proxy. It can be said to be a fusion point between the Spring framework and
 * our custom {@link top.osjf.assembly.sdk.client.Client} scheme.
 * <p>
 * Here is an explanation of the main implementation idea: we first scan the interface classes wearing
 * {@link top.osjf.assembly.sdk.annotation.EnableSdkProxyRegister} in
 * {@link top.osjf.assembly.support.AbstractProxyBeanInjectSupport} through
 * {@link top.osjf.assembly.sdk.annotation.Sdk}'s switch annotations,
 * and then create dynamic implementation classes for these interfaces through {}.
 * <p>
 * At this point, the implementation class is given to the proxy objects created by the jdk dynamic
 * proxy, and the proxy objects are handed over to spring as the virtual implementation classes for these interfaces.
 * <p>
 * When these interfaces are called through the spring container, We will uniformly bring the parameters
 * to the proxy object and connect them to our {@link ClientUtils} processing through this class
 *
 * @author zpf
 * @since 1.1.0
 */
public class SdkProxyBeanDefinition<T> extends AbstractSdkProxyInvoker<T> implements Serializable {

    private static final long serialVersionUID = -4976006670359451017L;

    private String host;

    /**
     * Obtain the current request host address
     *
     * @return no be {@literal null}
     */
    @NonNull
    public String getHost() {
        return host;
    }

    /**
     * Place the current request host address
     *
     * @param host host address no be {@literal null}
     */
    public void setHost(String host) {
        this.host = host;
    }

    @Override
    @NonNull
    public Object doSdk(Request<?> request, String methodName, Class<?> responseType) throws SdkException {
        //check no null
        Assert.notNull(request, "requestParams no be null");
        Assert.notNull(methodName, "name no be null");
        Assert.notNull(responseType, "returnType no be null");
        Response response;
        try {
            response = ClientUtils.execute(this.host, request);
        } catch (Throwable e) {
            /*
             * Capture unknown exceptions and throw them in the form of {@link SdkException}
             */
            throw new SdkException(AbstractResponse.DATA_ERROR_CODE, e.getMessage());
        }
        return JSON.parseObject(response.toJson(), responseType);
    }
}
