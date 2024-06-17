package top.osjf.assembly.cache.factory;

/**
 * An exception that should be thrown when the cache center is
 * not initialized during retrieval.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.1.4
 */
public class CenterUninitializedException extends RuntimeException {

    private static final long serialVersionUID = 8369448220762087208L;

    @SuppressWarnings("rawtypes")
    public CenterUninitializedException(Class<? extends Center> centerType) {
        super(centerType.getName());
    }
}
