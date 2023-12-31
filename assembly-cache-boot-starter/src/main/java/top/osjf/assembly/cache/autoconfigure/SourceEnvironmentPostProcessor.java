package top.osjf.assembly.cache.autoconfigure;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import top.osjf.assembly.util.annotation.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>The implementation of information collection before the creation of the
 * spring container here,including the package path where the main class is started,
 * plays a crucial role in the scalability of the example package in the future.</p>
 *
 * @author zpf
 * @since 1.1.0
 */
public class SourceEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {

    private static List<String> springbootPrimarySourcesPackages;

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        applicationSource(application.getAllSources());
    }

    @Override
    public int getOrder() {
        return Integer.MIN_VALUE + 13;
    }

    /**
     * Take the package path information where the springboot main class is located.
     *
     * @return According to {@code  org.springframework.boot.SpringApplication#primarySources},
     * there can be multiple main class paths and must not be {@literal  null}.
     */
    public static List<String> findSpringbootPrimarySourcesPackages() {
        return springbootPrimarySourcesPackages;
    }

    /**
     * Obtain the spring boot startup main class information in {@link SourceEnvironmentPostProcessor}.
     *
     * <p>Before initializing the spring boot container, please refer to {@link SourceEnvironmentPostProcessor} and
     * learn about {@link EnvironmentPostProcessor}.
     *
     * @param source {@link SpringApplication#getAllSources()}
     */
    private void applicationSource(@NotNull Set<Object> source) {
        if (source.isEmpty()) {
            throw new IllegalArgumentException("No detection of the existence of the startup main class");
        }
        springbootPrimarySourcesPackages = source.stream().map(o -> {
            if (o instanceof Class<?>) {
                return ((Class<?>) o).getName().split(
                        "\\." + ((Class<?>) o).getSimpleName())[0];
            }
            return null;
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }
}
