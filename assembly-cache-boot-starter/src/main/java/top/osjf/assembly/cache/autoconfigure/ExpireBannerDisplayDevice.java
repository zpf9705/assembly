package top.osjf.assembly.cache.autoconfigure;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.Banner;
import org.springframework.core.env.Environment;
import org.springframework.core.env.EnvironmentCapable;
import top.osjf.assembly.util.annotation.NotNull;

import java.io.PrintStream;

/**
 * Here is the logo printing method for classification of program interface,
 * mainly through the output of the {@code System} fixed pattern and print in injection class initialization
 *
 * <pre>
 *  If override {@code afterPropertiesSet} need to perform {@code printBanner}
 * </pre>
 *
 * @author zpf
 * @since 3.0.0
 */
public interface ExpireBannerDisplayDevice extends InitializingBean, Banner, EnvironmentCapable {

    @Override
    default void afterPropertiesSet() {
        //print client banner
        printBanner(getEnvironment(), getSourceClass(), System.out);
    }

    void printBanner(Environment environment, Class<?> sourceClass, PrintStream out);

    @Override
    @NotNull
    Environment getEnvironment();

    /**
     * Get banner source class
     *
     * @return a using banner class type no be {@literal null}
     */
    @NotNull
    Class<?> getSourceClass();

    /**
     * Using banner start up banner
     *
     * @return a {@link StartUpBanner} no be {@literal null}
     */
    @NotNull
    StartUpBanner getStartUpBanner();
}
