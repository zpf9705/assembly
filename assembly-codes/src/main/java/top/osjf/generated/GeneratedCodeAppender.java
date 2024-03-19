package top.osjf.generated;

import java.util.Map;

/**
 * Implementation class for writing source code to interface {@link CodeAppender}.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.1.0
 */
public final class GeneratedCodeAppender implements CodeAppender {

    private final String description;

    private final ClassKind classKind;

    private final String simpleName;

    private final String packageName;

    private final String extendClassName;

    private final String[] extendGenericsClassNames;

    private final Map<String, String[]> interfaceWithGenericsNames;

    private final Map<String, String> annotationWithValueNames;


    GeneratedCodeAppender(String description,
                          ClassKind classKind,
                          String packageName,
                          String simpleName,
                          String extendClassName,
                          String[] extendGenericsClassNames,
                          Map<String, String[]> interfaceWithGenericsNames,
                          Map<String, String> annotationWithValueNames) {
        this.description = description;
        this.classKind = classKind;
        this.packageName = packageName;
        this.simpleName = simpleName;
        this.extendClassName = extendClassName;
        this.extendGenericsClassNames = extendGenericsClassNames;
        this.interfaceWithGenericsNames = interfaceWithGenericsNames;
        this.annotationWithValueNames = annotationWithValueNames;
    }

    public static GeneratedCodeAppenderBuilder builder() {
        return new GeneratedCodeAppenderBuilder();
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public ClassKind getClassKind() {
        return classKind;
    }

    @Override
    public String getPackageName() {
        return packageName;
    }

    @Override
    public String getSimpleName() {
        return simpleName;
    }

    @Override
    public String getExtendClassName() {
        return extendClassName;
    }

    @Override
    public String[] getExtendGenericsClassNames() {
        return extendGenericsClassNames;
    }

    @Override
    public Map<String, String[]> getInterfaceWithGenericsNames() {
        return interfaceWithGenericsNames;
    }

    @Override
    public Map<String, String> getAnnotationWithValueNames() {
        return annotationWithValueNames;
    }

    @Override
    public String toString() {
        return GeneratedUtils.codeAppenderToString(this);
    }
}
