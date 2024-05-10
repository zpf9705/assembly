package top.osjf.assembly.simplified.init;

import org.springframework.aop.support.AopUtils;

/**
 * Add initialization conditions to filter the initialization
 * of spring aop proxy objects.
 *
 * @see AopUtils
 * @see org.springframework.aop.framework.AopProxyUtils
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 2.2.5
 */
public abstract class AbstractWithOutAopProxyInit extends AbstractInit {

    @Override
    protected boolean conditionalJudgment() {
        return !AopUtils.isAopProxy(this);
    }
}
