package top.osjf.assembly.simplified.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.type.AnnotationMetadata;
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

        //The annotation type that triggers automatic configuration this time.
        Class<? extends Annotation> annotationType = getImportAnnotationType();
        if (annotationType == null) {
            if (log.isDebugEnabled()) {
                log.debug("You need to provide the annotation type to enable this bean assembly in " +
                        "the override method ‘getImportAnnotationType’.");
            }
            log.error("The target annotation was not obtained, and this automatic registration is invalid.");
            return;
        }

        //According to the type, obtain the attribute setting value of the annotation.
        String annotationName = annotationType.getName();
        Map<String, Object> annotationMap = metadata.getAnnotationAttributes(annotationName);
        if (CollectionUtils.isEmpty(annotationMap)) {
            if (log.isDebugEnabled()) {
                log.debug("No corresponding map structure was obtained based on the annotation type name {}.",
                        annotationName);
            }
            log.error("Failed to obtain annotation properties,, and this automatic registration is invalid.");
        }

        //Execute custom bean behavior registration.
        registerBeanDefinitions(AnnotationAttributes.fromMap(annotationMap), registry);
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
     * @param attributes Annotated content.
     * @param registry   The registration machine for the bean.
     */
    protected abstract void registerBeanDefinitions(AnnotationAttributes attributes, BeanDefinitionRegistry registry);

    /**
     * Provide {@link org.springframework.context.annotation.Import}
     * annotation to pour into the target annotation of the configuration,
     * and obtain it for analyzing the content for subsequent extension use.
     * <p>Must need Override and provider not be {@literal null}</p>
     *
     * <p>For clarity of meaning, the name ‘getImportAnnotationType’
     * was changed to version 2.2.5.
     *
     * @return Annotation type.
     */
    protected abstract Class<? extends Annotation> getImportAnnotationType();

    /**
     * Returns the package path where the main application is located.
     *
     * @return the package path where the main application is located.
     */
    protected String getMainApplicationClassPath() {
        return MainClassPathCapable.getMainApplicationClassPath();
    }

    /**
     * When initializing startup using {@link EnvironmentPostProcessor}, obtain the package path
     * where the main startup class is located.
     *
     * <p>Provide default values for scanning paths.
     */
    public static class MainClassPathCapable implements EnvironmentPostProcessor {

        private static String mainApplicationClassPath;

        @Override
        public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
            Class<?> mainApplicationClass = application.getMainApplicationClass();
            if (mainApplicationClass != null) {
                mainApplicationClassPath = mainApplicationClass.getPackage().getName();
            }
        }

        public static String getMainApplicationClassPath() {
            return mainApplicationClassPath;
        }
    }
}
