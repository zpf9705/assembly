package top.osjf.generated;

import top.osjf.assembly.util.annotation.NotNull;

import javax.annotation.processing.Filer;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Define the method of generating the source file and the necessary properties.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.1.0
 */
public abstract class AbstractCodeGenerateInvocation extends NameMetadataImpl implements CodeGenerateInvocation {

    public AbstractCodeGenerateInvocation(String simpleName, String packageName, String targetName) {
        super(simpleName, packageName, targetName);
    }

    @Override
    public void write(Filer filer, Logger logger) {
        JavaFileObject sourceFile;
        try {
            //Create the source file using a fully qualified name first.
            sourceFile = filer.createSourceFile(getName());
        } catch (IOException e) {
            //When there is an exception, output error information to the compiled output console.
            logger.log(Diagnostic.Kind.ERROR, "CreateSourceFile failed [{}] e [{}] error [{}]", getName(),
                    e.getClass().getName(), e.getMessage());
            return;
        }
        //Write the source code and then hand it over to Javac for compilation.
        try (PrintWriter writer = new PrintWriter(sourceFile.openWriter())) {
            //Obtain the source code for splicing.
            String writeCode = getGeneratedCodeAppenderBuilder().build().toString();
            //Write to the source file.
            writer.write(writeCode);
            logger.log(SystemPrintKind.OUT, " Source File Content \n {} .", writeCode);
        } catch (Exception e) {
            logger.log(Diagnostic.Kind.ERROR, "Writer failed [{}] e [{}] error [{}]", getName(),
                    e.getClass().getName(), e.getMessage());
        }
    }

    /**
     * Get the source code content to build the class.
     * @return Source code content construction class.
     */
    @NotNull
    protected GeneratedCodeAppenderBuilder getGeneratedCodeAppenderBuilder(){
        return GeneratedCodeAppender.builder()
                .simpleName(getSimpleName())
                .packageName(getPackageName());
    }
}
