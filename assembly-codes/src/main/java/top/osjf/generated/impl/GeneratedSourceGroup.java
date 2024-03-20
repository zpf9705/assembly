package top.osjf.generated.impl;

import top.osjf.generated.AnnotationSource;
import top.osjf.generated.ClassKind;
import top.osjf.generated.ClassSource;
import top.osjf.generated.GeneratedSource;

import java.lang.annotation.*;

/**
 * This annotation is used to generate a bytecode file with the specified name,
 * type, and object-oriented properties in a specified package during compilation.
 *
 * <p>Essentially, it is compiled into a Java file and then handed over to Java C for
 * compilation into a class file, which is run on the JVM platform.
 *
 * <p>Only Java classes that do not require method rewriting can be generated.
 * Currently, method rewriting is not supported. For methods that require logic, we
 * still hope to be able to actually write Java files.
 *
 * <p>When using, we annotate the annotation on {@link RetentionPolicy#SOURCE}, add the
 * necessary annotation properties, and compile Java code to achieve automatic class generation,
 * as shown below:
 * <pre>
 * {@code
 * GeneratedSourceGroup(
 *   packageName = 'top.osjf.generated.impl',
 *   extendClassName = 'top.osjf.generated.impl.auy',
 *   group = {GeneratedSource(extendGenericsClassNames = 'top.osjf.generated.impl.ext',
 *   annotationSources = {AnnotationSource(name = 'top.osjf.generated.impl.ann', valueSet = 'value')}})
 * public interface MessageConsumer {
 *   }
 * }
 * </pre>
 * <p>If you do not fill in the {@link #group()} attribute in {@link GeneratedSource#simpleName()},
 * it defaults to the current annotation target class+'$' random string+'$Impl'.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.1.0
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
@Documented
public @interface GeneratedSourceGroup {

    /**
     * Return the package name of the generated class.
     * <p>Priority is lower than {@link #group()}.
     * <p>If not filled in, it defaults to the package path where
     * the current annotated target class is located.
     * @return the package name of the generated class.
     */
    String packageName() default "";

    /**
     * Return the {@link ClassKind} type of the generated class.
     * <p>Priority is lower than {@link #group()}.
     * @return the {@link ClassKind} type of the generated class.
     */
    ClassKind classKind() default ClassKind.CLASS;

    /**
     * Return the fully qualified name of the inherited class of the generated class.
     * <p>Priority is lower than {@link #group()}.
     * @return the fully qualified name of the inherited class of the generated class.
     */
    String extendClassName() default "";

    /**
     * Returns a fully qualified array of inherited class generic combinations
     * for the generated class.
     * <p>Priority is lower than {@link #group()}.
     * @return a fully qualified array of inherited class generic combinations
     *        for the generated class.
     */
    String[] extendGenericsClassNames() default {};

    /**
     * Return the fully qualified name of the class that implements the interface
     * required to generate the class, as well as the fully qualified name of the
     * corresponding generic class, and combine them into a {@link ClassSource} array.
     * <p>Priority is lower than {@link #group()}.
     * @return the interface required to generate the class.
     */
    ClassSource[] interfaceClassSources() default {};

    /**
     * Return the fully qualified name and corresponding attribute value of the
     * annotation required to generate the class, and merge them into a
     * {@link AnnotationSource} array.
     * <p>Priority is lower than {@link #group()}.
     * @return annotations required to generate classes.
     */
    AnnotationSource[] annotationSources() default {};

    /**
     * Detailed information of the group needs to be generated,
     * with priority greater than the global attributes mentioned above.
     * @return Generate an array of group information.
     */
    GeneratedSource[] group();
}
