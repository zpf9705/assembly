package top.osjf.generated;

import java.util.Map;

/**
 * The source code concatenation is written into the content collection
 * interface, which includes some meta properties of the class.
 * <p>Currently, it supports inheritance, implementation, annotation flags,
 * annotation, and generic addition, as well as automatic import of corresponding
 * class packages.
 * <p>It should be noted that method rewriting and method writing are not supported.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.1.0
 * @see GeneratedCodeAppender
 */
public interface CodeAppender {

    /**
     * Return the description information of the generated class,
     * which is the annotation information.
     * @return the description information of the generated class.
     */
    String getDescription();

    /**
     * Returns the type keyword for the generated class.
     * @see ClassKind
     * @return the type keyword for the generated class.
     */
    ClassKind getClassKind();

    /**
     * Return the package path where the generated class needs to be located.
     * @see Package#getName()
     * @return the package path where the generated class needs to be located.
     */
    String getPackageName();

    /**
     * Return the abbreviation of the generated class.
     * @see Class#getSimpleName()
     * @return the abbreviation of the generated class.
     */
    String getSimpleName();

    /**
     * Return the fully qualified name of the object that needs to
     * be inherited to generate the class.
     * @see Class#getName()
     * @return the fully qualified name of the object that needs to
     *          be inherited to generate the class.
     */
    String getExtendClassName();

    /**
     * Returns the collection of fully qualified generic names that need
     * to be inherited by the generated class.
     * @return the collection of fully qualified generic names that need
     *         to be inherited by the generated class
     */
    String[] getExtendGenericsClassNames();

    /**
     * Return the fully qualified name of the interface that needs to be
     * implemented to generate the class, as well as the array of fully
     * qualified names corresponding to each interface that needs to be generic,
     * displayed in the form of a {@link Map}.
     * @return Interface and its generic combination.
     */
    Map<String, String[]> getInterfaceWithGenericsNames();

    /**
     * Return the annotation that needs to be identified, as well as the value
     * that needs to be set when the corresponding annotation has a
     * {@link AnnotationSource#SUPPORT_KEY} named attribute value.
     * <p>The current annotation only supports {@link AnnotationSource#SUPPORT_KEY} names.
     * @return Annotations and their possible value attribute values.
     */
    Map<String, String> getAnnotationWithValueNames();
}
