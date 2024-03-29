package top.osjf.generated.impl;

import top.osjf.assembly.util.annotation.NotNull;
import top.osjf.generated.*;

import java.util.Map;

/**
 * Before 1.1.2 to create source code files for {@link GeneratedSourceGroup} annotations and
 * write them to the executor.
 *
 * <p> After version 1.1.3, it was renamed {@code ImplSourceCodeGenerateInvocation},
 * focusing on matters related to the generation of service implementation classes.
 *
 * @see GeneratedSourceGroup
 * @see SpringServiceSource
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.1.0
 */
public class ImplSourceCodeGenerateInvocation extends AbstractCodeGenerateInvocation {

    private final ClassKind classKind;

    private final String extendClassName;

    private final String[] extendGenericsClassNames;

    private final Map<String, String[]> interfaceClassSources;

    private final Map<String, String> annotationSources;

    public ImplSourceCodeGenerateInvocation(String simpleName, String packageName,
                                        String target, ClassKind classKind, String extendClassName,
                                        String[] extendGenericsClassNames,
                                        Map<String, String[]> interfaceClassSources,
                                        Map<String, String> annotationSources) {
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
                .interfaces(interfaceClassSources)
                .annotations(annotationSources)
                .build();

        return builder;
    }
}
