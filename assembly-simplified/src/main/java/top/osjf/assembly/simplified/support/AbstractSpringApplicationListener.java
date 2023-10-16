package top.osjf.assembly.simplified.support;

import org.springframework.boot.ConfigurableBootstrapContext;
import org.springframework.boot.context.event.*;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import top.osjf.assembly.util.annotation.NotNull;

/**
 * Abstract help class for spring application time notification.
 * <p>Distinguish between the following stages: context preparation, environment
 * preparation, startup failure, startup preparation, startup in progress, and runtime.
 * <p>Simply inherit this class and rewrite the corresponding stage's methods to
 * obtain the notification given after the corresponding event is executed.
 *
 * @see ApplicationContextInitializedEvent {@link EventPublishingRunListener#contextPrepared(
 * ConfigurableApplicationContext)}
 * @see ApplicationEnvironmentPreparedEvent {@link EventPublishingRunListener#environmentPrepared(
 * ConfigurableBootstrapContext, ConfigurableEnvironment)}
 * @see ApplicationFailedEvent {@link EventPublishingRunListener#failed(ConfigurableApplicationContext, Throwable)}
 * @see ApplicationPreparedEvent {@link EventPublishingRunListener#contextLoaded(ConfigurableApplicationContext)}
 * @see ApplicationReadyEvent {@link EventPublishingRunListener#running(ConfigurableApplicationContext)}
 * @see ApplicationStartedEvent {@link EventPublishingRunListener#started(ConfigurableApplicationContext)}
 * @see ApplicationStartingEvent {@link EventPublishingRunListener#starting(ConfigurableBootstrapContext)}
 * @author zpf
 * @since 2.0.6
 */
public class AbstractSpringApplicationListener implements ApplicationListener<SpringApplicationEvent> {

    @Override
    public void onApplicationEvent(@NotNull SpringApplicationEvent event) {
        if (event instanceof ApplicationContextInitializedEvent) {
            onApplicationEvent((ApplicationContextInitializedEvent) event);
        } else if (event instanceof ApplicationEnvironmentPreparedEvent) {
            onApplicationEvent((ApplicationEnvironmentPreparedEvent) event);
        } else if (event instanceof ApplicationFailedEvent) {
            onApplicationEvent((ApplicationFailedEvent) event);
        } else if (event instanceof ApplicationPreparedEvent) {
            onApplicationEvent((ApplicationPreparedEvent) event);
        } else if (event instanceof ApplicationReadyEvent) {
            onApplicationEvent((ApplicationReadyEvent) event);
        } else if (event instanceof ApplicationStartedEvent) {
            onApplicationEvent((ApplicationStartedEvent) event);
        } else if (event instanceof ApplicationStartingEvent) {
            onApplicationEvent((ApplicationStartingEvent) event);
        }
    }

    /**
     * Handle an Application Context Initialized application event.
     * @param event the event to respond to.
     */
    public void onApplicationEvent(ApplicationContextInitializedEvent event) {
    }

    /**
     * Handle an Application Environment Prepared application event.
     * @param event the event to respond to.
     */
    public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
    }

    /**
     * Handle an Application Failed application event.
     * @param event the event to respond to.
     */
    public void onApplicationEvent(ApplicationFailedEvent event) {
    }

    /**
     * Handle an Application Prepared application event.
     * @param event the event to respond to.
     */
    public void onApplicationEvent(ApplicationPreparedEvent event) {
    }

    /**
     * Handle an Application Ready application event.
     *
     * @param event the event to respond to.
     */
    public void onApplicationEvent(ApplicationReadyEvent event) {
    }

    /**
     * Handle an Application Started application event.
     * @param event the event to respond to.
     */
    public void onApplicationEvent(ApplicationStartedEvent event) {
    }

    /**
     * Handle an Application Starting application event.
     * @param event the event to respond to.
     */
    public void onApplicationEvent(ApplicationStartingEvent event) {
    }
}
