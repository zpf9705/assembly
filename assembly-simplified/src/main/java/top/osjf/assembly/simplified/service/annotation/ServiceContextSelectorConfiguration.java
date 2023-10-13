package top.osjf.assembly.simplified.service.annotation;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.context.annotation.Role;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import top.osjf.assembly.simplified.service.context.*;
import top.osjf.assembly.util.annotation.NotNull;

import java.util.Objects;

/**
 * Select the processor for service context configuration.
 *
 * @see EnableServiceCollection2
 * @author zpf
 * @since 2.0.6
 */
@Configuration(proxyBeanMethods = false)
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
public class ServiceContextSelectorConfiguration {

    @Bean("TYPE-CHOOSE-CONTEXT")
    public ServiceContext serviceContext() {
        Type type = Selector.type;
        if (type == null || type.equals(Type.CLASSES)) {
            return new ClassesServiceContext();
        } else {
            return new SimpleServiceContext();
        }
    }

    /**
     * <ul>
     *     <li>{@link ClassesServiceContext}
     *     <pre>&#064;EnableServiceCollection2@type=SIMPLE.</pre></li>
     *     <li>{@link SimpleServiceContext}
     *     <pre>&#064;EnableServiceCollection2@type=CLASSES.</pre></li>
     * </ul>
     *
     * @author zpf
     * @see Type
     * @since 2.0.6
     */
    public static class Selector implements ImportSelector {

        static Type type;

        @NotNull
        @Override
        public String[] selectImports(@NotNull AnnotationMetadata metadata) {
            AnnotationAttributes attributes =
                    AnnotationAttributes.fromMap(metadata
                            .getAnnotationAttributes(EnableServiceCollection2.class.getName()));
            Objects.requireNonNull(attributes, EnableServiceCollection2.class.getName()
                    + " analysis failed.");
            type = attributes.getEnum("type");
            return new String[0];
        }
    }
}