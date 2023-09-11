package top.osjf.assembly.sdk.process;


import top.osjf.assembly.sdk.SdkException;
import top.osjf.assembly.sdk.client.ClientType;

import java.util.Collections;
import java.util.Map;

/**
 * Request abstract node class, used to define the public parameters or methods of the real request parameter class.
 *
 * @author zpf
 * @since 1.1.0
 */
public abstract class AbstractRequestParams<R extends AbstractResponse> implements Request<R> {

    @Override
    public Map<String, String> getHeadMap() {
        return Collections.emptyMap();
    }

    @Override
    public void validate() throws SdkException {
    }

    @Override
    public ClientType getClientType() {
        return ClientType.HTTP;
    }
}
