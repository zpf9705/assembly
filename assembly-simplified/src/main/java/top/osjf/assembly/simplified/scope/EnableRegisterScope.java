package top.osjf.assembly.simplified.scope;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.Scope;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * Automatically configure switch annotations for scope names
 * and processing classes {@link Scope}.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 2.2.5
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(RegisterScopeConfiguration.class)
public @interface EnableRegisterScope {

    /**
     * Specifies the name of the scope to use for the annotated component/bean.
     * <p>Defaults to an empty string ({@code ""}) which implies
     * {@link ConfigurableBeanFactory#SCOPE_SINGLETON SCOPE_SINGLETON}.
     *
     * @see ConfigurableBeanFactory#SCOPE_PROTOTYPE
     * @see ConfigurableBeanFactory#SCOPE_SINGLETON
     * @see org.springframework.web.context.WebApplicationContext#SCOPE_REQUEST
     * @see org.springframework.web.context.WebApplicationContext#SCOPE_SESSION
     */
    String scopeName() default "";

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
    Class<? extends Scope> scopeType() default Scope.class;
}
