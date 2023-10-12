package top.osjf.assembly.simplified.service.annotation;

import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import top.osjf.assembly.simplified.service.context.ClassesServiceContext;
import top.osjf.assembly.simplified.service.context.SimpleServiceContext;
import top.osjf.assembly.util.annotation.NotNull;

import java.util.Objects;

/**
 * Select the processor for service context configuration.
 *
 * <p>Select {@link top.osjf.assembly.simplified.service.context.ServiceContext} injection
 * {@link ClassesServiceContext} or {@link SimpleServiceContext} based on the type of annotation {},
 * and select {@link ClassesServiceContext} by default.
 *
 * @see top.osjf.assembly.simplified.service.context.ServiceContext
 * @author zpf
 * @since 2.0.6
 */
public class ServiceContextSelector implements ImportSelector {

    @NotNull
    @Override
    public String[] selectImports(@NotNull AnnotationMetadata metadata) {
        AnnotationAttributes attributes =
                AnnotationAttributes.fromMap(metadata
                        .getAnnotationAttributes(EnableServiceCollection2.class.getName()));
        Objects.requireNonNull(attributes, EnableServiceCollection2.class.getName() + " analysis failed.");
        Type type = attributes.getEnum("type");
        String name;
        if (Type.CLASSES.equals(type)) {
            name = ClassesServiceContext.class.getName();
        } else if (Type.SIMPLE.equals(type)) {
            name = SimpleServiceContext.class.getName();
        } else {
            name = null;
        }
        return new String[]{name};
    }
}
