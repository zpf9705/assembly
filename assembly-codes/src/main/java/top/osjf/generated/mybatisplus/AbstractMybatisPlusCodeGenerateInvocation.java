package top.osjf.generated.mybatisplus;

import top.osjf.assembly.util.annotation.NotNull;
import top.osjf.generated.GeneratedCodeAppenderBuilder;
import top.osjf.generated.impl.annotation.AbstractCodeGenerateInvocation;

/**
 * Abstract implementation for special attributes of the mybatis plus framework.
 * @see MybatisPlusCodeGenerate
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.1.0
 */
public abstract class AbstractMybatisPlusCodeGenerateInvocation extends AbstractCodeGenerateInvocation
        implements MybatisPlusCodeGenerateInvocation {

    private final boolean join;

    private final String tableChineseName;

    public AbstractMybatisPlusCodeGenerateInvocation(String simpleName, String packageName, String targetName,
                                                     boolean join, String tableChineseName) {
        super(simpleName, packageName, targetName);
        this.join = join;
        this.tableChineseName = tableChineseName;
    }

    @Override
    public boolean getJoin() {
        return join;
    }

    @Override
    public String getTableChineseName() {
        return tableChineseName;
    }

    @NotNull
    @Override
    protected GeneratedCodeAppenderBuilder getGeneratedCodeAppenderBuilder() {

        GeneratedCodeAppenderBuilder builder = super.getGeneratedCodeAppenderBuilder();

        builder.description(getTableChineseName() + getCharacterization());

        return builder;
    }

    protected abstract String getCharacterization();
}
