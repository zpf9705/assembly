package top.osjf.generated.impl;

import top.osjf.generated.*;

import java.lang.annotation.Documented;

/**
 * Compared to {@link GeneratedSource} annotations, the method of annotating
 * {@link SameAs} has the same meaning, with an additional {@link #value()}
 * attribute, which is the value attribute of the Spring framework service
 * annotation.
 *
 * <p>It is used to identify and generate the name of the service class in the
 * container. If not provided, it is still in accordance with Spring's specifications.
 * Using this annotation, you need to understand some knowledge about the Spring framework.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.1.3
 */
@Documented
public @interface SpringServiceSource {

    /**
     * The value may indicate a suggestion for a logical component name,
     * to be turned into a Spring bean in case of an autodetected component.
     * Copy from {@code org.springframework.stereotype.Service}
     *
     * @return the suggested component name, if any (or empty String otherwise)
     */
    String value() default "";

    @SameAs(GeneratedSource.class)
    String packageName() default "";

    @SameAs(GeneratedSource.class)
    String simpleName() default "";

    @SameAs(GeneratedSource.class)
    String extendClassName() default "";

    @SameAs(GeneratedSource.class)
    String[] extendGenericsClassNames() default {};

    @SameAs(GeneratedSource.class)
    ClassSource[] interfaceClassSources() default {};

    @SameAs(GeneratedSource.class)
    AnnotationSource[] annotationSources() default {};
}
