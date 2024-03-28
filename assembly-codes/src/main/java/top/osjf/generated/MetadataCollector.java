package top.osjf.generated;

import javax.annotation.processing.Filer;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import java.lang.annotation.Annotation;

/**
 * The build class executes metadata records and executes, including
 * executor-specific annotations to generate processor initialization
 * properties related to annotations.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @see ProcessAble
 * @see AbstractMetadataCollector
 * @since 1.1.3
 */
public interface MetadataCollector<T extends Annotation> extends ProcessAble {

    /**
     * Return the annotation content that triggers the actuator.
     * @return the annotation content that triggers the actuator.
     */
    T getAnnotation();

    /**
     * Returns the process environment when the annotation processor
     * triggers execution.
     * @return the process environment when the annotation processor
     * triggers execution.
     */
    RoundEnvironment getRoundEnvironment();

    /**
     * Returns the {@link Element} object that identifies the target class
     * of the annotation.
     * @param <E> The type of {@link Element} required.
     * @return the {@link Element} object that identifies the target class
     * of the annotation.
     * @see javax.lang.model.element.TypeElement
     * @see javax.lang.model.element.PackageElement
     * @see javax.lang.model.element.TypeParameterElement
     * @see javax.lang.model.element.ExecutableElement
     * @see javax.lang.model.element.VariableElement
     */
    <E extends Element> E getElement();

    /**
     * Returns the file manager during compilation.
     * @return the file manager during compilation.
     */
    Filer getFiler();

    /**
     * Returns a custom log output type.
     * @return a custom log output type.
     */
    Logger getLogger();

    @Override
    void process();
}
