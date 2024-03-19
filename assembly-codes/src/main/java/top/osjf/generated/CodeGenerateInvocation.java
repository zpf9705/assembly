package top.osjf.generated;

import javax.annotation.processing.Filer;

/**
 * Write the source code into the supporting interface, use
 * the annotation processor environment tool {@link Filer}
 * to create Java files, and after writing the source code,
 * hand it over to JavaC for compilation and processing.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.1.0
 */
public interface CodeGenerateInvocation extends NameMetadata {

    /**
     * Create and write the analyzed generated file information.
     * @param filer The bytecode file creation entry provided by the annotation processor.
     * @param logger Customize the logging interface.
     */
    void write(Filer filer, Logger logger);
}
