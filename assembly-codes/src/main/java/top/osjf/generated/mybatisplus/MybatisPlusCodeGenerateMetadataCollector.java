package top.osjf.generated.mybatisplus;

import top.osjf.assembly.util.lang.ReflectUtils;
import top.osjf.assembly.util.lang.StringUtils;
import top.osjf.generated.GeneratedUtils;
import top.osjf.generated.Logger;

import javax.annotation.processing.Filer;
import javax.lang.model.element.TypeElement;

/**
 * @see MybatisPlusCodeGenerateProcessor
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.1.1
 */
class MybatisPlusCodeGenerateMetadataCollector {

    private final MybatisPlusCodeGenerate codeGenerate;

    private final TypeElement element;

    private final Filer filer;

    private final Logger logger;

    MybatisPlusCodeGenerateMetadataCollector(MybatisPlusCodeGenerate codeGenerate,
                                                    TypeElement element,
                                                    Filer filer,
                                                    Logger logger) {
        this.codeGenerate = codeGenerate;
        this.element = element;
        this.filer = filer;
        this.logger = logger;
    }

    public void process() {

        String commonPackage = codeGenerate.codeCommonPackage();

        MybatisPlusCodeGenerateInvocation mapperInvocation = getMapperNameMetadata(element, codeGenerate,
                commonPackage);

        if (mapperInvocation != null) mapperInvocation.write(filer, logger);

        MybatisPlusCodeGenerateInvocation serviceInvocation = getServiceNameMetadata(element, codeGenerate,
                commonPackage);

        if (serviceInvocation != null) serviceInvocation.write(filer, logger);

        MybatisPlusCodeGenerateInvocation serviceImplInvocation = getServiceImplNameMetadata(element, codeGenerate,
                commonPackage);

        if (serviceImplInvocation != null) ((ServiceImplCodeGenerateInvocation) serviceImplInvocation)
                .mapper(mapperInvocation).service(serviceInvocation).write(filer, logger);
    }

    private MybatisPlusCodeGenerateInvocation getMapperNameMetadata(TypeElement element,
                                                                    MybatisPlusCodeGenerate codeGenerate,
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

    private MybatisPlusCodeGenerateInvocation getServiceNameMetadata(TypeElement element,
                                                                     MybatisPlusCodeGenerate codeGenerate,
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

    private MybatisPlusCodeGenerateInvocation getServiceImplNameMetadata(TypeElement element,
                                                                         MybatisPlusCodeGenerate codeGenerate,
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

    private MybatisPlusCodeGenerateInvocation getCodeGenerateInvocation(TypeElement element, String appointPackage,
                                                                        String defaultSuffixPackageName, String suffixName,
                                                                        String commonPackage,
                                                                        boolean noProviderPackageUseDefault,
                                                                        Class<? extends MybatisPlusCodeGenerateInvocation> clazz,
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
            return ReflectUtils.getConstructor(clazz,
                            String.class,
                            String.class,
                            String.class,
                            boolean.class,
                            String.class,
                            String.class)
                    .newInstance(tableClassSimpleName + suffixName,
                            appointPackage,
                            element.getQualifiedName().toString(),
                            join,
                            tableChineseName,
                            suffixName);
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
