package top.osjf.assembly.simplified.support;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.Assert;
import top.osjf.assembly.util.annotation.NotNull;

import java.lang.annotation.Annotation;
import java.util.Map;

/**
 * Abstract {@link ImportBeanDefinitionRegistrar} implementation class,
 * parsing the triggered annotation content, and providing the package
 * path where the application main class is located.
 * <p>Pass in method {@link #process(AnnotationAttributes, BeanDefinitionRegistry)}
 * and hand it over to subclasses to implement the behavior of registering beans.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @see ImportBeanDefinitionRegistrar
 * @since 2.0.9
 */
public abstract class AbstractImportBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(@NotNull AnnotationMetadata metadata, @NotNull BeanDefinitionRegistry registry,
                                        @NotNull BeanNameGenerator importBeanNameGenerator) {
        this.registerBeanDefinitions(metadata, registry);
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata metadata, @NotNull BeanDefinitionRegistry registry) {

        //The annotation type that triggers automatic configuration this time.
        Class<? extends Annotation> annotationType = getImportAnnotationType();
        Assert.notNull(annotationType, "Imported annotation type must not be null");

        //According to the type, obtain the attribute setting value of the annotation.
        String annotationName = annotationType.getName();
        Map<String, Object> annotationMap = metadata.getAnnotationAttributes(annotationName);
        Assert.notEmpty(annotationMap, "Get annotation [" + annotationName + "] attributes is empty");
        AnnotationAttributes attributes = AnnotationAttributes.fromMap(annotationMap);

        //Execute custom bean behavior registration.
        process(attributes, registry);
    }

    /**
     * Process the annotation content analyzed by the above method
     * here, present {@link AnnotationAttributes} in the form of an
     * encapsulated map, without providing a default method, and leave
     * it to the subclass to implement the processing logic.
     *
     * @param attributes Annotated content.
     * @param registry   The registration machine for the bean.
     */
    protected void process(AnnotationAttributes attributes, BeanDefinitionRegistry registry) {
        // no op
    }

    /**
     * Provide {@link org.springframework.context.annotation.Import}
     * annotation to pour into the target annotation of the configuration,
     * and obtain it for analyzing the content for subsequent extension use.
     * <p>Must need Override and provider not be {@literal null}</p>
     *
     * @return Annotation class.
     */
    @NotNull
    protected abstract Class<? extends Annotation> getImportAnnotationType();

    /**
     * Obtain the package path where the main application is located.
     *
     * @return Path of main application.
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
