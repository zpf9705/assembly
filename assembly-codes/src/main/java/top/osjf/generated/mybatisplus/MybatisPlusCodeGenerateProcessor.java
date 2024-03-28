package top.osjf.generated.mybatisplus;

import com.baomidou.mybatisplus.annotation.TableName;
import top.osjf.generated.AbstractInitializationProcessor;
import top.osjf.generated.MetadataCollector;

import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.lang.annotation.Annotation;

/**
 * The Mybatis plus framework is a processor that automatically generates
 * bytecode level annotations {@link MybatisPlusCodeGenerate} for the mapper
 * interface, service interface, and service implementation class interface.
 *
 * @see MybatisPlusCodeGenerateMetadataCollector
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @see MybatisPlusCodeGenerate
 * @since 1.1.0
 */
@SupportedAnnotationTypes(MybatisPlusCodeGenerateProcessor.SUPPORT_OF_MPC_GENERATE_NAME)
public class MybatisPlusCodeGenerateProcessor extends AbstractInitializationProcessor {

    public static final String SUPPORT_OF_MPC_GENERATE_NAME = "top.osjf.generated.mybatisplus.MybatisPlusCodeGenerate";

    @Override
    public boolean elementFilterCondition(Element element) {
        return element.getAnnotation(TableName.class) != null && element instanceof TypeElement;
    }

    @Override
    public Class<? extends MetadataCollector<?>> getProcessorCollectorType() {
        return MybatisPlusCodeGenerateMetadataCollector.class;
    }

    @Override
    public Class<? extends Annotation> getTriggerAnnotationType() {
        return MybatisPlusCodeGenerate.class;
    }
}
