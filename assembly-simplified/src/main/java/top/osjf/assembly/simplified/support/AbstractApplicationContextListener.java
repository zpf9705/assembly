package top.osjf.assembly.simplified.support;

import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.event.ContextStoppedEvent;

/**
 * Abstract impl for {@link ApplicationContextListeners}.
 * @see ApplicationContextListeners
 * @author zpf
 * @since 2.0.6
 */
public abstract class AbstractApplicationContextListener implements ApplicationContextListeners {

    /**
     * Handle a Context Refreshed application event.
     * @param event the event to respond to.
     */
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
    }

    /**
     * Handle a Context Closed application event.
     * @param event the event to respond to.
     */
    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
    }

    /**
     * Handle a Context Started application event.
     * @param event the event to respond to.
     */
    @Override
    public void onApplicationEvent(ContextStartedEvent event) {
    }

    /**
     * Handle a Context Stopped application event.
     * @param event the event to respond to.
     */
    @Override
    public void onApplicationEvent(ContextStoppedEvent event) {
    }
}
