package top.osjf.assembly.simplified.support;

import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.*;
import top.osjf.assembly.util.annotation.NotNull;


/**
 * Help class for spring context time notification.
 * <p>Distinguish between the following four stages: context refresh completion,
 * start, stop, and close.
 * <p>Simply inherit this class and rewrite the corresponding stage's methods to
 * obtain the notification given after the corresponding event is executed.
 *
 * @see ContextRefreshedEvent {@code AbstractApplicationContext#finishRefresh()}
 * @see ContextStartedEvent   {@link ConfigurableApplicationContext#start()}}
 * @see ContextStoppedEvent   {@link ConfigurableApplicationContext#stop()}
 * @see ContextClosedEvent    {@code AbstractApplicationContext#doClose()}
 * @author zpf
 * @since 2.0.6
 */
public interface ApplicationContextListeners extends ApplicationListener<ApplicationContextEvent> {

    @Override
    default void onApplicationEvent(@NotNull ApplicationContextEvent event){
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
    void onApplicationEvent(ContextRefreshedEvent event);

    /**
     * Handle a Context Closed application event.
     * @param event the event to respond to.
     */
    void onApplicationEvent(ContextClosedEvent event);

    /**
     * Handle a Context Started application event.
     * @param event the event to respond to.
     */
    void onApplicationEvent(ContextStartedEvent event);

    /**
     * Handle a Context Stopped application event.
     * @param event the event to respond to.
     */
    void onApplicationEvent(ContextStoppedEvent event);
}
