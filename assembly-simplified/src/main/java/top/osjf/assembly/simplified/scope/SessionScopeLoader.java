package top.osjf.assembly.simplified.scope;

/**
 * Replace the interface function of Spring container bean scope annotation
 * {@link org.springframework.web.context.annotation.SessionScope} with
 * additional loading function.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 2.1.6
 */
public interface SessionScopeLoader extends ScopeLoader {
    @Override
    default SupportScopeType type(){
        return SupportScopeType.SESSION;
    }
}
