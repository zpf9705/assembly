package top.osjf.assembly.simplified.support;

import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.filter.TypeFilter;
import top.osjf.assembly.util.annotation.NotNull;
import top.osjf.assembly.util.lang.ArrayUtils;
import top.osjf.assembly.util.lang.CollectionUtils;
import top.osjf.assembly.util.lang.StringUtils;

import java.util.Set;

/**
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 2.2.5
 */
public abstract class ScanningCandidateImportBeanDefinitionRegistrar<T extends BeanDefinition>
        extends AbstractImportBeanDefinitionRegistrar implements EnvironmentAware, ResourceLoaderAware, Ordered {

    /*** Prefix for system property placeholders: "${". */
    protected static final String PLACEHOLDER_PREFIX = "${";

    /*** Suffix for system property placeholders: "}". */
    protected static final String PLACEHOLDER_SUFFIX = "}";

    /*** Value separator for system property placeholders: ":". */
    protected static final String VALUE_SEPARATOR = ":";

    /*** Environmental variables. */
    private Environment environment;

    /*** Resource processor. */
    private ResourceLoader resourceLoader;

    /*** 2.2.5 The default configuration triggers the name of the annotation path scan item property.*/
    protected static final String DEFAULT_SCAN_PATH_ATTRIBUTE_NAME = "value";

    @Override
    public void setEnvironment(@NotNull Environment environment) {
        this.environment = environment;
    }

    @Override
    public void setResourceLoader(@NotNull ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public int getOrder() {
        return Integer.MIN_VALUE + 11;
    }

    @Override
    protected void registerBeanDefinitions(@NotNull AnnotationAttributes importAnnotationAttributes,
                                           @NotNull BeanDefinitionRegistry registry) {

        String scanPathAttributeName = getScanPathAttributeName();
        String[] scanningPackageNames = importAnnotationAttributes.getStringArray(scanPathAttributeName);
        if (ArrayUtils.isEmpty(scanningPackageNames)) {
            if (log.isDebugEnabled()) {
                log.debug("According to the attribute name {}, the scan path array value was not obtained.",
                        scanPathAttributeName);
            }
            scanningPackageNames = new String[]{getMainApplicationClassPath()};
        }
        ClassPathScanningCandidateComponentProvider scanningCandidateProvider = getScanningCandidateProvider();
        for (String packageName : scanningPackageNames) {
            //Add configuration file compatible spel expression support to the specified path.
            if (is$PropertyGet(packageName)) {
                packageName = environment.resolvePlaceholders(packageName);
            }
            Set<BeanDefinition> markedBeanDefinitions = scanningCandidateProvider.findCandidateComponents(packageName);
            if (CollectionUtils.isEmpty(markedBeanDefinitions)) {
                continue;
            }
            for (BeanDefinition markedBeanDefinition : markedBeanDefinitions) {
                if (!isAvailableMarkedBeanDefinition(markedBeanDefinition)) {
                    continue;
                }
                @SuppressWarnings("unchecked")
                BeanDefinitionHolder holder = this.createBeanDefinitionHolder((T) markedBeanDefinition);
                if (holder != null) {
                    BeanDefinitionReaderUtils.registerBeanDefinition(holder, registry);
                }
            }
        }
    }

    /**
     * Return the scan path for the relevant categories of registered
     * beans that need to be searched.
     *
     * @return scan path for the relevant categories of registered
     * beans that need to be searched.
     */
    protected String getScanPathAttributeName() {
        return DEFAULT_SCAN_PATH_ATTRIBUTE_NAME;
    }

    /**
     * @see ClassMetadata#isIndependent()
     * @return Returns a {@link ClassPathScanningCandidateComponentProvider} that can
     * have the ability to search for independent and non interface classes under the
     * defined package, specifying a filter {@link TypeFilter}.
     */
    protected ClassPathScanningCandidateComponentProvider getScanningCandidateProvider() {
        ClassPathScanningCandidateComponentProvider classPathScan =
                new ClassPathScanningCandidateComponentProvider(false, this.environment) {
                    @Override
                    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
                        return beanDefinition.getMetadata().isIndependent() && !beanDefinition.getMetadata()
                                .isAnnotation();
                    }
                };
        classPathScan.setResourceLoader(this.resourceLoader);
        classPathScan.setResourcePattern("**/*.class");
        classPathScan.addIncludeFilter(getTypeFilter());
        return classPathScan;
    }

    /**
     * Returns the class filter used when scanning with the Spring class path scanner.
     *
     * @return the class filter used when scanning with the Spring class path scanner.
     */
    @NotNull
    protected abstract TypeFilter getTypeFilter();

    /**
     * Returns whether the {@link BeanDefinition} provided by the scan is
     * available for subclass conditions.
     *
     * @param beanDefinition the {@link BeanDefinition} provided by the scan.
     * @return whether the {@link BeanDefinition} provided by the scan is
     * available for subclass conditions.
     */
    protected boolean isAvailableMarkedBeanDefinition(BeanDefinition beanDefinition) {
        return true;
    }

    /**
     * Returns holder for a BeanDefinition with name and aliases.
     *
     * @param markedMarkedBeanDefinition Specify the {@link BeanDefinition} of the filter type tag.
     * @return Holder for a BeanDefinition with name and aliases.
     */
    protected abstract BeanDefinitionHolder createBeanDefinitionHolder(T markedMarkedBeanDefinition);

    /**
     * @return Run the environment object and leave it to subclasses to
     * obtain environment variables.
     */
    protected Environment getEnvironment() {
        return this.environment;
    }

    /**
     * Determine whether the given attribute is in the form of an el expression.
     *
     * @param property Attribute name.
     * @return If {@code true} is determined as an el expression , {@code false} otherwise.
     * @since 2.1.4
     */
    protected boolean is$PropertyGet(String property) {
        if (StringUtils.isNotBlank(property)) {
            return property.startsWith(PLACEHOLDER_PREFIX) && property.endsWith(PLACEHOLDER_SUFFIX);
        }
        return false;
    }
}
