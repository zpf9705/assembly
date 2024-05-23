package top.osjf.assembly.simplified.support;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import top.osjf.assembly.util.lang.CollectionUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * When initializing startup using {@link EnvironmentPostProcessor}, obtain the package path
 * where the main startup class is located.
 *
 * <p>Provide default values for scanning paths.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 2.2.5
 */
public class EmbeddedEnvironmentPostProcessor implements EnvironmentPostProcessor {

    private static ConfigurableEnvironment embeddedEnvironment;

    private static SpringApplication embeddedSpringApplication;

    private static final Set<String> embeddedPackageNames = new HashSet<>();

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        embeddedEnvironment = environment;
        embeddedSpringApplication = application;
    }

    /**
     * @return Return the {@code Environment} for this application context in configurable
     * form, allowing for further customization.
     */
    public static ConfigurableEnvironment getEmbeddedEnvironment() {
        return embeddedEnvironment;
    }

    /**
     * @return Return the Spring main application Object {@link SpringApplication}.
     */
    public static SpringApplication getEmbeddedSpringApplication() {
        return embeddedSpringApplication;
    }

    /**
     * @return Return the acquisition of built-in main class, main configuration
     * class, and related resource package names.
     * <p>SpringApplication#primarySources
     * @see SpringApplication#getMainApplicationClass()
     * @see SpringApplication#getAllSources()
     * @see SpringApplication#getSources()
     */
    public static String[] getEmbeddedPackageNameArray() {
        return getEmbeddedPackageNames().toArray(new String[]{});
    }

    /**
     * @return Return the acquisition of built-in main class, main configuration
     * class, and related resource package names.
     * <p>SpringApplication#primarySources
     * @see SpringApplication#getMainApplicationClass()
     * @see SpringApplication#getAllSources()
     * @see SpringApplication#getSources()
     */
    public static Set<String> getEmbeddedPackageNames() {
        if (CollectionUtils.isEmpty(embeddedPackageNames)) {
            synchronized (embeddedPackageNames) {
                Class<?> mainApplicationClass = embeddedSpringApplication.getMainApplicationClass();
                if (mainApplicationClass != null) {
                    embeddedPackageNames.add(mainApplicationClass.getPackage().getName());
                }
                for (Object source : embeddedSpringApplication.getAllSources()) {
                    if (source instanceof Class) {
                        embeddedPackageNames.add(((Class<?>) source).getPackage().getName());
                    } else if (source instanceof String) {
                        Package ak = Package.getPackage((String) source);
                        if (ak != null) {
                            embeddedPackageNames.add(ak.getName());
                        }
                    }
                }
            }
        }
        return embeddedPackageNames;
    }
}
