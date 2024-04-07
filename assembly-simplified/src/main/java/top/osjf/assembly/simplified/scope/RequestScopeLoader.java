package top.osjf.assembly.simplified.scope;

/**
 * Replace the interface function of Spring container bean scope annotation
 * {@link org.springframework.web.context.annotation.RequestScope} with
 * additional loading function.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 2.1.6
 */
public interface RequestScopeLoader extends ScopeLoader {
    @Override
    default SupportScopeType type(){
        return SupportScopeType.REQUEST;
    }
}
