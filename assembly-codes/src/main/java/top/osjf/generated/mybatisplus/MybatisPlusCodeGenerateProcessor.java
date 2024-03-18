package top.osjf.generated.mybatisplus;

import com.baomidou.mybatisplus.annotation.TableName;
import top.osjf.generated.AbstractSmartProcessor;

import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

/**
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.1.0
 */
@SupportedAnnotationTypes(MybatisPlusCodeGenerateProcessor.SUPPORT_OF_MP_SERVICE)
public class MybatisPlusCodeGenerateProcessor extends AbstractSmartProcessor {

    public static final String SUPPORT_OF_MP_SERVICE
            = "top.osjf.generated.mybatisplus.MybatisPlusCodeGenerate";

    @Override
    public void process0(Element element, RoundEnvironment roundEnv) {
        MybatisPlusCodeGenerateActuator
                .setAndNew(element.getAnnotation(MybatisPlusCodeGenerate.class), (TypeElement) element, roundEnv)
                .toGenerate();
    }

    @Override
    public boolean elementFilterCondition(Element element) {
        return element.getAnnotation(TableName.class) != null && element instanceof TypeElement;
    }
}
