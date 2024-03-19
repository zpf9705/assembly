package top.osjf.generated.mybatisplus;

import top.osjf.assembly.util.annotation.NotNull;
import top.osjf.assembly.util.lang.MapUtils;
import top.osjf.generated.ClassKind;
import top.osjf.generated.CodeGenerateInvocation;
import top.osjf.generated.GeneratedCodeAppenderBuilder;

/**
 * Generate the necessary service implementation class for accessing
 * database tables using the mybatis plus framework.
 * To modify the class, specify the corresponding mapper interface and
 * service interface, integrate Spring, and add Spring's service annotations.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.1.0
 */
public class ServiceImplCodeGenerateInvocationImpl extends AbstractMybatisPlusCodeGenerateInvocation implements
        ServiceImplCodeGenerateInvocation {

    public static final String MYBATIS_PLUS_BASE_SERVICE_IMPL =
            "com.baomidou.mybatisplus.extension.service.impl.ServiceImpl";

    public static final String MYBATIS_PLUS_JOIN_BASE_SERVICE_IMPL
            = "com.github.yulichang.base.MPJBaseServiceImpl";

    public static final String SPRING_SERVICE_ANNOTATION = "org.springframework.stereotype.Service";

    private CodeGenerateInvocation mapper;

    private CodeGenerateInvocation service;

    public ServiceImplCodeGenerateInvocationImpl(String simpleName, String packageName, String targetName,
                                                 boolean join, String tableChineseName) {
        super(simpleName, packageName, targetName, join, tableChineseName);
    }


    @Override
    public ServiceImplCodeGenerateInvocationImpl mapper(CodeGenerateInvocation codeGenerateInvocation) {
        this.mapper = codeGenerateInvocation;
        return this;
    }

    @Override
    public ServiceImplCodeGenerateInvocationImpl service(CodeGenerateInvocation codeGenerateInvocation) {
        this.service = codeGenerateInvocation;
        return this;
    }

    @NotNull
    @Override
    protected GeneratedCodeAppenderBuilder getGeneratedCodeAppenderBuilder() {

        GeneratedCodeAppenderBuilder builder = super.getGeneratedCodeAppenderBuilder();

        builder.classKind(ClassKind.CLASS)
                .extend(getJoin() ? MYBATIS_PLUS_JOIN_BASE_SERVICE_IMPL :
                        MYBATIS_PLUS_BASE_SERVICE_IMPL)
                .annotations(MapUtils.of(SPRING_SERVICE_ANNOTATION, ""));

        if (mapper != null) {
            builder.extendGenerics(new String[]{mapper.getName(), getTargetName()});
        }

        if (service != null) {
            builder.interfaces(MapUtils.of(service.getName(), new String[]{}));
        }

        return builder;
    }
}
