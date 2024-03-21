package top.osjf.generated.mybatisplus;

import top.osjf.assembly.util.annotation.NotNull;
import top.osjf.assembly.util.lang.StringUtils;
import top.osjf.generated.AbstractCodeGenerateInvocation;
import top.osjf.generated.GeneratedCodeAppenderBuilder;
import top.osjf.generated.Logger;
import top.osjf.generated.SystemPrintKind;

import javax.annotation.processing.Filer;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.PrintWriter;

/**
 * Abstract implementation for special attributes of the mybatis plus framework.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @see MybatisPlusCodeGenerate
 * @since 1.1.0
 */
public abstract class AbstractMybatisPlusCodeGenerateInvocation extends AbstractCodeGenerateInvocation
        implements MybatisPlusCodeGenerateInvocation {

    private final boolean join;

    private final String tableChineseName;

    private final String suffixName;

    //since 1.1.1
    private static boolean create;

    //since 1.1.1
    private static PrintWriter printWriter;

    //since 1.1.1
    private static int implNum = 3;

    //since 1.1.1
    public static final String afterSourceFileName = "mybatisPlusCodeGenerate.properties";

    public AbstractMybatisPlusCodeGenerateInvocation(String simpleName, String packageName, String targetName,
                                                     boolean join, String tableChineseName, String suffixName) {
        super(simpleName, packageName, targetName);
        this.join = join;
        this.tableChineseName = tableChineseName;
        this.suffixName = suffixName;
    }

    @Override
    public boolean getJoin() {
        return join;
    }

    @Override
    public String getTableChineseName() {
        return tableChineseName;
    }

    @Override
    public String getSuffixName() {
        return suffixName;
    }

    @NotNull
    @Override
    protected GeneratedCodeAppenderBuilder getGeneratedCodeAppenderBuilder() {
        GeneratedCodeAppenderBuilder builder = super.getGeneratedCodeAppenderBuilder();
        //When the provided table Chinese name is not empty, concatenate and annotate the
        // description based on the table name with special expressions.
        if (StringUtils.isNotBlank(getTableChineseName())) {
            builder.description(getTableChineseName() + getCharacterization());
        }
        return builder;
    }

    //since 1.1.1
    @Override
    public void write(Filer filer, Logger logger) {
        //Write the necessary Java files first.
        super.write(filer, logger);
        //Create a file for the mybatis plus configuration project.
        FileObject fileObject =
                MybatisPlusCodeGeneratedUtils.getFileObject(filer, logger, StandardLocation.CLASS_OUTPUT, afterSourceFileName);
        if (fileObject != null) {
            try (PrintWriter writer = new PrintWriter(fileObject.openWriter())) {
                writer.write(getTargetName() + "." + getWritePrefix() + "=" + getSuffixName() + "\n");
            } catch (Exception e) {
                logger.log(SystemPrintKind.OUT, "Error writing content to afterSource File file::  {} {}",
                        e.getClass().getName(), e.getMessage());
            }
        }
//        if (!create) {
//            try {
//                FileObject afterSourceFile = filer.createResource(StandardLocation.CLASS_OUTPUT, "", afterSourceFileName);
//                printWriter = new PrintWriter(afterSourceFile.openWriter());
//                create = true;
//            } catch (Exception e) {
//                logger.log(SystemPrintKind.OUT, "Error creating post file for mybatis plus:  {} {}",
//                        e.getClass().getName(), e.getMessage());
//            }
//        }
//        //Global write of files.
//        if (printWriter != null) {
//            try {
//                printWriter.write(getWritePrefix() + "=" + getSuffixName() + "\n");
//                implNum--;
//            } catch (Exception e) {
//                logger.log(SystemPrintKind.OUT, "Error writing content to afterSource File file::  {} {}",
//                        e.getClass().getName(), e.getMessage());
//            }
//            if (implNum == 0) {
//                printWriter.close();
//                printWriter = null;
//                logger.log(SystemPrintKind.OUT, "The file {} write stream has been closed.", afterSourceFileName);
//            }
//        }
    }

    /**
     * Write the configuration item prefix to the temporary configuration file.
     *
     * @return the configuration item prefix to the temporary configuration file
     * @since 1.1.1
     */
    protected abstract String getWritePrefix();

    /**
     * Returns a special description of the generated class.
     *
     * @return a special description of the generated class.
     */
    protected abstract String getCharacterization();
}
