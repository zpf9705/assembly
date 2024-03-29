package top.osjf.generated.impl;

import top.osjf.generated.AnnotationSource;
import top.osjf.generated.ClassSource;
import top.osjf.generated.SameAs;

import java.lang.annotation.*;

/**
 * For the generation of implementation classes that do not require rewriting
 * methods to implement the target interface, the Spring framework's service class
 * annotation {@code org.springframework.stereotype.Service} will be added by default,
 * which is a derivative version of the annotation {@link GeneratedSourceGroup}.
 *
 * <p>The attributes of the annotation {@link SameAs} are consistent with the parsing
 * meaning of {@link GeneratedSourceGroup}.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @see GeneratedSourceGroup
 * @since 1.1.3
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
@Documented
public @interface SpringServiceGroup {

    @SameAs(GeneratedSourceGroup.class)
    String packageName() default "";

    @SameAs(GeneratedSourceGroup.class)
    String extendClassName() default "";

    @SameAs(GeneratedSourceGroup.class)
    String[] extendGenericsClassNames() default {};

    @SameAs(GeneratedSourceGroup.class)
    ClassSource[] interfaceClassSources() default {};

    @SameAs(GeneratedSourceGroup.class)
    AnnotationSource[] annotationSources() default {};

    @SameAs(GeneratedSourceGroup.class)
    SpringServiceSource[] group();
}
