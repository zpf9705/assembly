package top.osjf.generated.impl;

import top.osjf.generated.MetadataCollector;

import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import java.lang.annotation.Annotation;

/**
 * Compilation time annotation processor for {@link SpringServiceGroup}.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.1.3
 */
@SupportedAnnotationTypes(SpringServiceGroupProcessor.SUPPORT_OF_SPRING_SERVICE_SOURCE)
public class SpringServiceGroupProcessor extends GeneratedSourceGroupProcessor {

    public static final String SUPPORT_OF_SPRING_SERVICE_SOURCE = "top.osjf.generated.impl.SpringServiceGroup";

    @Override
    public boolean elementFilterCondition(Element element) {
        return super.elementFilterCondition(element) && ElementKind.INTERFACE.equals(element.getKind());
    }

    @Override
    public Class<? extends MetadataCollector<?>> getProcessorCollectorType() {
        return SpringServiceGenerateMetadataCollector.class;
    }

    @Override
    public Class<? extends Annotation> getTriggerAnnotationType() {
        return SpringServiceGroup.class;
    }
}
