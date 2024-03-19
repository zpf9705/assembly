package top.osjf.generated;

import top.osjf.assembly.util.annotation.NotNull;

import javax.lang.model.element.*;

/**
 * Definition of methods for generating type related nouns.
 * @see Class
 * @see Element
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.1.0
 */
public interface NameMetadata {

    /**
     * Returns the abbreviation of the class that needs to be generated,
     * typically triggering the annotation processor's related element definition.
     * @see Class#getSimpleName()
     * @return Returns the abbreviation of the class that needs to be generated.
     */
    @NotNull
    String getSimpleName();

    /**
     * Returns the package name of the generated definition type, usually provided
     * by the annotation that triggers the annotation processor.
     * @see Package#getName()
     * @return Returns the package name of the generated definition type.
     */
    @NotNull
    String getPackageName();

    /**
     * Generate a fully qualified name for the defined type, including the
     * package name and abbreviation.
     * Fully qualified name rules that comply with Java specifications are
     * not recommended to be rewritten.
     * @see Class#getName()
     * @return Generate a fully qualified name for the defined type.
     */
    @NotNull
    default String getName() {
        return getPackageName() + "." + getSimpleName();
    }

    /**
     * Returns the fully qualified name of the specified annotation type,
     * depending on the specific type of {@link javax.lang.model.element.Element}.
     * <ul>
     *     <li>Specific classes, including interfaces, classes, and enums,
     *     return fully qualified names {@link Class#getName()}.</li>
     *     <li>All package types return the complete package path.</li>
     *     <li>The attribute elements of variables, methods, and other classes
     *     return the defined names.</li>
     * </ul>
     *
     * @return Returns the fully qualified name with the specified annotation type.
     * @see TypeElement#getQualifiedName()
     * @see VariableElement#getSimpleName()
     * @see TypeParameterElement#getSimpleName()
     * @see ExecutableElement#getSimpleName()
     * @see PackageElement#getQualifiedName()
     */
    String getTargetName();
}
