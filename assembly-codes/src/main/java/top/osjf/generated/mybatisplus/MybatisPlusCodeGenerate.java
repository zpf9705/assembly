package top.osjf.generated.mybatisplus;

import java.lang.annotation.*;

/**
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @see com.baomidou.mybatisplus.annotation.TableName
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
     * <p> The priority of this attribute is lower than the specified package names
     * of the following types.
     * @return Public generation path.
     */
    String codeCommonPackage() default "";

    /**
     * Specify the generation path of the mapper interface, with priority
     * greater than the public package path {@link #codeCommonPackage()}.
     * <p>This attribute is not provided, nor is {@link #codeCommonPackage()} provided.
     * The default path for generating the mapper is the package path
     * where the annotation identity class is located.
     * @return Generate package path for mapper interface.
     */
    String mapperGeneratePackage() default "";

    /**
     * Specify the generation path of the service interface, with priority
     * greater than the public package path {@link #codeCommonPackage()}.
     * <p>This attribute is not provided, nor is {@link #codeCommonPackage()} provided.
     * The default path for generating the service is the package path
     * where the annotation identity class is located.
     * @return Generate package path for service interface.
     */
    String serviceGeneratePackage() default "";

    /**
     * Specify the generation path of the service impl, with priority
     * greater than the public package path {@link #codeCommonPackage()}.
     * <p>This attribute is not provided, nor is {@link #codeCommonPackage()} provided.
     * The default path for generating the service impl is the package path
     * where the annotation identity class is located.
     * @return Generate package path for service impl.
     */
    String serviceImplGeneratePackage() default "";

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
}
