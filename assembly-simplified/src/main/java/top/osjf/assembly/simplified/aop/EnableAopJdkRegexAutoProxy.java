package top.osjf.assembly.simplified.aop;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * Add this annotation to configure a spring AOP, directly using
 * {@link org.springframework.aop.support.JdkRegexpMethodPointcut} with
 * parameter {@link #patten()} as its method matching criterion.
 *
 * <p>After adding this annotation, the method information of the current
 * thread can be obtained in real-time in {@link InvocationContext}.
 *
 * <p>The implementation method is similar to {@link org.springframework.aop.framework.AopContext},
 * and the real-time method information is deleted after the method
 * execution is completed.
 *
 * @see org.springframework.aop.support.StaticMethodMatcher
 * @see org.springframework.aop.support.StaticMethodMatcherPointcut
 * @see org.springframework.aop.support.AbstractRegexpMethodPointcut
 * @see org.springframework.aop.support.JdkRegexpMethodPointcut
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 2.0.7
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(AopJdkRegexAutoProxyConfiguration.class)
public @interface EnableAopJdkRegexAutoProxy {

    /**
     * Regular expression for the fully-qualified method names to match.
     * The exact regexp syntax will depend on the subclass (e.g. Perl5 regular expressions)
     * @return Must be filled in.
     */
    String patten();
}
