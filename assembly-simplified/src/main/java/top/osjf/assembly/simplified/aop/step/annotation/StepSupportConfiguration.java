package top.osjf.assembly.simplified.aop.step.annotation;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportAware;
import org.springframework.context.annotation.Role;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import top.osjf.assembly.simplified.aop.step.AspectJStepSupport;
import top.osjf.assembly.simplified.aop.step.StepSignature;
import top.osjf.assembly.util.annotation.NotNull;

import java.util.Objects;

/**
 * Enable loading configuration for {@link EnableStepSupport} on
 * annotation classes.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 2.2.5
 */
@Configuration(proxyBeanMethods = false)
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
public class StepSupportConfiguration implements ImportAware {

    private StepSignature signature;

    @Override
    public void setImportMetadata(@NotNull AnnotationMetadata metadata) {
        AnnotationAttributes attributes =
                AnnotationAttributes.fromMap(metadata
                        .getAnnotationAttributes(EnableStepSupport.class.getName()));
        Objects.requireNonNull(attributes, EnableStepSupport.class.getName()
                + " analysis failed.");
        signature = attributes.getEnum("value");
    }

    @Bean
    public AspectJStepSupport aspectJStepSupport(){
        return new AspectJStepSupport(signature);
    }
}
