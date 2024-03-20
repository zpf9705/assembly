package top.osjf.generated.impl;

import top.osjf.assembly.util.data.Triple;
import top.osjf.assembly.util.lang.ArrayUtils;
import top.osjf.assembly.util.lang.StringUtils;
import top.osjf.generated.*;

import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Compilation time annotation processor for {@link GeneratedSourceGroup}.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.1.0
 */
@SupportedAnnotationTypes(GeneratedSourceGroupProcessor.SUPPORT_OF_GENERATED_SOURCE)
public class GeneratedSourceGroupProcessor extends AbstractSmartProcessor {

    public static final String SUPPORT_OF_GENERATED_SOURCE = "top.osjf.generated.impl.GeneratedSourceGroup";

    @Override
    public boolean elementFilterCondition(Element element) {
        return element instanceof TypeElement;
    }

    @Override
    public void process0(Element element, RoundEnvironment roundEnv) {
        generateCreateSourceFile((TypeElement) element);
    }

    private void generateCreateSourceFile(TypeElement typeElement) {

        GeneratedSourceGroup group = typeElement.getAnnotation(GeneratedSourceGroup.class);

        String globePackageName = group.packageName();

        ClassKind globeClassKind = group.classKind();

        String globeExtendClassName = group.extendClassName();

        String[] globeGenericsClassNames = group.extendGenericsClassNames();

        ClassSource[] globeInterfaceClassSources = group.interfaceClassSources();

        AnnotationSource[] globeAnnotationSources = group.annotationSources();

        String targetName = typeElement.getQualifiedName().toString();

        AtomicLong noProviderSimpleNameCounter = new AtomicLong(0);

        for (GeneratedSource source : group.group()) {

            String usePackageName;

            ClassKind useClassKind;

            String useExtendClassName;

            String[] useGenericsClassNames;

            ClassSource[] useInterfaceClassSources;

            AnnotationSource[] useAnnotationSources;

            if (StringUtils.isNotBlank(globePackageName)) {
                usePackageName = globePackageName;
            } else usePackageName = source.packageName();

            if (globeClassKind != null) {
                useClassKind = globeClassKind;
            } else useClassKind = source.classKind();

            if (StringUtils.isNotBlank(globeExtendClassName)) {
                useExtendClassName = globeExtendClassName;
            } else useExtendClassName = source.extendClassName();

            if (ArrayUtils.isNotEmpty(globeGenericsClassNames)) {
                useGenericsClassNames = globeGenericsClassNames;
            } else useGenericsClassNames = source.extendGenericsClassNames();

            if (ArrayUtils.isNotEmpty(globeInterfaceClassSources)) {
                useInterfaceClassSources = globeInterfaceClassSources;
            } else useInterfaceClassSources = source.interfaceClassSources();

            if (ArrayUtils.isNotEmpty(globeAnnotationSources)) {
                useAnnotationSources = globeAnnotationSources;
            } else useAnnotationSources = source.annotationSources();

            Triple<String, String, String> names = GeneratedUtils.getNames(
                    source.simpleName(),
                    usePackageName,
                    typeElement.getQualifiedName(), typeElement.getSimpleName(),
                    noProviderSimpleNameCounter);

            new SourceCodeGenerateInvocation(
                    names.getV1(),
                    names.getV2(),
                    targetName,
                    useClassKind,
                    useExtendClassName,
                    useGenericsClassNames,
                    useInterfaceClassSources,
                    useAnnotationSources).write(getFiler(), this);
        }
    }
}
