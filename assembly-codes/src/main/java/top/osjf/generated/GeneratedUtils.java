package top.osjf.generated;

import cn.hutool.core.util.RandomUtil;
import top.osjf.assembly.util.annotation.NotNull;
import top.osjf.assembly.util.data.Triple;
import top.osjf.assembly.util.lang.ArrayUtils;
import top.osjf.assembly.util.lang.Asserts;
import top.osjf.assembly.util.lang.MapUtils;
import top.osjf.assembly.util.lang.StringUtils;

import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * The supporting method tool class for generating related classes.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.1.0
 */
public final class GeneratedUtils {

    /** Regular validation of Java canonical class names.*/
    private static final String NAME_MATCHER_EXPRESS = "^[a-zA-Z_][a-zA-Z0-9_]*(\\.[a-zA-Z_][a-zA-Z0-9_]*)*$";

    /** Keywords for Java class import.*/
    private static final String JAVA_IMPORT_WORD = "import";

    /** Keyword for identifying Java class packages.*/
    private static final String JAVA_PACKAGE_WORD = "package";

    /** Java syntax statement completion character.*/
    private static final String JAVA_SEMICOLON_WORD = ";";

    /** Java interval symbol.*/
    private static final String JAVA_COMMA_WORD = ",";

    /** The default permission for creating Java classes.*/
    private static final String JAVA_CLASS_DEFAULT_PERM = "public";

    /** Space symbol, for elegant code.*/
    private static final String JAVA_SYNTAX_BLANK = " ";

    /** Java methods, class body left curly braces.*/
    private static final String JAVA_LEFT_BRACKET = "{";

    /** Java methods, class body right curly braces.*/
    private static final String JAVA_RIGHT_BRACKET = "}";

    /** Inheritance keywords for Java classes.*/
    private static final String JAVA_EXTEND_KEYWORDS = "extends";

    /** Implementation keywords for Java classes.*/
    private static final String JAVA_IMPLEMENT_KEYWORDS = "implements";

    /** Line breaks, for the sake of elegant code.*/
    private static final String LINE_BREAK = "\n";

    /** Java generic wrap left arrow symbol.*/
    private static final String JAVA_GENERICS_LEFT_ANGLE_BRACKET = "<";

    /** Java generic wrap right arrow symbol.*/
    private static final String JAVA_GENERICS_RIGHT_ANGLE_BRACKET = ">";

    /** Java annotation values are set to elegant equal signs.*/
    private static final String EQUAL_COMMA = " = ";

    /** Java annotation prefix @ symbol.*/
    private static final String ANNOTATION_PREFIX = "@";

    /** The Java annotation settings include a left square bracket.*/
    private static final String LEFT_BRACKET = "(";

    /** The Java annotation settings include a right square bracket.*/
    private static final String RIGHT_BRACKET = ")";

    /** Double quotation marks, string settings introduced.*/
    private static final String QUO_MARK = "\"";

    /** Annotate concatenated string models.*/
    private static final String NOTE_FORMAT =
            "/**" + LINE_BREAK + "* %s" + LINE_BREAK + "* @author <a href=\"mailto:%s\">%s</a>" + LINE_BREAK
                    + "* @since %s" + LINE_BREAK + "*/";

    /** The startup version number of the current project.*/
    private static String version;

    /** The startup id of the current project.*/
    private static String id;

    /** Author email of the current project.*/
    private static String email;

    /** Generate the default description of the class.*/
    private static String defaultDescribe;

    /*
     * Load the project version number.
     */
    static {
        try (InputStream stream = GeneratedUtils.class.getClassLoader().getResourceAsStream
                ("base.properties")) {
            Properties properties = new Properties();
            properties.load(stream);
            version = properties.getProperty("project.version");
            id = properties.getProperty("project.id");
            email = properties.getProperty("project.author.email");
            defaultDescribe = properties.getProperty("project.default.describe");
        } catch (IOException e) {
            version = "unKnown";
        }
    }

