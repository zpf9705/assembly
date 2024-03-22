package top.osjf.generated.mybatisplus;

import com.baomidou.mybatisplus.annotation.TableName;
import top.osjf.assembly.util.logger.Console;
import top.osjf.generated.GeneratedUtils;

import javax.lang.model.element.TypeElement;
import java.beans.Introspector;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A tool for generating classes related to mybatis plus.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.1.1
 */
public final class MybatisPlusCodeGeneratedUtils {

    private MybatisPlusCodeGeneratedUtils() {
    }

    /**
     * The package separator character: {@code '.'}.
     */
    private static final char PACKAGE_SEPARATOR = '.';


    /**
     * The nested class separator character: {@code '$'}.
     */
    private static final char NESTED_CLASS_SEPARATOR = '$';

    /**
     * The CGLIB class separator: {@code "$$"}.
     */
    public static final String CGLIB_CLASS_SEPARATOR = "$$";

    /**
     * Fill in the relevant configuration cache file for the attribute of {@link MybatisPlusCodeGenerate}.
     */
    private final static Map<Class<?>, Properties> propertiesCache = new ConcurrentHashMap<>();

    /**
     * The calling of this method depends on whether to use the parsing method of
     * the target class's package or the default type package without providing the
     * package where the generated class is located.
     * @param typeElement                 The target class encapsulates the interface.
     * @param noProviderPackageUseDefault When no package is provided, decide whether to
     *                                    include it in the target class's own package
     *                                    {@literal false} or the default package {@literal true}.
     * @param defaultSuffixPackageName    The default package suffix name provided when the parameter
     *                                    is {@code noProviderPackageUseDefault}.
     * @return The package name of the generated class after parsing according to the rules.
     */
    public static String getTypeElementNoYourSelfPackage(TypeElement typeElement,
                                                         boolean noProviderPackageUseDefault,
                                                         String defaultSuffixPackageName) {
        String typeElementPackage = GeneratedUtils.getTypeElementPackage(typeElement);
        String realPackage;
        if (!noProviderPackageUseDefault) {
            realPackage = typeElementPackage;
        } else {
            realPackage = GeneratedUtils.getPackageNamePrevious(typeElementPackage) + PACKAGE_SEPARATOR
                    + defaultSuffixPackageName;
        }
        return realPackage;
    }

    /**
     * When using this tool to generate the relevant service classes for
     * mybatis plus and inject them into the Spring container as beans,
     * this method is used to obtain the corresponding service bean name
     * through the class object of the data entity.
     * @param entityClass The class object of the data entity.
     * @return The name in the spring container.
     */
    public static String getEntityServiceBeanName(Class<?> entityClass) {
        if (entityClass == null || entityClass.getAnnotation(TableName.class) == null) {
            return null;
        }
        init(entityClass);
        Properties properties = propertiesCache.get(entityClass);
        if (properties == null) return null;
        //get shortName
        String className = entityClass.getTypeName();
        int lastDotIndex = className.lastIndexOf(PACKAGE_SEPARATOR);
        int nameEndIndex = className.indexOf(CGLIB_CLASS_SEPARATOR);
        if (nameEndIndex == -1) {
            nameEndIndex = className.length();
        }
        String shortName = className.substring(lastDotIndex + 1, nameEndIndex);
        shortName = shortName.replace(NESTED_CLASS_SEPARATOR, PACKAGE_SEPARATOR);
        shortName = Introspector.decapitalize(shortName);
        //get name
        return shortName + properties.getProperty(ServiceImplCodeGenerateInvocationImpl.SERVICE_IMPL_WRITE_PREFIX);
    }

    /**
     * Initialize the configuration file for the generated class.
     * @param entityClass The class object of the data table.
     */
     static void init(Class<?> entityClass) {
        Properties properties = propertiesCache.get(entityClass);
        if (properties == null) {
            synchronized (propertiesCache) {
                properties = propertiesCache.get(entityClass);
                if (properties == null) {
                    ClassLoader classLoader = entityClass.getClassLoader();
                    if (classLoader == null) {
                        classLoader = ClassLoader.getSystemClassLoader();
                    }
                    String name =
                            String.format(MybatisPlusCodeGenerateMetadataCollector.afterSourceFileName,
                                    entityClass.getName());
                    InputStream stream = classLoader.getResourceAsStream(name);
                    if (stream == null) {
                        return;
                    }
                    properties = new Properties();
                    try {
                        properties.load(stream);
                        propertiesCache.putIfAbsent(entityClass, properties);
                    } catch (IOException e) {
                        Console.error("Properties Load {} error {}", name, e.getMessage());
                    }
                }
            }
        }
    }
}
