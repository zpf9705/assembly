package top.osjf.assembly.simplified.cron.annotation;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.context.annotation.Role;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import top.osjf.assembly.util.annotation.NotNull;

import java.util.Objects;

/**
 * The configuration class of the post processor for timed task registration.
 *
 * <p>Only for temporary configuration, not for user use.
 *
 * @author zpf
 * @since 2.0.6
 */
@Configuration(proxyBeanMethods = false)
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
public class CronTaskRegisterConfiguration {

    @Bean(CronTaskRegisterPostProcessor.NAME)
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public CronTaskRegisterPostProcessor cronTaskRegisterPostProcessor() {
        return new CronTaskRegisterPostProcessor(Selector.noMethodDefaultStart);
    }


    /**
     * Get {@link EnableCronTaskRegister2} info.
     */
    public static class Selector implements ImportSelector {

        static boolean noMethodDefaultStart;

        @Override
        @NotNull
        public String[] selectImports(@NotNull AnnotationMetadata metadata) {
            AnnotationAttributes attributes =
                    AnnotationAttributes.fromMap(metadata
                            .getAnnotationAttributes(EnableCronTaskRegister2.class.getName()));
            Objects.requireNonNull(attributes, EnableCronTaskRegister2.class.getName()
                    + " analysis failed.");
            noMethodDefaultStart = attributes.getBoolean("noMethodDefaultStart");
            return new String[0];
        }
    }
}
