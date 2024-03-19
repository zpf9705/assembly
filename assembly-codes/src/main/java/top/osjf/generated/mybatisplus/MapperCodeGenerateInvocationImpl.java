package top.osjf.generated.mybatisplus;

import top.osjf.assembly.util.annotation.NotNull;
import top.osjf.generated.ClassKind;
import top.osjf.generated.GeneratedCodeAppenderBuilder;

/**
 * Generate the necessary mapper interface for accessing database tables
 * using the mybatis plus framework.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.1.0
 */
public class MapperCodeGenerateInvocationImpl extends AbstractMybatisPlusCodeGenerateInvocation {

    public static final String MYBATIS_PLUS_BASE_MAPPER = "com.baomidou.mybatisplus.core.mapper.BaseMapper";
    public static final String MYBATIS_PLUS_JOIN_BASE_MAPPER = "com.github.yulichang.base.MPJBaseMapper";

    public MapperCodeGenerateInvocationImpl(String simpleName, String packageName, String targetName,
                                            boolean join, String tableChineseName) {
        super(simpleName, packageName, targetName, join, tableChineseName);
    }

    @NotNull
    @Override
    protected GeneratedCodeAppenderBuilder getGeneratedCodeAppenderBuilder() {

        GeneratedCodeAppenderBuilder builder = super.getGeneratedCodeAppenderBuilder();

        return builder
                .classKind(ClassKind.INTERFACE)
                .extend(getJoin() ? MYBATIS_PLUS_JOIN_BASE_MAPPER : MYBATIS_PLUS_BASE_MAPPER)
                .extendGenerics(new String[]{getTargetName()});
    }
}
