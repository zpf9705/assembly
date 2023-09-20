package top.osjf.assembly.simplified.support;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.lang.NonNull;

import java.util.Objects;
import java.util.Set;

/**
 * <p>The implementation of information collection before the creation of the
 * spring container here,including the package path where the main class is started,
 * plays a crucial role in the scalability of the example package in the future.</p>
 *
 * @author zpf
 * @since 1.1.0
 */
public class SourceEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {

    private static String[] springbootPrimarySourcesPackages;

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        applicationSource(application.getAllSources());
    }

    @Override
    public int getOrder() {
        return Integer.MIN_VALUE + 10;
    }

    /**
     * Take the package path information where the springboot main class is located.
     *
     * @return According to {@code  org.springframework.boot.SpringApplication#primarySources},
     * there can be multiple main class paths and must not be {@literal  null}.
     */
    public static String[] findSpringbootPrimarySourcesPackages() {
        return springbootPrimarySourcesPackages;
    }

    /**
     * Obtain the spring boot startup main class information in {@link SourceEnvironmentPostProcessor}.
     * <p>
     * Before initializing the spring boot container, please refer to {@link SourceEnvironmentPostProcessor} and
     * learn about {@link org.springframework.boot.env.EnvironmentPostProcessor}.
     *
     * @param source {@link SpringApplication#getAllSources()}
     */
    private void applicationSource(@NonNull Set<Object> source) {
        if (source.isEmpty()) {
            throw new IllegalArgumentException("No detection of the existence of the startup main class");
        }
        springbootPrimarySourcesPackages = source.stream().map(o -> {
            if (o instanceof Class<?>) {
                return ((Class<?>) o).getName().split(
                        "\\." + ((Class<?>) o).getSimpleName())[0];
            }
            return null;
        }).filter(Objects::nonNull).toArray(String[]::new);
    }
}
