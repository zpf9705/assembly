package top.osjf.generated.impl;

import top.osjf.generated.AbstractInitializationProcessor;

import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

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
    public void process0(Element element, RoundEnvironment roundEnv) {
        new SourceCodeGenerateMetadataCollector(element.getAnnotation(GeneratedSourceGroup.class),
                (TypeElement) element,getFiler(),this);
    }
}