    /** Change the tool class to only make static calls without instantiation.*/
    private GeneratedUtils() {
    }

    /**
     * Write Java file source code according to the relevant properties
     * provided by interface {@link CodeAppender}, which complies with
     * Java syntax specifications.
     * @param appender Splicing attribute content in source code.
     * @return Standardized Java source code to be written.
     * @see #setPackage(String, StringBuilder)
     * @see #setGeneratedNote(StringBuilder, String)
     * @see #addAnnotation(String, String, StringBuilder)
     * @see #addGenerics(String[], StringBuilder, StringBuilder)
     * @see #addClassPurpose(ClassKind, String, StringBuilder)
     */
    public static String codeAppenderToString(CodeAppender appender) {
        if (appender == null) return "";

        //Overall splicing.
        StringBuilder builder = new StringBuilder();

        //Import package information concatenation.
        StringBuilder importAppend = new StringBuilder();

        //Set package information
        setPackage(appender.getPackageName(), importAppend);

        //Splicing of class header related information.
        StringBuilder classAppend = new StringBuilder();

        //Set comments.
        setGeneratedNote(classAppend, appender.getDescription());

        //First, handle the concatenation of annotations.
        Map<String, String> annotationWithValueNames = appender.getAnnotationWithValueNames();
        if (MapUtils.isNotEmpty(annotationWithValueNames)) {
            for (String annotationName : annotationWithValueNames.keySet()) {
                Triple<String, String, String> annotationNameMetadata
                        = GeneratedUtils.getNameMetadataByProvider(annotationName);
                addAnnotation(annotationNameMetadata.getV1(),
                        annotationWithValueNames.get(annotationName),
                        classAppend);
                addImport(annotationNameMetadata.getV3(), importAppend);
            }
        }

        //Add relevant types of the class.
        addClassPurpose(appender.getClassKind(), appender.getSimpleName(), classAppend);

        //Secondly, handle inheritance class settings.
        String extendClassName = appender.getExtendClassName();
        if (StringUtils.isNotBlank(extendClassName) && !appender.getClassKind().isEnum()) {
            Triple<String, String, String> extendClassNameMetadata = getNameMetadataByProvider(extendClassName);
            classAppend.append(JAVA_EXTEND_KEYWORDS)
                    .append(JAVA_SYNTAX_BLANK)
                    .append(extendClassNameMetadata.getV1());
            addImport(extendClassNameMetadata.getV3(), importAppend);
            String[] extendGenericsClassNames = appender.getExtendGenericsClassNames();
            addGenerics(extendGenericsClassNames, importAppend, classAppend);
        }

        //Thirdly, handle interface related settings.
        Map<String, String[]> interfaceWithGenericsNames = appender.getInterfaceWithGenericsNames();
        if (MapUtils.isNotEmpty(interfaceWithGenericsNames)) {
            classAppend.append(JAVA_SYNTAX_BLANK)
                    .append(JAVA_IMPLEMENT_KEYWORDS)
                    .append(JAVA_SYNTAX_BLANK);
            for (String interfaceName : interfaceWithGenericsNames.keySet()) {
                Triple<String, String, String> interfaceNameMetadata = getNameMetadataByProvider(interfaceName);
                classAppend.append(interfaceNameMetadata.getV1());
                addImport(interfaceNameMetadata.getV3(), importAppend);
                String[] interfaceGenericNames = interfaceWithGenericsNames.get(interfaceName);
                addGenerics(interfaceGenericNames, importAppend, classAppend);
            }
        }

        //Processing completed, concatenate import and class.
        classAppend.append(JAVA_SYNTAX_BLANK)
                .append(JAVA_LEFT_BRACKET)
                .append(JAVA_SYNTAX_BLANK)
                .append(JAVA_RIGHT_BRACKET);
        builder.append(importAppend)
                .append(LINE_BREAK)
                .append(classAppend);

        return builder.toString();
    }



