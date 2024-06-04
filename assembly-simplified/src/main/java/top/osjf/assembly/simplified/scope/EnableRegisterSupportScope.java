package top.osjf.assembly.simplified.scope;

import org.springframework.beans.factory.config.Scope;
import org.springframework.core.annotation.AliasFor;

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
@EnableRegisterScope(scopeName = SupportScope.SCOPE_NAME)
public @interface EnableRegisterSupportScope {

    /*
     * @see EnableRegisterScope#scopeType()
     */
    @AliasFor(annotation = EnableRegisterScope.class)
    Class<? extends Scope> scopeType();
}
