package top.osjf.generated.impl;

import top.osjf.generated.AbstractInitializationProcessor;
import top.osjf.generated.MetadataCollector;

import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.lang.annotation.Annotation;

/**
 * Compilation time annotation processor for {@link GeneratedSourceGroup}.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.1.0
 */
@SupportedAnnotationTypes(GeneratedSourceGroupProcessor.SUPPORT_OF_GENERATED_SOURCE)
public class GeneratedSourceGroupProcessor extends AbstractInitializationProcessor {

    public static final String SUPPORT_OF_GENERATED_SOURCE = "top.osjf.generated.impl.GeneratedSourceGroup";

    @Override
    public boolean elementFilterCondition(Element element) {
        return element instanceof TypeElement;
    }

    @Override
    public Class<? extends MetadataCollector<?>> getProcessorCollectorType() {
        return SourceCodeGenerateMetadataCollector.class;
    }

    @Override
    public Class<? extends Annotation> getTriggerAnnotationType() {
        return GeneratedSourceGroup.class;
    }
}
