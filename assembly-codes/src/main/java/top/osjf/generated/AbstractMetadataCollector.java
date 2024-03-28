package top.osjf.generated;

import javax.annotation.processing.Filer;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import java.lang.annotation.Annotation;

/**
 * The abstract {@link MetadataCollector} provides a default constructor
 * and a common property field in which the final state method is called.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @see ProcessAble#run(ProcessAble, Logger)
 * @since 1.1.3
 */
public abstract class AbstractMetadataCollector<T extends Annotation> implements MetadataCollector<T> {

    private final T annotation;

    private final RoundEnvironment roundEnvironment;

    private final Element element;

    private final Filer filer;

    private final Logger logger;

    public AbstractMetadataCollector(T annotation, RoundEnvironment roundEnvironment,
                                     Element element, Filer filer, Logger logger) {
        this.annotation = annotation;
        this.roundEnvironment = roundEnvironment;
        this.element = element;
        this.filer = filer;
        this.logger = logger;
    }

    @Override
    public T getAnnotation() {
        return annotation;
    }

    @Override
    public RoundEnvironment getRoundEnvironment() {
        return roundEnvironment;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <E extends Element> E getElement() {
        return (E) element;
    }

    @Override
    public Filer getFiler() {
        return filer;
    }

    @Override
    public Logger getLogger() {
        return logger;
    }
}
