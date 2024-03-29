package top.osjf.generated.impl;

import top.osjf.assembly.util.annotation.NotNull;
import top.osjf.generated.GeneratedSourceAllocation;
import top.osjf.generated.Logger;

import javax.annotation.processing.Filer;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;

/**
 * The integration logic of annotation processor {@link SpringServiceGroup} for
 * {@link SpringServiceGroupProcessor}.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @see SpringServiceGroup
 * @see SpringServiceGroupProcessor
 * @since 1.1.3
 */
public class SpringServiceGenerateMetadataCollector extends AbstractImplMetadataCollector<SpringServiceGroup> {

    public SpringServiceGenerateMetadataCollector(SpringServiceGroup annotation,
                                                  RoundEnvironment roundEnvironment,
                                                  Element element, Filer filer, Logger logger) {
        super(annotation, roundEnvironment, element, filer, logger);
    }

    @Override
    @NotNull
    public GeneratedSourceAllocation getGeneratedSourceAllocation() {
        return new SpringServiceGenerateMetadataCollector$GeneratedSourceAllocation(getAnnotation());
    }
}
