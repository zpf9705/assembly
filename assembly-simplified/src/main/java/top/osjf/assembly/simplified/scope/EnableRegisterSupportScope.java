package top.osjf.assembly.simplified.scope;

import org.springframework.beans.factory.config.Scope;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * Provide a {@link Scope} scope processing class for {@link SupportScope}
 * annotations and provide automatic registration.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 2.2.5
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(RegisterSupportScopeConfiguration.class)
public @interface EnableRegisterSupportScope {

    /**
     * Copy from {@link Scope}.<br>
     * <p>{@code Scope} implementations are expected to be thread-safe.
     * One {@code Scope} instance can be used with multiple bean factories
     * at the same time, if desired (unless it explicitly wants to be aware of
     * the containing BeanFactory), with any number of threads accessing
     * the {@code Scope} concurrently from any number of factories.
     *
     * @return The implementation type of {@link Scope}.
     */
    Class<? extends Scope> value();
}
