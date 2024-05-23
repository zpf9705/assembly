package top.osjf.assembly.simplified.support;

/**
 * About the technology selection enumeration class when creating proxy objects.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 2.2.5
 */
public enum ProxyModel {

    /**
     * The enumeration representation of the JDK dynamic proxy model.
     */
    JDK,

    /**
     * The enumeration representation of cglib dynamic proxy based on Spring.
     */
    SPRING_CJ_LIB
}
