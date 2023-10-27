package top.osjf.assembly.util.lang;

import top.osjf.assembly.util.annotation.NotNull;

/**
 * When the method type is inconsistent with the expected type,
 * this exception prompt is thrown.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.5
 */
public class MethodReturnTypeNoEqualException extends Exception {
    private static final long serialVersionUID = 4515411840150301586L;

    @NotNull
    private final Class<?> needType;

    @NotNull
    private final Class<?> provideType;

    public MethodReturnTypeNoEqualException(Class<?> needType, Class<?> provideType) {
        super(String.format("Expected type %s, but provided %s.", needType.getName(),
                provideType.getName()));
        this.needType = needType;
        this.provideType = provideType;
    }

    @NotNull
    public Class<?> getNeedType() {
        return needType;
    }

    @NotNull
    public Class<?> getProvideType() {
        return provideType;
    }
}
