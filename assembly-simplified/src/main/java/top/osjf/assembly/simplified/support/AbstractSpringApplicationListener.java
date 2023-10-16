package top.osjf.assembly.simplified.support;

import org.springframework.boot.context.event.*;

/**
 * Abstract impl for {@link SpringApplicationListeners}.
 * @see SpringApplicationListeners
 * @author zpf
 * @since 2.0.6
 */
public abstract class AbstractSpringApplicationListener implements SpringApplicationListeners {

    /**
     * Handle an Application Context Initialized application event.
     * @param event the event to respond to.
     */
    @Override
    public void onApplicationEvent(ApplicationContextInitializedEvent event) {
    }

    /**
     * Handle an Application Environment Prepared application event.
     * @param event the event to respond to.
     */
    @Override
    public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
    }

    /**
     * Handle an Application Failed application event.
     * @param event the event to respond to.
     */
    @Override
    public void onApplicationEvent(ApplicationFailedEvent event) {
    }

    /**
     * Handle an Application Prepared application event.
     * @param event the event to respond to.
     */
    @Override
    public void onApplicationEvent(ApplicationPreparedEvent event) {
    }

    /**
     * Handle an Application Ready application event.
     *
     * @param event the event to respond to.
     */
    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
    }

    /**
     * Handle an Application Started application event.
     * @param event the event to respond to.
     */
    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
    }

    /**
     * Handle an Application Starting application event.
     * @param event the event to respond to.
     */
    @Override
    public void onApplicationEvent(ApplicationStartingEvent event) {
    }
}
