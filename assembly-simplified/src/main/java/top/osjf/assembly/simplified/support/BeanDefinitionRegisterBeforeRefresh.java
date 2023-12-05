package top.osjf.assembly.simplified.support;

import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.Assert;
import top.osjf.assembly.util.annotation.NotNull;
import top.osjf.assembly.util.lang.ArrayUtils;
import top.osjf.assembly.util.lang.CollectionUtils;
import top.osjf.assembly.util.lang.StringUtils;

import java.lang.annotation.Annotation;
import java.util.Set;

/**
 * Manually register objects into the support classes of the Spring container.
 *
 * <p>With annotation usage as the core, the general process is to query and annotate
 * an interface class for a certain annotation, construct a {@link BeanDefinition} based
 * on the information, and inject it into the spring container.
 *
 * <p>This process can be enabled using an annotation switch.
 *
 * <p>For example, when the springboot program starts, if it detects that the container bean is
 * wearing {@link top.osjf.assembly.simplified.sdk.annotation.EnableSdkProxyRegister} annotations,
 * it will automatically search for the wearing annotation {@link top.osjf.assembly.simplified.sdk.annotation.Sdk}
 * interface in this class and create an implementation class (refer to {@link AbstractJdkProxySupport}).
 *
 * <p>The implementation process depends on the bean's registrar interface {@link ImportBeanDefinitionRegistrar}.
 *
 * <p>The lookup of annotated interfaces depends on the interface {@link ResourceLoader} and
 * {@link ClassPathScanningCandidateComponentProvider}.
 *
 * <p>Innovated in version {@code 2.0.9}, this registration scheme does not participate
 * in calling services such as {@link org.springframework.beans.factory.annotation.Autowired}
 * before spring bean registration, and is executed as a registration scheme here.
 *
 * @author zpf
 * @since 1.1.0
 */
public abstract class BeanDefinitionRegisterBeforeRefresh extends AbstractImportBeanDefinitionRegistrar
        implements EnvironmentAware, ResourceLoaderAware, Ordered {

    /*** Prefix for system property placeholders: "${". */
    protected static final String PLACEHOLDER_PREFIX = "${";

    /*** Suffix for system property placeholders: "}". */
    protected static final String PLACEHOLDER_SUFFIX = "}";

    /*** Value separator for system property placeholders: ":". */
    protected static final String VALUE_SEPARATOR = ":";

    private Environment environment;

    private ResourceLoader resourceLoader;

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
    protected void process(AnnotationAttributes attributes, BeanDefinitionRegistry registry) {

        //Obtain Scan Path
        //if blank default to use value
        String scanPathAttributeName = getScanPathAttributeName();
        if (StringUtils.isBlank(scanPathAttributeName)) {
            scanPathAttributeName = "value";
        }
        String[] basePackages = attributes.getStringArray(scanPathAttributeName);
        if (ArrayUtils.isEmpty(basePackages)) {

            //not support default to use main class path
            basePackages = new String[]{getMainApplicationClassPath()};
        }

        //Obtain Path Scan Provider
        ClassPathScanningCandidateComponentProvider classPathScan = getCandidateComponentsScanProvider();

        //The class annotated by the subcontracting scanning target
        for (String basePackage : basePackages) {

            //Supports obtaining el expressions.
            if (is$PropertyGet(basePackage)) {

                //Directly obtain without judging whether it can be obtained,
                // and do not throw exceptions for configuration not found.
                basePackage = environment.resolvePlaceholders(basePackage);
            }
            Set<BeanDefinition> candidateComponents = classPathScan.findCandidateComponents(basePackage);
            if (CollectionUtils.isEmpty(candidateComponents)) {
                continue;
            }

            //Perform proxy registration for each bean
            for (BeanDefinition beanDefinition : candidateComponents) {
                if (!(beanDefinition instanceof AnnotatedBeanDefinition)) {
                    continue;
                }
                AnnotatedBeanDefinition adi = (AnnotatedBeanDefinition) beanDefinition;

                //Obtain annotation class identification interface
                AnnotationMetadata meta = adi.getMetadata();
                if (!meta.isInterface()) {
                    continue;
                }

                //Obtain annotation class identification interface annotation value domain
                AnnotationAttributes attributesFu = AnnotationAttributes
                        .fromMap(meta.getAnnotationAttributes(getFilterAnnotationClass().getCanonicalName()));
                if (attributesFu == null) {
                    continue;
                }

                //let son class take BeanDefinition
                BeanDefinitionHolder holder = this.getBeanDefinitionHolder(attributesFu, meta);

                //bean register
                if (holder != null) {
                    BeanDefinitionReaderUtils.registerBeanDefinition(holder, registry);
                }
            }
        }
    }

    /**
     * Define the registration behavior of beans based on all attributes.
     *
     * @param attributes Annotation value Range.
     * @param meta       Associated annotation interface.
     * @return A wait register container BeanDefinition.
     */
    protected BeanDefinitionHolder getBeanDefinitionHolder(AnnotationAttributes attributes, AnnotationMetadata meta) {
        return null;
    }

    /**
     * Obtain class path scanning candidate component providers based on conditions.
     *
     * @return {@link ClassPathScanningCandidateComponentProvider}.
     */
    @NotNull
    protected ClassPathScanningCandidateComponentProvider getCandidateComponentsScanProvider() {
        Class<? extends Annotation> filterAnnotationClass = getFilterAnnotationClass();
        Assert.notNull(filterAnnotationClass, "filterAnnotationClass not be null");
        ClassPathScanningCandidateComponentProvider classPathScan =
                new ClassPathScanningCandidateComponentProvider(false, this.environment) {
                    @Override
                    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
                        return beanDefinition.getMetadata().isIndependent()
                                && !beanDefinition.getMetadata().isAnnotation();
                    }
                };
        classPathScan.setResourceLoader(this.resourceLoader);
        //scan all class type
        classPathScan.setResourcePattern("**/*.class");
        classPathScan.addIncludeFilter(new AnnotationTypeFilter(filterAnnotationClass));
        return classPathScan;
    }

    /**
     * Provide class objects for annotation of annotation classes.
     *
     * @return must not be {@literal null}.
     */
    @NotNull
    protected abstract Class<? extends Annotation> getFilterAnnotationClass();

    /**
     * Provide path scan variable identification, default to {@code value}.
     *
     * @return must not be {@literal null}.
     */
    @NotNull
    protected String getScanPathAttributeName() {
        return "value";
    }

    /**
     * @return Return the {@link Environment} associated with this component.
     */
    protected Environment getEnvironment() {
        return this.environment;
    }

    /**
     * Determine whether the given attribute is in the form of an el expression.
     * @param property Attribute name.
     * @return If {@code true} is determined as an el expression , {@code false} otherwise.
     */
    //@since 2.1.4
    protected boolean is$PropertyGet(String property) {
        if (StringUtils.isNotBlank(property)) {
            return property.startsWith(PLACEHOLDER_PREFIX) && property.endsWith(PLACEHOLDER_SUFFIX);
        }
        return false;
    }
}