    /**
     * Use {@link TypeElement} to obtain its fully qualified name.
     * @param typeElement The {@link javax.lang.model.element.Element} type of the class.
     * @return fully qualified name.
     */
    public static String getTypeElementPackage(TypeElement typeElement) {
        if (typeElement == null) {
            return null;
        }
        return getPackageNamePrevious(typeElement.getQualifiedName().toString());
    }

    /**
     * Get the path before the last {@code "."} digit of the current package name.
     * @param packageName known packageName.
     * @return after substring {@code "."} packageName.
     */
    public static String getPackageNamePrevious(String packageName) {
        if (StringUtils.isBlank(packageName)) {
            return null;
        }
        return packageName.substring(0, packageName.lastIndexOf("."));
    }

    /**
     * Choose based on the input package name and abbreviation.
     * <p>If it is not empty, return it directly.
     * <p>If it is empty, the abbreviation is based on the abbreviation
     * of the target class+"$"+random string+"$Impl", and the package name is
     * directly set to the package where the target class is located.
     * @param simpleName            provider simpleName.
     * @param packageName           provider packageName.
     * @param sourceQualifiedName   target name.
     * @param sourceSimpleName      target simpleName.
     * @return The abbreviation, package name, and full name obtained from the analysis
     */
    public static Triple<String, String, String> getNames(String simpleName,
                                                          String packageName,
                                                          @NotNull Name sourceQualifiedName,
                                                          @NotNull Name sourceSimpleName) {
        String sourceSimpleNameStr = sourceSimpleName.toString();
        if (StringUtils.isBlank(simpleName)) {
            simpleName = sourceSimpleNameStr + "$" + RandomUtil.randomString(6) + "$Impl";
        }
        if (StringUtils.isBlank(packageName)) {
            packageName = sourceQualifiedName.toString().split(sourceSimpleNameStr)[0];
        }
        if (packageName.endsWith(".")) {
            packageName = packageName.substring(0, packageName.lastIndexOf("."));
        }
        return Triple.ofTriple(simpleName, packageName, packageName + "." + simpleName);
    }

    /**
     * Based on the fully qualified name of the input class,
     * check its specifications and parse it into abbreviation,
     * package name, and fully qualified name output.
     * @see Class#getName()
     * @see Class#getSimpleName()
     * @see Class#getPackage()
     * @see Package#getName()
     * @see NameMetadata
     * @param name The fully qualified name of the class.
     * @return The abbreviation, package name, and full name obtained from the analysis.
     */
    public static Triple<String, String, String> getNameMetadataByProvider(String name) {
        if (StringUtils.isBlank(name)) return Triple.emptyTriple();
        if (!name.matches(NAME_MATCHER_EXPRESS)) return Triple.emptyTriple();
        if (name.endsWith(".")) {
            name = name.substring(0, name.lastIndexOf("."));
        }
        String packageName = name.substring(0, name.lastIndexOf("."));
        String simpleName = name.substring(name.lastIndexOf(".") + 1);
        return Triple.ofTriple(simpleName, packageName, name);
    }

    /**
     * Check if the package name and abbreviation are empty.
     * @param packageName package name of class.
     * @param simpleName simple name of class.
     */
    public static void checkName(String packageName, String simpleName) {
        Asserts.notBlank(packageName, "packageName not be null");
        Asserts.notBlank(simpleName, "simpleName not be null");
    }

    /**
     * Convert the array collection of {@link ClassSource} to a
     * type that {@link CodeAppender#getInterfaceWithGenericsNames()} can parse.
     * @param interfaceClassSources interface Class Sources with {@link ClassSource}.
     * @return {@link GeneratedCodeAppenderBuilder#interfaces(Map)}
     */
    public static Map<String, String[]> convertInterfaceClassNameSources(ClassSource[] interfaceClassSources) {
        if (ArrayUtils.isEmpty(interfaceClassSources)) return Collections.emptyMap();
        return Arrays.stream(interfaceClassSources).collect(Collectors.toMap(ClassSource::name,
                ClassSource::genericsNames));
    }

