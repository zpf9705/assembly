package top.osjf.assembly.simplified.support;

import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import top.osjf.assembly.util.annotation.NotNull;

import java.lang.annotation.Annotation;

/**
 * Manually register objects into the support classes of the Spring container.
 *
 * <p>With annotation usage as the core, the general process is to query and annotate
 * an interface class for a certain annotation, construct a {@link BeanDefinition} based
 * on the information, and inject it into the spring container.
 *
 * <p>This process can be enabled using an annotation switch.
 *
 * <p>For example, when the springboot program starts, if it detects that the container bean is
 * wearing {@link top.osjf.assembly.simplified.sdk.annotation.EnableSdkProxyRegister} annotations,
 * it will automatically search for the wearing annotation {@link top.osjf.assembly.simplified.sdk.annotation.Sdk}
 * interface in this class and create an implementation class (refer to {@link AbstractJdkProxySupport}).
 *
 * <p>The implementation process depends on the bean's registrar interface {@link ImportBeanDefinitionRegistrar}.
 *
 * <p>The lookup of annotated interfaces depends on the interface {@link ResourceLoader} and
 * {@link ClassPathScanningCandidateComponentProvider}.
 *
 * <p>Innovated in version {@code 2.0.9}, this registration scheme does not participate
 * in calling services such as {@link org.springframework.beans.factory.annotation.Autowired}
 * before spring bean registration, and is executed as a registration scheme here.
 *
 * <p>For clarity of meaning, the name ‘AnnotationTypeScanningCandidateImportBeanDefinitionRegistrar’ was
 * changed to version 2.2.5.
 *
 * @author zpf
 * @since 1.1.0
 */
public abstract class AnnotationTypeScanningCandidateImportBeanDefinitionRegistrar
        extends ScanningCandidateImportBeanDefinitionRegistrar<AnnotatedBeanDefinition> {
    @NotNull
    @Override
    protected ClassPathScanningCandidateComponentProvider getScanningCandidateProvider() {
        ClassPathScanningCandidateComponentProvider componentProvider =
                new ClassPathScanningCandidateComponentProvider(false, getEnvironment()) {
                    @Override
                    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
                        AnnotationMetadata metadata = beanDefinition.getMetadata();
                        return metadata.isIndependent() && !metadata.isAnnotation();
                    }
                };
        componentProvider.setResourceLoader(getResourceLoader());
        componentProvider.setResourcePattern("**/*.class");
        componentProvider.addIncludeFilter(new AnnotationTypeFilter(getFilterAnnotationType()));
        return componentProvider;
    }

    @Override
    protected boolean isAvailableMarkedBeanDefinition(BeanDefinition markedBeanDefinition) {
        if (markedBeanDefinition instanceof AnnotatedBeanDefinition) {
            return isAvailableMarkedBeanDefinitionMetadata(((AnnotatedBeanDefinition) markedBeanDefinition)
                    .getMetadata());
        }
        return false;
    }

    @Override
    protected BeanDefinitionHolder createBeanDefinitionHolder(AnnotatedBeanDefinition markedMarkedBeanDefinition) {
        AnnotationMetadata markedAnnotationMetadata = markedMarkedBeanDefinition.getMetadata();
        AnnotationAttributes markedAnnotationAttributes = AnnotationAttributes
                .fromMap(markedAnnotationMetadata.getAnnotationAttributes(getFilterAnnotationType().getCanonicalName()));
        return createBeanDefinitionHolder(markedAnnotationAttributes, markedAnnotationMetadata);
    }

    /**
     * Return the search resource class to filter the type of annotation.
     *
     * <p>For clarity of meaning, the name ‘getFilterAnnotationType’
     * was changed to version 2.2.5.
     *
     * @return the search resource class to filter the type of annotation,
     * must not be {@literal null}.
     */
    @NotNull
    protected abstract Class<? extends Annotation> getFilterAnnotationType();

    /**
     * Return the custom valid judgment result of the associated
     * metadata {@link AnnotationMetadata} of {@link AnnotatedBeanDefinition}.
     *
     * @param metadata the {@link AnnotationMetadata} provided by the
     *                 {@link AnnotatedBeanDefinition}.
     * @return the custom valid judgment result of the associated
     * metadata {@link AnnotationMetadata} of {@link AnnotatedBeanDefinition}.
     */
    protected boolean isAvailableMarkedBeanDefinitionMetadata(AnnotationMetadata metadata) {
        return true;
    }

    /**
     * Returns holder for a BeanDefinition with name and aliases.
     * <p>Update for 2.2.5
     *
     * @param markedAnnotationAttributes Mark the attribute map of the annotation.
     * @param markedAnnotationMetadata   Annotate the metadata of the annotation class.
     * @return Holder for a BeanDefinition with name and aliases.
     */
    protected abstract BeanDefinitionHolder createBeanDefinitionHolder(AnnotationAttributes markedAnnotationAttributes,
                                                                       AnnotationMetadata markedAnnotationMetadata);
}
