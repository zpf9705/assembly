package top.osjf.assembly.sdk.process;


import top.osjf.assembly.sdk.SdkException;

import java.util.Collections;
import java.util.Map;

/**
 * <p>The abstract implementation of {@link Request} mainly focuses on default implementation of some
 * rules and methods of {@link Request}.</p>
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
}
