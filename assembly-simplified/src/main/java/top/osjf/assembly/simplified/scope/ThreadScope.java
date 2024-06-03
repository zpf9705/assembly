package top.osjf.assembly.simplified.scope;

import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * {@code @ThreadScope} is a specialization of {@link Scope @Scope} for a
 * component whose lifecycle is bound to the current {@link Thread Thread}.
 *
 * <p>Specifically, {@code @ThreadScope} is a <em>composed annotation</em> that
 * acts as a shortcut for {@code @Scope("thread")} with the default
 * {@link #proxyMode} set to {@link ScopedProxyMode#TARGET_CLASS TARGET_CLASS}.
 *
 * <p>{@code @ThreadScope} may be used as a meta-annotation to create custom
 * composed annotations.
 *
 * <p>At present, there is no actual scope processing. If you need it, you can
 * use {@link ConfigurableListableBeanFactory#registerScope(String,
 * org.springframework.beans.factory.config.Scope)} to register a
 * {@link org.springframework.beans.factory.config.Scope} and compare it with
 * the {@code thread} of this annotation.
 *
 * @see org.springframework.web.context.support.ServletContextScope
 * @see org.springframework.web.context.annotation.ApplicationScope
 * @see org.springframework.web.context.request.SessionScope
 * @see org.springframework.web.context.annotation.SessionScope
 * @see org.springframework.web.context.request.RequestScope
 * @see org.springframework.web.context.annotation.RequestScope
 * @see org.springframework.context.support.SimpleThreadScope
 * @see ThreadScope
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 2.2.5
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Scope("thread")
public @interface ThreadScope {

    /**
     * Alias for {@link Scope#proxyMode}.
     * <p>Defaults to {@link ScopedProxyMode#TARGET_CLASS}.
     * @return Enumerates the various scoped-proxy options.
     */
    @AliasFor(annotation = Scope.class)
    ScopedProxyMode proxyMode() default ScopedProxyMode.TARGET_CLASS;
}
