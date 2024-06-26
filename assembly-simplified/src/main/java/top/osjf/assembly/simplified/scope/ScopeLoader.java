package top.osjf.assembly.simplified.scope;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * The loading interface for unNormal scope container beans in the Spring
 * framework (normal scope includes singleton and multi instance), and the
 * special scope includes the request domain{@code SCOPE_REQUEST},application
 * domain{@code SCOPE_APPLICATION}, session domain{@code SCOPE_SESSION},
 * etc. defined by the Spring framework.
 *
 * <p>This interface defines the initialization method {@link #load()} for the
 * special scope beans mentioned above, the callback after setting the properties,
 * and the destroy callback when discarding usage.
 *
 * <p>Due to the fact that the special scope of beans in the Spring framework cannot
 * be dynamically changed in {@link org.springframework.beans.factory.config.BeanDefinition},
 * the establishment of special scopes still requires the use of annotations from the
 * Spring special framework, as referenced in {@link org.springframework.context.annotation.Scope}.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 2.1.7
 */
public interface ScopeLoader extends InitializingBean, DisposableBean {

    /**
     * The loading method at the time of triggering a special scope can
     * be called to create a bean for this special scope.
     *
     * <p>At the end of the call, the {@link #destroy()} callback is
     * automatically called to destroy it.
     */
    void load();

    /**
     * After using a special scope to create a bean and completing attribute assignments,
     * certain necessary initialization logic can be performed.
     *
     * @throws Exception The exception generated by special initialization logic is
     *                   generally {@link org.springframework.beans.BeansException}.
     */
    @Override
    default void afterPropertiesSet() throws Exception {
    }

    /**
     * The destruction callback after the use of a special scope can be
     * used to perform certain end logic.
     *
     * @throws Exception Exception generated by bean destruction logic.
     */
    @Override
    default void destroy() throws Exception {
    }
}
