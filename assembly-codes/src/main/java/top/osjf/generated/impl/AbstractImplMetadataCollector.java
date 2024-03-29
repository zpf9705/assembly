package top.osjf.generated.impl;

import top.osjf.assembly.util.annotation.NotNull;
import top.osjf.assembly.util.data.Triple;
import top.osjf.assembly.util.lang.ArrayUtils;
import top.osjf.assembly.util.lang.MapUtils;
import top.osjf.assembly.util.lang.StringUtils;
import top.osjf.generated.*;

import javax.annotation.processing.Filer;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

/**
 * For the processing scheme of generating implementation class annotation
 * processors, handle the abstract class of announcements, obtain the
 * {@link GeneratedSourceAllocation} announcement definition properties
 * from subclasses, and execute them uniformly in the {} method.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @see GeneratedSourceAllocation
 * @since 1.1.3
 */
public abstract class AbstractImplMetadataCollector<T extends Annotation> extends AbstractMetadataCollector<T> {

    public AbstractImplMetadataCollector(T annotation, RoundEnvironment roundEnvironment,
                                         Element element,
                                         Filer filer, Logger logger) {
        super(annotation, roundEnvironment, element, filer, logger);
    }

    @Override
    public void process() {

        GeneratedSourceAllocation allocation = getGeneratedSourceAllocation();

        TypeElement element = getElement();

        String unifiedPackageName = allocation.getUnifiedPackageName();

        ClassKind unifiedClassKind = allocation.getUnifiedClassKind();

        String unifiedExtendClassName = allocation.getUnifiedExtendClassName();

        String[] unifiedExtendGenericsClassNames = allocation.getUnifiedExtendGenericsClassNames();

        Map<String, String[]> unifiedInterfaceClassSources = allocation.getUnifiedInterfaceClassSources();

        Map<String, String> unifiedAnnotationSources = allocation.getUnifiedAnnotationSources();

        String targetName = element.getQualifiedName().toString();

        AtomicLong noProviderSimpleNameCounter = new AtomicLong(0);

        while (allocation.hasNext()) {

            GeneratedSourceAllocation.GroupSourceEntry sourceEntry = allocation.next();

            String usePackageName = sourceEntry.getPackageName();

            ClassKind useClassKind = sourceEntry.getClassKind();

            String useExtendClassName = sourceEntry.getExtendClassName();

            String[] useGenericsClassNames = sourceEntry.getExtendGenericsClassNames();

            Map<String, String[]> useInterfaceClassSources = sourceEntry.getInterfaceClassSources();

            Map<String, String> useAnnotationSources = sourceEntry.getAnnotationSources();

            if (StringUtils.isBlank(usePackageName)) usePackageName = unifiedPackageName;

            if (Objects.isNull(useClassKind)) useClassKind = unifiedClassKind;

            if (StringUtils.isBlank(useExtendClassName)) useExtendClassName = unifiedExtendClassName;

            if (ArrayUtils.isEmpty(useGenericsClassNames)) useGenericsClassNames = unifiedExtendGenericsClassNames;

            if (MapUtils.isEmpty(useInterfaceClassSources)) useInterfaceClassSources = unifiedInterfaceClassSources;

            if (MapUtils.isEmpty(useAnnotationSources)) useAnnotationSources = unifiedAnnotationSources;

            Triple<String, String, String> names = GeneratedUtils.getNames(
                    sourceEntry.getSimpleName(),
                    usePackageName,
                    element.getQualifiedName(), element.getSimpleName(), noProviderSimpleNameCounter);

            new ImplSourceCodeGenerateInvocation(
                    names.getV1(),
                    names.getV2(),
                    targetName,
                    useClassKind,
                    useExtendClassName,
                    useGenericsClassNames,
                    useInterfaceClassSources,
                    useAnnotationSources).write(getFiler(), getLogger());
        }
    }

    /**
     * Return the basic metadata interface required to generate class code.
     * @return the basic metadata interface required to generate class code.
     */
    @NotNull
    public abstract GeneratedSourceAllocation getGeneratedSourceAllocation();
}
