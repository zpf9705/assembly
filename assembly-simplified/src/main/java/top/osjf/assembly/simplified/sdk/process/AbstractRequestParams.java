package top.osjf.assembly.simplified.sdk.process;

import top.osjf.assembly.simplified.sdk.SdkException;

import java.util.Collections;
import java.util.Map;

/**
 * <p>The abstract implementation of {@link Request} mainly focuses on
 * default implementation of some rules and methods of {@link Request}.</p>
 *
 * @param <R> Implement a unified response class data type.
 * @author zpf
 * @since 1.1.0
 */
@SuppressWarnings("serial")
public abstract class AbstractRequestParams<R extends AbstractResponse> implements Request<R> {

    /** {@inheritDoc}*/
    @Override
    public Map<String, String> getHeadMap() {
        return Collections.emptyMap();
    }

    /** {@inheritDoc}*/
    @Override
    public void validate() throws SdkException {
    }
}
