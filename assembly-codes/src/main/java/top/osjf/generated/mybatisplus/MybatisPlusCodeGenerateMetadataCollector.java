package top.osjf.generated.mybatisplus;

import top.osjf.assembly.util.lang.ReflectUtils;
import top.osjf.assembly.util.lang.StringUtils;
import top.osjf.generated.AbstractMetadataCollector;
import top.osjf.generated.Logger;
import top.osjf.generated.SystemPrintKind;

import javax.annotation.processing.Filer;
import javax.lang.model.element.TypeElement;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.List;

/**
 * The integration logic of annotation processor {@link MybatisPlusCodeGenerate} for
 * {@link MybatisPlusCodeGenerateProcessor}.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @see MybatisPlusCodeGenerate
 * @see MybatisPlusCodeGenerateProcessor
 * @since 1.1.1
 */
class MybatisPlusCodeGenerateMetadataCollector extends AbstractMetadataCollector<MybatisPlusCodeGenerate> {

    static final String afterSourceFileName = "table-generate-setting/%s.MybatisPlusCodeGenerate.properties";

    public MybatisPlusCodeGenerateMetadataCollector(MybatisPlusCodeGenerate annotation,
                                                    TypeElement element,
                                                    Filer filer, Logger logger) {
        super(annotation, element, filer, logger);
    }


    @Override
    public void process() {

        //Obtain Metadata Collector info

        MybatisPlusCodeGenerate codeGenerate = getAnnotation();

        TypeElement element = getTypeElement();

        Filer filer = getFiler();

        Logger logger = getLogger();

        //Execute the logic for generating classes.

        String commonPackage = codeGenerate.codeCommonPackage();

        //Execute the generation of the mapper layer.

        MybatisPlusCodeGenerateInvocation mapperInvocation = getMapperNameMetadata(element, codeGenerate,
                commonPackage);

        if (mapperInvocation != null) mapperInvocation.write(filer, logger);

        //Execute the generation of the service layer.

        MybatisPlusCodeGenerateInvocation serviceInvocation = getServiceNameMetadata(element, codeGenerate,
                commonPackage);

        if (serviceInvocation != null) serviceInvocation.write(filer, logger);

        //Execute the generation of service implementation class layer.

        MybatisPlusCodeGenerateInvocation serviceImplInvocation = getServiceImplNameMetadata(element, codeGenerate,
                commonPackage);

        if (serviceImplInvocation != null) ((ServiceImplCodeGenerateInvocation) serviceImplInvocation)
                .mapper(mapperInvocation).service(serviceInvocation).write(filer, logger);

        //If the above does not mention generating corresponding generated instances,
        // there is no need to create subsequent record files.

        if (mapperInvocation == null || serviceInvocation == null || serviceImplInvocation == null) return;

        String targetName = serviceImplInvocation.getTargetName();

        //Create a file for the mybatis plus configuration project.
        //Generate record files for subsequent use by users.

        try {

            FileObject afterSourceFile = filer.createResource(StandardLocation.CLASS_OUTPUT, "",
                    String.format(afterSourceFileName, targetName));

            try (PrintWriter writer = new PrintWriter(afterSourceFile.openWriter())) {

                writeln(mapperInvocation.getWriteConfiguration(), writer);

                writeln(serviceInvocation.getWriteConfiguration(), writer);

                writeln(serviceImplInvocation.getWriteConfiguration(), writer);
            }
        } catch (Exception e) {
            logger.log(SystemPrintKind.OUT, "Error creating post file for mybatis plus:  {} {}",
                    e.getClass().getName(), e.getMessage());
        }
    }

    void writeln(List<MybatisPlusCodeGenerateInvocation.Configuration> configurations, Writer writer) throws IOException {
        for (MybatisPlusCodeGenerateInvocation.Configuration configuration : configurations) {
            writer.write(configuration.getWriteLine());
        }
    }

    MybatisPlusCodeGenerateInvocation getMapperNameMetadata(TypeElement element,
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

    MybatisPlusCodeGenerateInvocation getServiceNameMetadata(TypeElement element,
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

    MybatisPlusCodeGenerateInvocation getServiceImplNameMetadata(TypeElement element,
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

    MybatisPlusCodeGenerateInvocation getCodeGenerateInvocation(TypeElement element, String appointPackage,
                                                                String defaultSuffixPackageName, String suffixName,
                                                                String commonPackage,
                                                                boolean noProviderPackageUseDefault,
                                                                Class<? extends MybatisPlusCodeGenerateInvocation> clazz,
                                                                boolean join,
                                                                String tableChineseName) {
        if (StringUtils.isBlank(appointPackage)) {
            appointPackage = commonPackage;
            if (StringUtils.isBlank(appointPackage)) {
                appointPackage = MybatisPlusCodeGeneratedUtils.
                        getTypeElementNoYourSelfPackage(element, noProviderPackageUseDefault, defaultSuffixPackageName);
            }
        }
        String tableClassSimpleName = element.getSimpleName().toString();
        MybatisPlusCodeGenerateInvocation codeGenerateInvocation;
        try {
            codeGenerateInvocation = ReflectUtils.getConstructor(clazz,
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
        } catch (Exception ignored) {
            codeGenerateInvocation = null;
        }
        return codeGenerateInvocation;
    }
}
