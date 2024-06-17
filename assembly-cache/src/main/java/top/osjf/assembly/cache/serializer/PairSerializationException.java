package top.osjf.assembly.cache.serializer;

import org.apache.commons.lang3.SerializationException;

/**
 * Exception thrown when the Serialization process fails.
 * <p>The original error is wrapped within this one.</p>
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.1.4
 */
public class PairSerializationException extends SerializationException {

    private static final long serialVersionUID = -7504517350393310438L;

    public PairSerializationException(Throwable cause) {
        super(cause);
    }
}
