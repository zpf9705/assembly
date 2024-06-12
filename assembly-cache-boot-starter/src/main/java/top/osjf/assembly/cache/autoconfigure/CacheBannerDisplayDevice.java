package top.osjf.assembly.cache.autoconfigure;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.Banner;
import org.springframework.core.env.Environment;
import org.springframework.core.env.EnvironmentCapable;
import top.osjf.assembly.cache.CacheVersion;
import top.osjf.assembly.util.annotation.NotNull;

import java.io.PrintStream;

/**
 * Define the method for caching component logo output, implement
 * {@link Banner}, define relevant execution parameter acquisition
 * methods.
 * <p>Perform logo output after the initialization of the corresponding
 * bean is completed.
 *
 * @author zpf
 * @since 1.0.0
 */
public interface CacheBannerDisplayDevice extends InitializingBean, Banner, EnvironmentCapable {

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
     * Return a resource class object that can obtain the version.<br>
     * <p>If it is the original, it defaults to {@link CacheVersion} to
     * obtain the version of this project.
     *
     * @return must not be {@literal null}.
     */
    @NotNull
    default Class<?> getSourceClass() {
        return CacheVersion.class;
    }

    /**
     * Return a {@link StartUpBanner} implementation class.
     * @return the {@link StartUpBanner} associated with this assembly.
     */
    @NotNull
    StartUpBanner getStartUpBanner();
}
