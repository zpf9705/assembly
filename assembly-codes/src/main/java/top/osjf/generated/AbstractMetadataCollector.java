package top.osjf.generated;

import javax.annotation.processing.Filer;
import javax.lang.model.element.TypeElement;
import java.lang.annotation.Annotation;

/**
 * The abstract {@link MetadataCollector} provides a default constructor
 * and a common property field in which the final state method is called.
 * @see ProcessAble#run(ProcessAble, Logger)
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.1.3
 */
public abstract class AbstractMetadataCollector<T extends Annotation> implements MetadataCollector<T> {

    private final T annotation;

    private final TypeElement element;

    private final Filer filer;

    private final Logger logger;

    public AbstractMetadataCollector(T annotation, TypeElement element, Filer filer, Logger logger) {
        this.annotation = annotation;
        this.element = element;
        this.filer = filer;
        this.logger = logger;
        ProcessAble.run(this, logger);
    }

    @Override
    public T getAnnotation() {
        return annotation;
    }

    @Override
    public TypeElement getTypeElement() {
        return element;
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
