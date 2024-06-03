package top.osjf.assembly.simplified.scope;

import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * {@code @SupportScope} is a specialization of {@link Scope @Scope} for a
 * component whose lifecycle is bound to the {@link Scope} of you can provide
 * support.
 *
 * <p>Specifically, {@code @SupportScope} is a <em>composed annotation</em> that
 * acts as a shortcut for {@code @Scope("support")} with the default
 * {@link #proxyMode} set to {@link ScopedProxyMode#TARGET_CLASS TARGET_CLASS}.
 *
 * <p>{@code @SupportScope} may be used as a meta-annotation to create custom
 * composed annotations.
 *
 * <p>At present, there is no actual scope processing. If you need it, you can
 * use {@link ConfigurableListableBeanFactory#registerScope(String,
 * org.springframework.beans.factory.config.Scope)} to register a
 * {@link org.springframework.beans.factory.config.Scope} and compare it with
 * the {@code thread} of this annotation.
 *
 * <p>If you use {@link EnableRegisterSupportScope} to provide relevant
 * {@link org.springframework.beans.factory.config.Scope}, it can automatically
 * register relevant scope support for you.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @see org.springframework.web.context.support.ServletContextScope
 * @see org.springframework.web.context.annotation.ApplicationScope
 * @see org.springframework.web.context.request.SessionScope
 * @see org.springframework.web.context.annotation.SessionScope
 * @see org.springframework.web.context.request.RequestScope
 * @see org.springframework.web.context.annotation.RequestScope
 * @see org.springframework.context.support.SimpleThreadScope
 * @see SupportScope
 * @since 2.2.5
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Scope(SupportScope.SCOPE_NAME)
public @interface SupportScope {

    /**
     * {@link Scope} for {@link SupportScope} provider scope name.
     */
    String SCOPE_NAME = "support";

    /**
     * Alias for {@link Scope#proxyMode}.
     * <p>Defaults to {@link ScopedProxyMode#TARGET_CLASS}.
     *
     * @return Enumerates the various scoped-proxy options.
     */
    @AliasFor(annotation = Scope.class)
    ScopedProxyMode proxyMode() default ScopedProxyMode.TARGET_CLASS;
}
