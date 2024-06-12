package top.osjf.assembly.cache.autoconfigure;

import org.springframework.beans.factory.InitializingBean;
import top.osjf.assembly.cache.persistence.ByteCachePersistence;

/**
 * Cache the process information bean of the persistent file recovery class.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.8
 */
public class PersistenceReductionProcess implements InitializingBean {

    private final String path;

    public PersistenceReductionProcess(String path) {
        this.path = path;
    }

    @Override
    public void afterPropertiesSet() {
        new ByteCachePersistence().reductionUsePath(path);
    }
}
