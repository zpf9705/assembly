package top.osjf.generated.mybatisplus;

import top.osjf.generated.CodeGenerateInvocation;

/**
 * Method extension for mybatis plus framework's special support.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.1.0
 */
public interface MybatisPlusCodeGenerateInvocation extends CodeGenerateInvocation {

    /**
     * @see MybatisPlusCodeGenerate#join()
     * @return MybatisPlusCodeGenerate#join()
     */
    boolean getJoin();

    /**
     * @see MybatisPlusCodeGenerate#tableChineseName()
     * @return MybatisPlusCodeGenerate#tableChineseName()
     */
    String getTableChineseName();

    /**
     * @see MybatisPlusCodeGenerate#mapperSuffixName()
     * @see MybatisPlusCodeGenerate#serviceSuffixName()
     * @see MybatisPlusCodeGenerate#serviceImplSuffixName()
     * @since 1.1.1
     * @return Type of suffixName.
     */
    String getSuffixName();
}
