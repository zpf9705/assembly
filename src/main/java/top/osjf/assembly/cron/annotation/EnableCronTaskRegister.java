package top.osjf.assembly.cron.annotation;

import org.slf4j.LoggerFactory;
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
 * @see CronTaskRegister
 * @see AbstractCornRegister
 * @see org.springframework.context.ApplicationContext
 * @see org.springframework.core.env.Environment
 * @author zpf
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


    interface Logger {

        void info(String var);

        void info(String var1, Object... var2);

        void info(String var1, Throwable var2);

        void error(String var);

        void error(String var1, Object... var2);

        void error(String var1, Throwable var2);
    }

    class CronSlf4j implements Logger {

        private static final org.slf4j.Logger logger = LoggerFactory.getLogger(CronSlf4j.class);

        @Override
        public void info(String var) {
            logger.info(var);
        }

        @Override
        public void info(String var1, Object... var2) {
            logger.info(var1, var2);
        }

        @Override
        public void info(String var1, Throwable var2) {
            logger.info(var1, var2);
        }

        @Override
        public void error(String var) {
            logger.info(var);
        }

        @Override
        public void error(String var1, Object... var2) {
            logger.info(var1, var2);
        }

        @Override
        public void error(String var1, Throwable var2) {
            logger.info(var1, var2);
        }
    }
}
