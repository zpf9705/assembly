package top.osjf.assembly.simplified.aop.init.annotation;

import org.springframework.context.annotation.Import;
import top.osjf.assembly.simplified.aop.init.*;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation can enable the interception proxy
 * of the Spring aop aspectj method, executing the pre
 * method, post method, and confirmation initialization
 * method before executing the initialization method.
 *
 * @see InitConfiguration
 * @see AspectJInitSupport
 * @see InitBefore
 * @see InitAble
 * @see ActionInited
 * @see InitAfter
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 2.2.4
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(InitConfiguration.class)
public @interface EnableInit {
}
