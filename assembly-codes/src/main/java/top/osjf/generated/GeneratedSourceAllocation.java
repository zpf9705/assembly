package top.osjf.generated;

import java.util.Iterator;
import java.util.Map;

/**
 * Generate a continuation class for the annotation properties of the implementation
 * class, encapsulate the properties into the corresponding method acquisition in a
 * hierarchical manner, and in a certain advanced processing class, facilitate unified processing.
 *
 * <p>The methods are defined according to all standards, and the implementation class
 * can be selected as needed.
 *
 * @see GeneratedSource
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.1.3
 */
public interface GeneratedSourceAllocation extends Iterator<GeneratedSourceAllocation.GroupSourceEntry> {

    /**
     * Return the unified generation package path for the generated class.
     * <p>Priority is lower than {@link GroupSourceEntry#getPackageName()}.
     * @return the unified generation package path for the generated class.
     */
    String getUnifiedPackageName();

    /**
     * Return the unified {@link ClassKind} type of the generated class.
     * <p>Priority is lower than {@link GroupSourceEntry#getClassKind()}.
     * @return the unified {@link ClassKind} type of the generated class.
     */
    ClassKind getUnifiedClassKind();

    /**
     * Return the fully qualified full name of the class that is uniformly
     * inherited by the generated class.
     * <p>Priority is lower than {@link GroupSourceEntry#getExtendClassName()}.
     * @return the fully qualified full name of the class that is uniformly
     *          inherited by the generated class.
     */
    String getUnifiedExtendClassName();

    /**
     * Returns the fully qualified name of the generic collection of classes
     * that are uniformly inherited by the generated class.
     * <p>Priority is lower than {@link GroupSourceEntry#getExtendGenericsClassNames()}.
     * @return the fully qualified name of the generic collection of classes
     *          that are uniformly inherited by the generated class.
     */
    String[] getUnifiedExtendGenericsClassNames();

    /**
     * Return the fully qualified name of the collection of interfaces that
     * generate a unified implementation of the class.
     * <p>Priority is lower than {@link GroupSourceEntry#getInterfaceClassSources()}.
     * @return the fully qualified name of the collection of interfaces that
     *          generate a unified implementation of the class.
     */
    Map<String, String[]> getUnifiedInterfaceClassSources();

    /**
     * Return the fully qualified name of the annotation set that needs to be
     * identified uniformly for the generated class.
     * <p>Priority is lower than {@link GroupSourceEntry#getAnnotationSources()}.
     * @return the fully qualified name of the annotation set that needs to be
     *          identified uniformly for the generated class.
     */
    Map<String, String> getUnifiedAnnotationSources();

    /**
     * Define exclusive information interfaces for generating class groups.
     */
    interface GroupSourceEntry {

        /**
         * Return the exclusive class abbreviation for this generated class.
         * <p>If not provided, using target name concat order.
         * @return the exclusive class abbreviation for this generated class.
         */
        String getSimpleName();

        /**
         * Return the exclusive path name of this generated class.
         * <p>If not provided, using {@link #getUnifiedPackageName()}.
         * @return the exclusive path name of this generated class.
         */
        String getPackageName();

        /**
         * Return the exclusive {} type of this generated class.
         * <p>If not provided, using {@link #getUnifiedClassKind()}.
         * @return the exclusive {} type of this generated class.
         */
        ClassKind getClassKind();

        /**
         * Return the fully qualified name of the exclusive inheritance
         * class of this generated class.
         * <p>If not provided, using {@link #getUnifiedExtendClassName()}.
         * @return the fully qualified name of the exclusive inheritance
         *          class of this generated class.
         */
        String getExtendClassName();

        /**
         * Returns the fully qualified collection of generic classes that
         * are exclusive inheritors of this generated class.
         * <p>If not provided, using {@link #getUnifiedExtendGenericsClassNames()}.
         * @return the fully qualified collection of generic classes that
         *         are exclusive inheritors of this generated class.
         */
        String[] getExtendGenericsClassNames();

        /**
         * Return the fully qualified name of the collection of exclusive
         * implementation interfaces for this generated class.
         * <p>If not provided, using {@link #getUnifiedInterfaceClassSources()}.
         * @return the fully qualified name of the collection of exclusive
         *         implementation interfaces for this generated class.
         */
        Map<String, String[]> getInterfaceClassSources();

        /**
         * Return the fully qualified name of the set of exclusive flag
         * annotations for this generated class.
         * <p>If not provided, using {@link #getUnifiedAnnotationSources()}.
         * @return the fully qualified name of the set of exclusive flag
         *         annotations for this generated class.
         */
        Map<String, String> getAnnotationSources();
    }
}
