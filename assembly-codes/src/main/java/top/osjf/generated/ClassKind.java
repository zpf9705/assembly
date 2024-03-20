package top.osjf.generated;

/**
 * Provide keyword descriptions for generating types, including
 * Java interfaces, enumerations, regular classes, and abstract classes.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.1.0
 */
public enum ClassKind {

    CLASS("class"),

    ENUM("enum"),

    ABSTRACT_CLASS("abstract class"),

    INTERFACE("interface");

    final String appender;

    ClassKind(String appender) {
        this.appender = appender;
    }

    public String getAppender() {
        return appender;
    }

    /**
     * Check if it is an enumeration type.
     * @return Returns {@literal true}, otherwise it is not.
     */
    public boolean isEnum(){
        return ENUM.equals(this);
    }
}
