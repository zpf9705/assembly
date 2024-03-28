package top.osjf.generated;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;

/**
 * Regarding obtaining some executable classes for {@link ProcessingEnvironment},
 * the logic provided to the annotation processor is obtained during the initialization
 * phase of the annotation processor.
 *
 * <p>Due to a misinterpretation of the definition name for this interface in
 * version 1.1.2, it has now been changed to {@code ProcessorInitialization}.
 *
 * <p>The modified name better reflects that this interface is used to obtain a
 * unified set of variables that can be initialized when triggered by the
 * annotation processor.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @see AbstractInitializationProcessor#init(ProcessingEnvironment)
 * @since 1.1.0
 */
public interface ProcessorInitialization {

    /**
     * Returns the process environment when the annotation processor triggers.
     *
     * @return the process environment when the annotation processor triggers.
     */
    ProcessingEnvironment getProcessingEnvironment();

    /**
     * Returns the file manager during compilation.
     *
     * @return the file manager during compilation.
     */
    Filer getFiler();

    /**
     * Returns the message notification date manager during compilation time.
     *
     * @return the message notification date manager during compilation time.
     */
    Messager getMessager();
}
