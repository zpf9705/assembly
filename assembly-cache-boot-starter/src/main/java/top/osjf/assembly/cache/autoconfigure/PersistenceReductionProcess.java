package top.osjf.assembly.cache.autoconfigure;

import top.osjf.assembly.cache.persistence.CachePersistenceReduction;
import top.osjf.assembly.util.lang.ReflectUtils;
import top.osjf.assembly.util.lang.StringUtils;

import javax.annotation.PostConstruct;

/**
 * Cache the process information bean of the persistent file recovery class.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.8
 */
public class PersistenceReductionProcess {

    private final String path;

    private final Class<? extends CachePersistenceReduction> persistenceReductionClass;

    private String resultMessage;

    public PersistenceReductionProcess(String path,
                                       Class<? extends CachePersistenceReduction> persistenceReductionClass) {
        this.path = path;
        this.persistenceReductionClass = persistenceReductionClass;
    }

    public String getPath() {
        return path;
    }

    public Class<? extends CachePersistenceReduction> getPersistenceReductionClass() {
        return persistenceReductionClass;
    }

    public String getResultMessage() {
        return resultMessage;
    }

    @PostConstruct
    public void reductionUsePath() {
        if (StringUtils.isBlank(path) || persistenceReductionClass == null) {
            resultMessage = "reductionPath or persistenceReductionClass is null";
            return;
        }
        CachePersistenceReduction reduction;
        try {
            reduction = ReflectUtils.newInstance(persistenceReductionClass);
        } catch (Exception e) {
            resultMessage = "persistenceReductionClass newInstance  for default Constructor filed";
            return;
        }
        reduction.reductionUsePath(path);
        resultMessage = "process";
    }
}
