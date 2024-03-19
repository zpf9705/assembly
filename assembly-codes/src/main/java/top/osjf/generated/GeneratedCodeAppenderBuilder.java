package top.osjf.generated;

import org.apache.commons.lang3.builder.Builder;
import top.osjf.assembly.util.annotation.CanNull;
import top.osjf.assembly.util.annotation.NotNull;

import java.util.Map;

/**
 * Builder for {@link GeneratedCodeAppender}.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.1.0
 */
public class GeneratedCodeAppenderBuilder implements Builder<GeneratedCodeAppender> {

    private String description;

    private ClassKind classKind;

    private String simpleName;

    private String packageName;

    private String extendClassName;

    private String[] extendGenericsClassNames;

    private Map<String, String[]> interfaceWithGenericsNames;

    private Map<String, String> annotationWithValueNames;

    public GeneratedCodeAppenderBuilder description(String description) {
        this.description = description;
        return this;
    }

    public GeneratedCodeAppenderBuilder classKind(@CanNull ClassKind classKind) {
        this.classKind = classKind;
        return this;
    }

    public GeneratedCodeAppenderBuilder packageName(@NotNull String packageName) {
        this.packageName = packageName;
        return this;
    }

    public GeneratedCodeAppenderBuilder simpleName(@NotNull String simpleName) {
        this.simpleName = simpleName;
        return this;
    }


    public GeneratedCodeAppenderBuilder extend(@CanNull String extendClassName) {
        this.extendClassName = extendClassName;
        return this;
    }


    public GeneratedCodeAppenderBuilder extendGenerics(@CanNull String[] extendGenericsClassNames) {
        this.extendGenericsClassNames = extendGenericsClassNames;
        return this;
    }

    public GeneratedCodeAppenderBuilder interfaces(@CanNull Map<String, String[]> interfaceWithGenericsNames) {
        this.interfaceWithGenericsNames = interfaceWithGenericsNames;
        return this;
    }


    public GeneratedCodeAppenderBuilder annotations(@CanNull Map<String, String> annotationWithValueNames) {
        this.annotationWithValueNames = annotationWithValueNames;
        return this;
    }

    @Override
    public GeneratedCodeAppender build() {

        if (classKind == null) {
            this.classKind = ClassKind.CLASS;
        }

        GeneratedUtils.checkName(packageName, simpleName);

        return new GeneratedCodeAppender(description, classKind, packageName, simpleName, extendClassName,
                extendGenericsClassNames, interfaceWithGenericsNames, annotationWithValueNames);
    }
}
