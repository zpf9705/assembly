package top.osjf.assembly.simplified.support;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.*;
import top.osjf.assembly.util.annotation.NotNull;

/**
 * Abstract help class for spring context time notification.
 * <p>Distinguish between the following four stages: container refresh completion,
 * start, stop, and close.
 * <p>Simply inherit this class and rewrite the corresponding stage's methods to
 * obtain the notification given after the corresponding event is executed.
 *
 * @see ContextRefreshedEvent
 * @see ContextStartedEvent
 * @see ContextStoppedEvent
 * @see ContextClosedEvent
 * @author zpf
 * @since 2.0.6
 */
public abstract class AbstractApplicationListener implements ApplicationListener<ApplicationContextEvent> {

    @Override
    public void onApplicationEvent(@NotNull ApplicationContextEvent event) {
        if (event instanceof ContextRefreshedEvent) {
            onApplicationEvent((ContextRefreshedEvent) event);
        } else if (event instanceof ContextClosedEvent) {
            onApplicationEvent((ContextClosedEvent) event);
        } else if (event instanceof ContextStartedEvent) {
            onApplicationEvent((ContextStartedEvent) event);
        } else if (event instanceof ContextStoppedEvent) {
            onApplicationEvent((ContextStoppedEvent) event);
        }
    }

    /**
     * Handle a Context Refreshed application event.
     * @param event the event to respond to.
     */
    public void onApplicationEvent(ContextRefreshedEvent event) {
    }

    /**
     * Handle a Context Closed application event.
     * @param event the event to respond to.
     */
    public void onApplicationEvent(ContextClosedEvent event) {
    }

    /**
     * Handle a Context Started application event.
     * @param event the event to respond to.
     */
    public void onApplicationEvent(ContextStartedEvent event) {
    }

    /**
     * Handle a Context Stopped application event.
     * @param event the event to respond to.
     */
    public void onApplicationEvent(ContextStoppedEvent event) {
    }
}
