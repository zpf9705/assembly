package top.osjf.generated.impl;

import top.osjf.assembly.util.annotation.NotNull;
import top.osjf.generated.*;

/**
 * Create source code files for {@link GeneratedSourceGroup} annotations and
 * write them to the executor.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.1.0
 */
public class SourceCodeGenerateInvocation extends AbstractCodeGenerateInvocation {

    private final ClassKind classKind;

    private final String extendClassName;

    private final String[] extendGenericsClassNames;

    private final ClassSource[] interfaceClassSources;

    private final AnnotationSource[] annotationSources;

    public SourceCodeGenerateInvocation(String simpleName, String packageName,
                                        String target, ClassKind classKind, String extendClassName,
                                        String[] extendGenericsClassNames, ClassSource[] interfaceClassSources,
                                        AnnotationSource[] annotationSources) {
        super(simpleName, packageName, target);
        this.classKind = classKind;
        this.extendClassName = extendClassName;
        this.extendGenericsClassNames = extendGenericsClassNames;
        this.interfaceClassSources = interfaceClassSources;
        this.annotationSources = annotationSources;
    }

    @NotNull
    @Override
    protected GeneratedCodeAppenderBuilder getGeneratedCodeAppenderBuilder() {

        GeneratedCodeAppenderBuilder builder = super.getGeneratedCodeAppenderBuilder();

        builder.classKind(classKind)
                .extend(extendClassName)
                .extendGenerics(extendGenericsClassNames)
                .interfaces(GeneratedUtils.convertInterfaceClassNameSources(interfaceClassSources))
                .annotations(GeneratedUtils.convertAnnotationNameSources(annotationSources))
                .build();

        return builder;
    }
}
