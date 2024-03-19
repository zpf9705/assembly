package top.osjf.generated.mybatisplus;

import top.osjf.assembly.util.annotation.NotNull;
import top.osjf.generated.ClassKind;
import top.osjf.generated.GeneratedCodeAppenderBuilder;

/**
 * Generate the necessary service interface for accessing database
 * tables using the mybatis plus framework.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.1.0
 */
public class ServiceCodeGenerateInvocationImpl extends AbstractMybatisPlusCodeGenerateInvocation {

    public static final String MYBATIS_PLUS_BASE_SERVICE = "com.baomidou.mybatisplus.extension.service.IService";

    public static final String MYBATIS_PLUS_JOIN_BASE_MAPPER = "com.github.yulichang.base.MPJBaseService";

    public static final String SERVICE_CHINESE_CHARACTERIZATION = "的[数据服务接口]";

    public ServiceCodeGenerateInvocationImpl(String simpleName, String packageName, String targetName,
                                             boolean join, String tableChineseName) {
        super(simpleName, packageName, targetName, join, tableChineseName);
    }

    @NotNull
    @Override
    protected GeneratedCodeAppenderBuilder getGeneratedCodeAppenderBuilder() {

        GeneratedCodeAppenderBuilder builder = super.getGeneratedCodeAppenderBuilder();

        return builder.classKind(ClassKind.INTERFACE)
                .extend(getJoin() ? MYBATIS_PLUS_JOIN_BASE_MAPPER : MYBATIS_PLUS_BASE_SERVICE)
                .extendGenerics(new String[]{getTargetName()});
    }

    @Override
    protected String getCharacterization() {
        return SERVICE_CHINESE_CHARACTERIZATION;
    }
}
