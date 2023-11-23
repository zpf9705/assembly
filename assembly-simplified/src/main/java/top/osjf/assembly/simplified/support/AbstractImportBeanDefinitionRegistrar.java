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
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 */
public abstract class AbstractImportBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(@NotNull AnnotationMetadata metadata, @NotNull BeanDefinitionRegistry registry,
                                        @NotNull BeanNameGenerator importBeanNameGenerator) {
        this.registerBeanDefinitions(metadata, registry);
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata metadata, @NotNull BeanDefinitionRegistry registry) {
        Assert.notNull(getImportAnnotationClass(), "ImportAnnotationClass Not be null");
        String importAnnotationName = getImportAnnotationClass().getName();
        Map<String, Object> annotationAttributes = metadata.getAnnotationAttributes(importAnnotationName);
        Assert.notEmpty(annotationAttributes, "Not find named " + importAnnotationName + " annotation");
        AnnotationAttributes attributes = AnnotationAttributes.fromMap(annotationAttributes);
        process(attributes, registry);
    }

    /**
     * Process the annotation content analyzed by the above method
     * here, present {@link AnnotationAttributes} in the form of an
     * encapsulated map, without providing a default method, and leave
     * it to the subclass to implement the processing logic.
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
     * @return Annotation class.
     */
    @NotNull
    protected abstract Class<? extends Annotation> getImportAnnotationClass();

    /**
     * Obtain the package path where the main application is located.
     * @return Path of main application.
     */
    protected String getMainApplicationClassPath(){
        return MainClassPathCapable.getMainApplicationClassPath();
    }

    public static class MainClassPathCapable implements EnvironmentPostProcessor {

        private static String mainApplicationClassPath;

        @Override
        public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
            Class<?> mainApplicationClass = application.getMainApplicationClass();
            if (mainApplicationClass != null) {
                mainApplicationClassPath = mainApplicationClass.getPackage().getName();
            }
        }

        public static String getMainApplicationClassPath(){
            return mainApplicationClassPath;
        }
    }
}
