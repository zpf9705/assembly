package top.osjf.generated.mybatisplus;

import top.osjf.generated.CodeGenerateInvocation;

import java.util.List;

/**
 * Method extension for mybatis plus framework's special support.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.1.0
 */
public interface MybatisPlusCodeGenerateInvocation extends CodeGenerateInvocation {

    /**
     * @return MybatisPlusCodeGenerate#join()
     * @see MybatisPlusCodeGenerate#join()
     */
    boolean getJoin();

    /**
     * @return MybatisPlusCodeGenerate#tableChineseName()
     * @see MybatisPlusCodeGenerate#tableChineseName()
     */
    String getTableChineseName();

    /**
     * @return Type of suffixName.
     * @see MybatisPlusCodeGenerate#mapperSuffixName()
     * @see MybatisPlusCodeGenerate#serviceSuffixName()
     * @see MybatisPlusCodeGenerate#serviceImplSuffixName()
     * @since 1.1.1
     */
    String getSuffixName();

    /**
     * Return the set of configuration items that need to be written
     * to the configuration file after generating the class.
     *
     * @return the configuration items after generating the class.
     */
    List<Configuration> getWriteConfiguration();

    /**
     * Write the encapsulation interface for the subsequent configuration.
     */
    interface Configuration {

        /**
         * The write prefix for the properties file.
         * @return write prefix for the properties file.
         */
        String getPrefix();

        /**
         * The write value for the properties file.
         * @return write value for the properties file.
         */
        String getValue();

        /**
         * The write rule “prefix=value” for the properties file.
         * @return write rule “prefix=value” for the properties file.
         */
        default String getWriteLine() {
            return getPrefix() + "=" + getValue() + "\n";
        }
    }
}
