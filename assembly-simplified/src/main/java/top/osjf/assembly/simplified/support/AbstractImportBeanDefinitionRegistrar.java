package top.osjf.assembly.simplified.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.lang.Nullable;
import top.osjf.assembly.util.annotation.NotNull;
import top.osjf.assembly.util.lang.CollectionUtils;

import java.lang.annotation.Annotation;
import java.util.Map;

/**
 * Abstract {@link ImportBeanDefinitionRegistrar} implementation class,
 * parsing the triggered annotation content, and providing the package
 * path where the application main class is located.
 *
 * <p>Pass in method {@link #registerBeanDefinitions(AnnotationAttributes, BeanDefinitionRegistry)}
 * and hand it over to subclasses to implement the behavior of registering beans.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 2.0.9
 */
public abstract class AbstractImportBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar {

    /*** 2.2.5 add Log output, providing its own subclass usage.*/
    protected final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public void registerBeanDefinitions(@NotNull AnnotationMetadata metadata, @NotNull BeanDefinitionRegistry registry,
                                        @NotNull BeanNameGenerator beanNameGenerator) {
        this.registerBeanDefinitions(metadata, registry);
    }

    @Override
    public void registerBeanDefinitions(@NotNull AnnotationMetadata metadata, @NotNull BeanDefinitionRegistry registry) {
        AnnotationAttributes importAnnotationAttributes;
        Class<? extends Annotation> importAnnotationType = getImportAnnotationType();
        if (importAnnotationType != null) {
            String annotationName = importAnnotationType.getName();
            Map<String, Object> annotationMap = metadata.getAnnotationAttributes(annotationName);
            if (CollectionUtils.isNotEmpty(annotationMap)) {
                importAnnotationAttributes = AnnotationAttributes.fromMap(annotationMap);
            } else {
                importAnnotationAttributes = null;
                if (log.isDebugEnabled()) {
                    log.debug("No corresponding map structure was obtained based on the annotation type name {}.",
                            annotationName);
                }
            }
        } else {
            importAnnotationAttributes = null;
            if (log.isDebugEnabled()) {
                log.debug("There is no type provided that can trigger this configuration annotation.");
            }
        }
        registerBeanDefinitions(importAnnotationAttributes, registry);
    }

    /**
     * Process the annotation content analyzed by the above method
     * here, present {@link AnnotationAttributes} in the form of an
     * encapsulated map, without providing a default method, and leave
     * it to the subclass to implement the processing logic.
     *
     * <p>For clarity of meaning, the name ‘registerBeanDefinitions’
     * was changed to version 2.2.5.
     *
     * @param importAnnotationAttributes Pour {@link AnnotationAttributes} into the
     *                                   configuration annotation.
     * @param registry                   The registration machine for the bean.
     */
    protected abstract void registerBeanDefinitions(@Nullable AnnotationAttributes importAnnotationAttributes,
                                                    @NotNull BeanDefinitionRegistry registry);

    /**
     * Provide {@link org.springframework.context.annotation.Import}
     * annotation to pour into the target annotation of the configuration,
     * and obtain it for analyzing the content for subsequent extension use.
     * <p>Must need Override and provider not be {@literal null}</p>
     *
     * <p>For clarity of meaning, the name ‘getImportAnnotationType’
     * was changed to version 2.2.5.
     *
     * @return Annotation type,must not be {@literal null}.
     */
    @Nullable
    protected abstract Class<? extends Annotation> getImportAnnotationType();
}
