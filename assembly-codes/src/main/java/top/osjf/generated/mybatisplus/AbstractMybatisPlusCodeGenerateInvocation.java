package top.osjf.generated.mybatisplus;

import top.osjf.assembly.util.annotation.NotNull;
import top.osjf.assembly.util.lang.StringUtils;
import top.osjf.generated.AbstractCodeGenerateInvocation;
import top.osjf.generated.GeneratedCodeAppenderBuilder;

import java.util.Collections;
import java.util.List;

/**
 * Abstract implementation for special attributes of the mybatis plus framework.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @see MybatisPlusCodeGenerate
 * @since 1.1.0
 */
public abstract class AbstractMybatisPlusCodeGenerateInvocation extends AbstractCodeGenerateInvocation
        implements MybatisPlusCodeGenerateInvocation {

    private final boolean join;

    private final String tableChineseName;

    private final String suffixName;

    public AbstractMybatisPlusCodeGenerateInvocation(String simpleName, String packageName, String targetName,
                                                     boolean join, String tableChineseName, String suffixName) {
        super(simpleName, packageName, targetName);
        this.join = join;
        this.tableChineseName = tableChineseName;
        this.suffixName = suffixName;
    }

    @Override
    public boolean getJoin() {
        return join;
    }

    @Override
    public String getTableChineseName() {
        return tableChineseName;
    }

    @Override
    public String getSuffixName() {
        return suffixName;
    }

    @NotNull
    @Override
    protected GeneratedCodeAppenderBuilder getGeneratedCodeAppenderBuilder() {
        GeneratedCodeAppenderBuilder builder = super.getGeneratedCodeAppenderBuilder();
        //When the provided table Chinese name is not empty, concatenate and annotate the
        // description based on the table name with special expressions.
        if (StringUtils.isNotBlank(getTableChineseName())) {
            builder.description(getTableChineseName() + getCharacterization());
        }
        return builder;
    }

    @Override
    public List<Configuration> getWriteConfiguration() {
        return Collections.singletonList(new Configuration() {
            @Override
            public String getPrefix() {
                return getSuffixNameConfigurationPrefix();
            }

            @Override
            public String getValue() {
                return getSuffixName();
            }
        });
    }

    /**
     * Returns the write configuration prefix name for the generated class abbreviation suffix.
     * @since 1.1.2
     * @return the write configuration prefix name for the generated class abbreviation suffix.
     */
    protected abstract String getSuffixNameConfigurationPrefix();

    /**
     * Returns a special description of the generated class.
     * @return a special description of the generated class.
     */
    protected abstract String getCharacterization();
}
