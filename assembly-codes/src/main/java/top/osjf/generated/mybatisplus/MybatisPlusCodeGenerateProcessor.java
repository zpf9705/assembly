package top.osjf.generated.mybatisplus;

import com.baomidou.mybatisplus.annotation.TableName;
import top.osjf.assembly.util.lang.ReflectUtils;
import top.osjf.assembly.util.lang.StringUtils;
import top.osjf.generated.AbstractSmartProcessor;
import top.osjf.generated.CodeGenerateInvocation;
import top.osjf.generated.GeneratedUtils;

import javax.annotation.processing.Filer;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

/**
 * The Mybatis plus framework is a processor that automatically generates
 * bytecode level annotations {@link MybatisPlusCodeGenerate} for the mapper
 * interface, service interface, and service implementation class interface.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @see MybatisPlusCodeGenerate
 * @since 1.1.0
 */
@SupportedAnnotationTypes(MybatisPlusCodeGenerateProcessor.SUPPORT_OF_MPC_GENERATE_NAME)
public class MybatisPlusCodeGenerateProcessor extends AbstractSmartProcessor {

    public static final String SUPPORT_OF_MPC_GENERATE_NAME = "top.osjf.generated.mybatisplus.MybatisPlusCodeGenerate";

    @Override
    public void process0(Element element, RoundEnvironment roundEnv) {

        MybatisPlusCodeGenerate annotation = element.getAnnotation(MybatisPlusCodeGenerate.class);

        String commonPackage = annotation.codeCommonPackage();

        Filer filer = getFiler();

        TypeElement typeElement = (TypeElement) element;

        CodeGenerateInvocation mapperInvocation = getMapperNameMetadata(typeElement, annotation, commonPackage);

        if (mapperInvocation != null) mapperInvocation.write(filer, this);

        CodeGenerateInvocation serviceInvocation = getServiceNameMetadata(typeElement, annotation, commonPackage);

        if (serviceInvocation != null) serviceInvocation.write(filer, this);

        CodeGenerateInvocation serviceImplInvocation = getServiceImplNameMetadata(typeElement, annotation, commonPackage);

        if (serviceImplInvocation != null) ((ServiceImplCodeGenerateInvocation) serviceImplInvocation)
                .mapper(mapperInvocation).service(serviceInvocation).write(filer, this);
    }

    @Override
    public boolean elementFilterCondition(Element element) {
        return element.getAnnotation(TableName.class) != null && element instanceof TypeElement;
    }

    private CodeGenerateInvocation getMapperNameMetadata(TypeElement element, MybatisPlusCodeGenerate codeGenerate,
                                                         String commonPackage) {
        return getCodeGenerateInvocation(element, codeGenerate.mapperGeneratePackage(),
                MybatisPlusCodeGenerate.DEFAULT_MAPPER_PACKAGE_SUFFIX_NAME,
                codeGenerate.mapperSuffixName(),
                commonPackage,
                codeGenerate.noProviderPackageUseDefault(),
                MapperCodeGenerateInvocationImpl.class,
                codeGenerate.join(),
                codeGenerate.tableChineseName());
    }

    private CodeGenerateInvocation getServiceNameMetadata(TypeElement element, MybatisPlusCodeGenerate codeGenerate,
                                                          String commonPackage) {
        return getCodeGenerateInvocation(element, codeGenerate.serviceGeneratePackage(),
                MybatisPlusCodeGenerate.DEFAULT_SERVICE_PACKAGE_SUFFIX_NAME,
                codeGenerate.serviceSuffixName(),
                commonPackage,
                codeGenerate.noProviderPackageUseDefault(),
                ServiceCodeGenerateInvocationImpl.class,
                codeGenerate.join(),
                codeGenerate.tableChineseName());
    }

    private CodeGenerateInvocation getServiceImplNameMetadata(TypeElement element, MybatisPlusCodeGenerate codeGenerate,
                                                              String commonPackage) {
        return getCodeGenerateInvocation(element, codeGenerate.serviceImplGeneratePackage(),
                MybatisPlusCodeGenerate.DEFAULT_SERVICE_IMPL_PACKAGE_SUFFIX_NAME,
                codeGenerate.serviceImplSuffixName(),
                commonPackage,
                codeGenerate.noProviderPackageUseDefault(),
                ServiceImplCodeGenerateInvocationImpl.class,
                codeGenerate.join(),
                codeGenerate.tableChineseName());
    }

    private CodeGenerateInvocation getCodeGenerateInvocation(TypeElement element, String appointPackage,
                                                             String defaultSuffixPackageName, String suffixName,
                                                             String commonPackage,
                                                             boolean noProviderPackageUseDefault,
                                                             Class<? extends CodeGenerateInvocation> clazz,
                                                             boolean join,
                                                             String tableChineseName) {
        if (StringUtils.isBlank(appointPackage)) {
            appointPackage = commonPackage;
            if (StringUtils.isBlank(appointPackage)) {
                appointPackage = getTypeElementNoYourSelfPackage(element, noProviderPackageUseDefault,
                        defaultSuffixPackageName);
            }
        }
        String tableClassSimpleName = element.getSimpleName().toString();
        try {
            return ReflectUtils.getConstructor(clazz, String.class, String.class, String.class,
                            boolean.class, String.class)
                    .newInstance(tableClassSimpleName + suffixName, appointPackage,
                            element.getQualifiedName().toString(), join, tableChineseName);
        } catch (Exception e) {
            return null;
        }
    }

    private String getTypeElementNoYourSelfPackage(TypeElement typeElement, boolean noProviderPackageUseDefault,
                                                   String defaultSuffixPackageName) {

        String typeElementPackage = GeneratedUtils.getTypeElementPackage(typeElement);
        String realPackage;
        if (!noProviderPackageUseDefault) {
            realPackage = typeElementPackage;
        } else {
            realPackage = GeneratedUtils.getPackageNamePrevious(typeElementPackage) + "." + defaultSuffixPackageName;
        }
        return realPackage;
    }
}
