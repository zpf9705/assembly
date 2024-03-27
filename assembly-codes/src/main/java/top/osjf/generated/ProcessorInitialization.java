package top.osjf.generated;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;

/**
 * Regarding obtaining some executable classes for {@link ProcessingEnvironment},
 * the logic provided to the annotation processor is obtained during the initialization
 * phase of the annotation processor.
 * <p>Rename ProcessorInitialization form 1.1.3
 * @see AbstractInitializationProcessor#init(ProcessingEnvironment)
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.1.0
 */
public interface ProcessorInitialization {

    ProcessingEnvironment getProcessingEnvironment();

    Filer getFiler();

    Messager getMessager();
}
