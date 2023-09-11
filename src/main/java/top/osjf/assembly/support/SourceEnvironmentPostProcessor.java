package top.osjf.assembly.support;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import top.osjf.assembly.utils.ScanUtils;

/**
 * The implementation of information collection before the creation of the spring container here,
 * including the package path where the main class is started, plays a crucial role in the scalability of
 * the example package in the future.
 *
 * @author zpf
 * @since 1.1.0
 */
public class SourceEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        ScanUtils.applicationSource(application.getAllSources());
    }

    @Override
    public int getOrder() {
        return Integer.MIN_VALUE + 10;
    }
}
