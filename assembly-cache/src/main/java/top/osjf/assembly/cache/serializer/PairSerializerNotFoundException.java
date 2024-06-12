package top.osjf.assembly.cache.serializer;

/**
 * Serialize the exception type that was not found during
 * cache recovery for the specified type.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.1.4
 */
public class PairSerializerNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 2233673880961466847L;

    public PairSerializerNotFoundException(String pairSerializerName) {
        super("PairSerializer Implementation class " + pairSerializerName + " not found !");
    }
}
