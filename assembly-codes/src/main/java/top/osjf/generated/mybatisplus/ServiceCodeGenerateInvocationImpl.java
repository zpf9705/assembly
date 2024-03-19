package top.osjf.generated.mybatisplus;

import top.osjf.assembly.util.annotation.NotNull;
import top.osjf.generated.ClassKind;
import top.osjf.generated.GeneratedCodeAppenderBuilder;
import top.osjf.generated.impl.annotation.AbstractCodeGenerateInvocation;

/**
 * Generate the necessary service interface for accessing database
 * tables using the mybatis plus framework.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.1.0
 */
public class ServiceCodeGenerateInvocationImpl extends AbstractCodeGenerateInvocation {

    public static final String MYBATIS_PLUS_BASE_SERVICE = "com.baomidou.mybatisplus.extension.service.IService";

    public ServiceCodeGenerateInvocationImpl(String simpleName, String packageName, String targetName) {
        super(simpleName, packageName, targetName);
    }

    @NotNull
    @Override
    protected GeneratedCodeAppenderBuilder getGeneratedCodeAppenderBuilder() {

        GeneratedCodeAppenderBuilder builder = super.getGeneratedCodeAppenderBuilder();

        return builder.classKind(ClassKind.INTERFACE)
                .extend(MYBATIS_PLUS_BASE_SERVICE)
                .extendGenerics(new String[]{getTargetName()});
    }
}
