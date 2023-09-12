package top.osjf.assembly.cron.annotation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.MarkerIgnoringBase;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AliasFor;
import top.osjf.assembly.cron.AbstractCornRegister;
import top.osjf.assembly.cron.AbstractExceptionCronListener;
import top.osjf.assembly.cron.CronListener;

import java.lang.annotation.*;

/**
 * The annotation identifier of the timed task registration switch can specify the path for scanning {@link Cron}
 * and set the survival mode of the calling object {@link Type}.
 * <p>
 * This annotation will only take effect when it exists on the class header of the spring container object.
 * <p>
 * Please pay attention to how to use it
 * <p>
 *
 * @author zpf
 * @see CronTaskRegister
 * @see AbstractCornRegister
 * @see org.springframework.context.ApplicationContext
 * @see org.springframework.core.env.Environment
 * @since 1.1.0
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({CronTaskRegister.class})
public @interface EnableCronTaskRegister {

    /**
     * Carrying the path where the {@link Cron} class is located.
     * <p>
     * At the same time, task listeners can also be added to implement
     * {@link CronListener} or
     * {@link AbstractExceptionCronListener}
     * <p>
     * If it is null, the default is to use springboot to start the package path where the main class is located.
     *
     * @return alias for {{@link #basePackages()}}
     */
    @AliasFor("basePackages")
    String[] value() default {};

    /**
     * His value shifts to {@link #value()}, consistent with it.
     * <p>
     * At the same time, task listeners can also be added to implement
     * {@link CronListener} or
     * {@link AbstractExceptionCronListener}
     * <p>
     * If it is null, the default is to use springboot to start the package path where the main class is located.
     *
     * @return alias for {{@link #value()}}
     */
    @AliasFor("value")
    String[] basePackages() default {};

    /**
     * The survival mode of the calling object triggered by a timed task,
     * which defaults to the proxy object of the spring container
     *
     * @return {@link Type}
     */
    Type type() default Type.PROXY;

    /**
     * Choose whether to start the timed thread pool even if no registration method is found.
     * <p>
     * But I won't actively add listeners for {@link CronListener},
     * unless a timing method with {@link Cron} annotation is added.
     *
     * @return if {@code true} , defaults to enabled
     */
    boolean noMethodDefaultStart() default false;

    /**
     * Regular registration information supports logger, which can print the number
     * of registrations and related registration exceptions.
     * <p>Used {@link org.slf4j.Logger}.</p>
     *
     * @return Return a {@link org.slf4j.Logger} clazz.
     */
    Class<? extends Logger> logger() default CronSlf4j.class;


    class CronSlf4j extends MarkerIgnoringBase implements Logger {

        private static final Logger logger = LoggerFactory.getLogger(CronSlf4j.class);

        @Override
        public boolean isTraceEnabled() {
            return logger.isTraceEnabled();
        }

        @Override
        public void trace(String s) {
            logger.trace(s);
        }

        @Override
        public void trace(String s, Object o) {
            logger.trace(s, o);
        }

        @Override
        public void trace(String s, Object o, Object o1) {
            logger.trace(s, o, o1);
        }

        @Override
        public void trace(String s, Object... objects) {
            logger.trace(s, objects);
        }

        @Override
        public void trace(String s, Throwable throwable) {
            logger.trace(s, throwable);
        }

        @Override
        public boolean isDebugEnabled() {
            return logger.isDebugEnabled();
        }

        @Override
        public void debug(String s) {
            logger.debug(s);
        }

        @Override
        public void debug(String s, Object o) {
            logger.debug(s, o);
        }

        @Override
        public void debug(String s, Object o, Object o1) {
            logger.debug(s, o, o1);
        }

        @Override
        public void debug(String s, Object... objects) {
            logger.debug(s, objects);
        }

        @Override
        public void debug(String s, Throwable throwable) {
            logger.debug(s, throwable);
        }

        @Override
        public boolean isInfoEnabled() {
            return logger.isInfoEnabled();
        }

        @Override
        public void info(String s) {
            logger.info(s);
        }

        @Override
        public void info(String s, Object o) {
            logger.info(s, o);
        }

        @Override
        public void info(String s, Object o, Object o1) {
            logger.info(s, o, o1);
        }

        @Override
        public void info(String s, Object... objects) {
            logger.info(s, objects);
        }

        @Override
        public void info(String s, Throwable throwable) {
            logger.info(s, throwable);
        }

        @Override
        public boolean isWarnEnabled() {
            return logger.isWarnEnabled();
        }

        @Override
        public void warn(String s) {
            logger.warn(s);
        }

        @Override
        public void warn(String s, Object o) {
            logger.warn(s, o);
        }

        @Override
        public void warn(String s, Object... objects) {
            logger.warn(s, objects);
        }

        @Override
        public void warn(String s, Object o, Object o1) {
            logger.warn(s, o, o1);
        }

        @Override
        public void warn(String s, Throwable throwable) {
            logger.warn(s, throwable);
        }

        @Override
        public boolean isErrorEnabled() {
            return logger.isErrorEnabled();
        }

        @Override
        public void error(String s) {
            logger.error(s);
        }

        @Override
        public void error(String s, Object o) {
            logger.error(s, o);
        }

        @Override
        public void error(String s, Object o, Object o1) {
            logger.error(s, o, o1);
        }

        @Override
        public void error(String s, Object... objects) {
            logger.error(s, objects);
        }

        @Override
        public void error(String s, Throwable throwable) {
            logger.error(s, throwable);
        }
    }
}
