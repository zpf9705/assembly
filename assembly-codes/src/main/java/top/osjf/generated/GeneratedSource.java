package top.osjf.generated;

import java.lang.annotation.Documented;

/**
 * It has no practical effect and only serves as a bearer annotation
 * for generating class metadata.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.1.0
 */
@Documented
public @interface GeneratedSource {

    /**
     * Return the package name of the generated class.
     * @return the package name of the generated class.
     */
    String packageName() default "";

    /**
     * Return the abbreviation of the generated class.
     * <p>If left blank, this annotation does not take any action.
     * Other annotations that use this annotation can provide default values,
     * such as {@link top.osjf.generated.impl.GeneratedSourceGroup} defaulting
     * to the current annotation target class+'$' random string+'$Impl'.
     * @return the abbreviation of the generated class.
     */
    String simpleName() default "";

    /**
     * Return the {@link ClassKind} type of the generated class.
     * @return the {@link ClassKind} type of the generated class.
     */
    ClassKind classKind() default ClassKind.CLASS;

    /**
     * Return the fully qualified name of the inherited class of the generated class.
     * @return the fully qualified name of the inherited class of the generated class.
     */
    String extendClassName() default "";

    /**
     * Returns a fully qualified array of inherited class generic combinations
     * for the generated class.
     * @return a fully qualified array of inherited class generic combinations
     *        for the generated class.
     */
    String[] extendGenericsClassNames() default {};

    /**
     * Return the fully qualified name of the class that implements the interface
     * required to generate the class, as well as the fully qualified name of the
     * corresponding generic class, and combine them into a {@link ClassSource} array.
     * @return the interface required to generate the class.
     */
    ClassSource[] interfaceClassSources() default {};

    /**
     * Return the fully qualified name and corresponding attribute value of the
     * annotation required to generate the class, and merge them into a
     * {@link AnnotationSource} array.
     * @return annotations required to generate classes.
     */
    AnnotationSource[] annotationSources() default {};
}
