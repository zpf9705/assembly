package top.osjf.generated.mybatisplus;

import com.baomidou.mybatisplus.annotation.TableName;
import top.osjf.generated.AbstractSmartProcessor;

import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

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
public class MybatisPlusCodeGenerateProcessor extends AbstractSmartProcessor {

    public static final String SUPPORT_OF_MPC_GENERATE_NAME = "top.osjf.generated.mybatisplus.MybatisPlusCodeGenerate";

    @Override
    public void process0(Element element, RoundEnvironment roundEnv) {
        new MybatisPlusCodeGenerateMetadataCollector(element.getAnnotation(MybatisPlusCodeGenerate.class),
                (TypeElement) element,
                getFiler(), this)
                .process();
    }

    @Override
    public boolean elementFilterCondition(Element element) {
        return element.getAnnotation(TableName.class) != null && element instanceof TypeElement;
    }
}
