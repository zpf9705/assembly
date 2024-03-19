package top.osjf.generated.mybatisplus;

import top.osjf.assembly.util.annotation.NotNull;
import top.osjf.generated.ClassKind;
import top.osjf.generated.GeneratedCodeAppenderBuilder;
import top.osjf.generated.impl.annotation.AbstractCodeGenerateInvocation;

/**
 * Generate the necessary mapper interface for accessing database tables
 * using the mybatis plus framework.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.1.0
 */
public class MapperCodeGenerateInvocationImpl extends AbstractCodeGenerateInvocation {

    public static final String MYBATIS_PLUS_BASE_MAPPER = "com.baomidou.mybatisplus.core.mapper.BaseMapper";

    public MapperCodeGenerateInvocationImpl(String simpleName, String packageName, String targetName) {
        super(simpleName, packageName, targetName);
    }

    @NotNull
    @Override
    protected GeneratedCodeAppenderBuilder getGeneratedCodeAppenderBuilder() {

        GeneratedCodeAppenderBuilder builder = super.getGeneratedCodeAppenderBuilder();

        return builder
                .classKind(ClassKind.INTERFACE)
                .extend(MYBATIS_PLUS_BASE_MAPPER)
                .extendGenerics(new String[]{getTargetName()});
    }
}
