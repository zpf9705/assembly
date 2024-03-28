package top.osjf.generated;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.Set;

/**
 * The public processing class of the annotation processor standardizes each
 * step of the annotation processor, provides default values for the execution
 * of each annotation processor, and passes the most direct method processing to
 * the actual processing class.
 *
 * <p>Due to a misunderstanding of the annotation processor in the previous version,
 * the current class was renamed as {@code AbstractInitializationProcessor} in
 * version {@code 1.1.3}.
 * @see ProcessorInitialization
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.1.0
 */
public abstract class AbstractInitializationProcessor extends AbstractProcessor implements ProcessorInitialization
        , Logger {

    private ProcessingEnvironment processingEnvironment;

    private Filer filer;

    private Messager messager;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.processingEnvironment = processingEnv;
        this.filer = this.processingEnvironment.getFiler();
        this.messager = this.processingEnvironment.getMessager();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        //If it has been processed, it will be returned directly.
        if (roundEnv.processingOver()) {
            return true;
        }
        for (TypeElement annotation : annotations) {
            Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(annotation);
            for (Element element : annotatedElements) {
                //The current annotation only supports type.
                if (elementFilterCondition(element)) {
                    process0(element, roundEnv);
                }
            }
        }
        return true;
    }

    /**
     * Returns the result of the execution filtering condition
     * for the annotation identification class.
     * @param element {@link Element}.
     * @return if {@literal true} acceptance processing.
     */
    public boolean elementFilterCondition(Element element) {
        return true;
    }

    /**
     * The true execution logic that annotation processors need to implement.
     * @param element The {@link Element} object of the target class.
     * @param roundEnv The annotation processor triggers the runtime wrap
     *                 around environment variable.
     */
    public abstract void process0(Element element, RoundEnvironment roundEnv);

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latest();
    }

    @Override
    public ProcessingEnvironment getProcessingEnvironment() {
        return processingEnvironment;
    }

    @Override
    public Filer getFiler() {
        return filer;
    }

    @Override
    public Messager getMessager() {
        return messager;
    }

    @Override
    public void log(SystemPrintKind kind, String message, Object... args) {
        kind.log(message, args);
    }

    @Override
    public void log(Diagnostic.Kind kind, String message, Object... args) {
        getMessager().printMessage(kind, Logger.loggerFormat(message, args));
    }
}
