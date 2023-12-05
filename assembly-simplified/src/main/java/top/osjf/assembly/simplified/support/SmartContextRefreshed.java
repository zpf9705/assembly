package top.osjf.assembly.simplified.support;

import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import top.osjf.assembly.util.annotation.NotNull;
import top.osjf.assembly.util.lang.Asserts;

/**
 * Smart State checker for after Context refresh.
 *
 * @author zpf
 * @since 2.0.6
 */
public abstract class SmartContextRefreshed extends AbstractApplicationContextListener {

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        Asserts.state(getApplicationContext() == event.getApplicationContext(),
                "Inconsistency before and after application startup.");
    }

    /**
     * @return Currently supported application context information.
     */
    @NotNull
    public abstract ApplicationContext getApplicationContext();
}
