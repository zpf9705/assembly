package top.osjf.assembly.simplified.service.annotation;

import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import top.osjf.assembly.simplified.service.context.ClassesServiceContextConfiguration;
import top.osjf.assembly.simplified.service.context.SimpleServiceContextConfiguration;
import top.osjf.assembly.util.annotation.NotNull;

import java.util.Objects;

/**
 * Select the processor for service context configuration.
 * <ul>
 *     <li>{@link ClassesServiceContextConfiguration}
 *     <pre>&#064;EnableServiceCollection2@type=SIMPLE.</pre></li>
 *     <li>{@link SimpleServiceContextConfiguration}
 *     <pre>&#064;EnableServiceCollection2@type=CLASSES.</li>
 * </ul>
 *
 * @author zpf
 * @see Type
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
            name = ClassesServiceContextConfiguration.class.getName();
        } else if (Type.SIMPLE.equals(type)) {
            name = SimpleServiceContextConfiguration.class.getName();
        } else {
            name = null;
        }
        return new String[]{name};
    }
}