    /**
     * Convert the array collection of {@link AnnotationSource} to a
     * type that {@link CodeAppender#getAnnotationWithValueNames()} can parse.
     * @param annotationSources annotation Class Sources with {@link ClassSource}.
     * @return {@link GeneratedCodeAppenderBuilder#annotations(Map)}
     */
    public static Map<String, String> convertAnnotationNameSources(AnnotationSource[] annotationSources) {
        if (ArrayUtils.isEmpty(annotationSources)) return Collections.emptyMap();
        return Arrays.stream(annotationSources).collect(Collectors.toMap(AnnotationSource::name,
                AnnotationSource::valueSet));
    }

    private static void setGeneratedNote(StringBuilder classAppend, String description) {
        classAppend.append(String.format(NOTE_FORMAT,
                        StringUtils.isNotBlank(description) ? description : defaultDescribe,
                        email,
                        id,
                        version))
                .append(LINE_BREAK);
    }

    private static void setPackage(String packageName, StringBuilder importAppend) {
        importAppend.append(JAVA_PACKAGE_WORD)
                .append(JAVA_SYNTAX_BLANK)
                .append(packageName)
                .append(JAVA_SEMICOLON_WORD)
                .append(LINE_BREAK)
                .append(LINE_BREAK);
    }

    private static void addClassPurpose(ClassKind classKind, String simpleName, StringBuilder classAppend) {
        classAppend.append(JAVA_CLASS_DEFAULT_PERM)
                .append(JAVA_SYNTAX_BLANK)
                .append(classKind.getAppender())
                .append(JAVA_SYNTAX_BLANK)
                .append(simpleName)
                .append(JAVA_SYNTAX_BLANK);
    }

    private static void addImport(String className, StringBuilder importAppend) {
        importAppend.append(JAVA_IMPORT_WORD)
                .append(JAVA_SYNTAX_BLANK)
                .append(className)
                .append(JAVA_SEMICOLON_WORD)
                .append(LINE_BREAK);
    }

    private static void addAnnotation(String annotationClassName, String value, StringBuilder classAppend) {
        classAppend.append(ANNOTATION_PREFIX).append(annotationClassName);
        if (StringUtils.isNotBlank(value)) {
            classAppend.append(LEFT_BRACKET)
                    .append(AnnotationSource.SUPPORT_KEY)
                    .append(EQUAL_COMMA)
                    .append(QUO_MARK)
                    .append(value)
                    .append(QUO_MARK)
                    .append(RIGHT_BRACKET);
        }
        classAppend.append(LINE_BREAK);
    }

    private static void addGenerics(String[] genericsClassNames, StringBuilder importAppend, StringBuilder classAppend) {
        if (ArrayUtils.isNotEmpty(genericsClassNames)) {
            StringBuilder genericsAppend = new StringBuilder(JAVA_GENERICS_LEFT_ANGLE_BRACKET);
            for (int i = 0; i < genericsClassNames.length; i++) {
                String genericsName = genericsClassNames[i];
                Triple<String, String, String> genericsNameMetadata = getNameMetadataByProvider(genericsName);
                if (i < genericsClassNames.length - 1) {
                    genericsAppend.append(genericsNameMetadata.getV1())
                            .append(JAVA_COMMA_WORD);
                } else {
                    genericsAppend.append(genericsNameMetadata.getV1());
                }
                addImport(genericsNameMetadata.getV3(), importAppend);
            }
            genericsAppend.append(JAVA_GENERICS_RIGHT_ANGLE_BRACKET);
            classAppend.append(JAVA_SYNTAX_BLANK).append(genericsAppend);
        }
    }
}
