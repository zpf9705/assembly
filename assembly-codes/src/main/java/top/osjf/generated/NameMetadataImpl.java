package top.osjf.generated;

import top.osjf.assembly.util.annotation.NotNull;

/**
 * Default Impl for {@link NameMetadata}.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.1.0
 */
public class NameMetadataImpl implements NameMetadata {

    private final String simpleName;

    private final String packageName;

    private String targetName;

    public NameMetadataImpl(String simpleName, String packageName) {
        this.simpleName = simpleName;
        this.packageName = packageName;
    }

    public NameMetadataImpl(String simpleName, String packageName, String targetName) {
        this.simpleName = simpleName;
        this.packageName = packageName;
        this.targetName = targetName;
    }

    @NotNull
    @Override
    public String getSimpleName() {
        return simpleName;
    }

    @NotNull
    @Override
    public String getPackageName() {
        return packageName;
    }

    @Override
    public String getTargetName() {
        return targetName;
    }
}
