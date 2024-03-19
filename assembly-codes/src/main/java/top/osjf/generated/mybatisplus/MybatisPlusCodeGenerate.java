package top.osjf.generated.mybatisplus;

import java.lang.annotation.*;

/**
 * The mybatis plus framework automatically generates annotations for
 * mapper, service, and service implementation classes, without generating
 * specific Java files.
 *
 * <p>Instead, it directly generates compiled bytecode files.
 *
 * <p>Annotations need to be annotated on the corresponding entities in the database
 * table, and the presence of {@link com.baomidou.mybatisplus.annotation.TableName}
 * annotations will be checked.
 *
 * <p>The default value for annotation changes has been planned to be generated under
 * the specification package of the annotated class path in all cases where it is not provided.
 * Therefore, when you do not provide a specified path, you only need to create
 * {@link #DEFAULT_MAPPER_PACKAGE_SUFFIX_NAME} or {@link #DEFAULT_SERVICE_PACKAGE_SUFFIX_NAME}
 * or {@link #DEFAULT_SERVICE_IMPL_PACKAGE_SUFFIX_NAME} under the annotated class
 * package to meet the package path generation requirements for generating mappers,
 * services, and service implementation classes.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @see com.baomidou.mybatisplus.annotation.TableName
 * @see MybatisPlusCodeGenerateProcessor
 * @since 1.1.0
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
@Documented
public @interface MybatisPlusCodeGenerate {

    /** The default package suffix name when no mapper path is provided.*/
    String DEFAULT_MAPPER_PACKAGE_SUFFIX_NAME = "mapper";

    /** The default package suffix name when no service path is provided.*/
    String DEFAULT_SERVICE_PACKAGE_SUFFIX_NAME = "manager";

    /** The default package suffix name when no service impl path is provided.*/
    String DEFAULT_SERVICE_IMPL_PACKAGE_SUFFIX_NAME = "manager.impl";

    /**
     * If you do not provide the following type value and provide this property
     * value, the final generated package definition will be based on a certain
     * type plus the default package suffix mentioned above.
     *
     * <p>For example, if the mapper generation path {@link #mapperGeneratePackage()}
     * is not provided, the final generated path of the mapper interface will
     * be defined in the same way as {@code  #codeCommonPackage()} + {@link #DEFAULT_MAPPER_PACKAGE_SUFFIX_NAME}.
     *
     * <p>To achieve the above viewpoint, you need to pay attention to {@link #noProviderPackageUseDefault()}.
     *
     * <p> The priority of this attribute is lower than the specified package names
     * of the following types.
     *
     * <p>Example for {@code com.example.common}
     * @return Public generation path.
     */
    String codeCommonPackage() default "";

    /**
     * Specify the generation path of the mapper interface, with priority
     * greater than the public package path {@link #codeCommonPackage()}.
     * <p>This attribute is not provided, nor is {@link #codeCommonPackage()} provided.
     * The default path for generating the mapper is the package path
     * where the annotation identity class is located.
     * <p>Example for {@code com.example.mapper}
     * @return Generate package path for mapper interface.
     */
    String mapperGeneratePackage() default "";

    /**
     * Specify the generation path of the service interface, with priority
     * greater than the public package path {@link #codeCommonPackage()}.
     * <p>This attribute is not provided, nor is {@link #codeCommonPackage()} provided.
     * The default path for generating the service is the package path
     * where the annotation identity class is located.
     * <p>Example for {@code com.example.service}
     * @return Generate package path for service interface.
     */
    String serviceGeneratePackage() default "";

    /**
     * Specify the generation path of the service impl, with priority
     * greater than the public package path {@link #codeCommonPackage()}.
     * <p>This attribute is not provided, nor is {@link #codeCommonPackage()} provided.
     * The default path for generating the service impl is the package path
     * where the annotation identity class is located.
     * <p>Example for {@code com.example.service.impl}
     * @return Generate package path for service impl.
     */
    String serviceImplGeneratePackage() default "";

    /**
     * When you do not provide the specified path or public path mentioned above,
     * if this property is {@code true}, the generated classes such as mapper will
     * be placed in a standardized file package, such as {@link #DEFAULT_MAPPER_PACKAGE_SUFFIX_NAME}.
     * If set to {@code false}, they will be placed in the same path as the current
     * annotation flag class.
     * @return Have the generated classes been moved to the specification package
     * under the target class without providing a clear package.
     */
    boolean noProviderPackageUseDefault() default false;

    /**
     * The abbreviation suffix specification name of the interface
     * that generates the mapper, which defaults to {@code Mapper}.
     * @return The abbreviation and suffix specification name of the mapper interface.
     */
    String mapperSuffixName() default "Mapper";

    /**
     * The abbreviation suffix specification name of the interface
     * that generates the service, which defaults to {@code Manager}.
     * @return The abbreviation and suffix specification name of the service interface.
     */
    String serviceSuffixName() default "Manager";

    /**
     * The abbreviation suffix specification name of the interface
     * that generates the service impl, which defaults to {@code ManagerImpl}.
     * @return The abbreviation and suffix specification name of the service impl.
     */
    String serviceImplSuffixName() default "ManagerImpl";

    /**
     * Set whether to generate a service interface that supports linked table queries.
     * <p>The default value of this property is to generate the basic service interface
     * of the mybatis plus framework.
     * <p>If a linked table service is required, it will generate the linked table service
     * class provided by {@code com.github.yulichang.mybatis-plus-join}.
     * <p>This tool is based on mybatis plus and provides an extended service option for
     * selection.
     * @return Generate a service class that supports table concatenation.
     */
    boolean join() default false;

    /**
     * Set the Chinese descriptive name for the table.
     * @return Chinese descriptive name.
     */
    String tableChineseName() default "";
}
