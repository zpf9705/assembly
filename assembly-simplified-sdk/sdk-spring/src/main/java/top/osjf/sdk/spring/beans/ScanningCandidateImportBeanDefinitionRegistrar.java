/*
 * Copyright 2024-? the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package top.osjf.sdk.spring.beans;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
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
import org.springframework.core.type.filter.TypeFilter;
import top.osjf.sdk.commons.annotation.NotNull;

import java.util.Set;

/**
 * The processing class used to dynamically add beans during the startup
 * process of the Spring framework is a further encapsulation of
 * {@link ImportBeanDefinitionRegistrar} to achieve directional scanning.
 *
 * <p>To use the functionality of this encapsulation class, it is necessary
 * to provide the annotation type {@link #getImportAnnotationType()} that
 * triggers this configuration,which is used to determine the package path
 * selection for subsequent directed scanning.
 * If the above type is not provided, the package scanning will be carried
 * out according to the package where the main class is launched.
 *
 * <p>Subclass can customize scanning rules {@link #getScanningCandidateProvider()},
 * which must be provided. The scanned {@link BeanDefinition} will be handed over
 * to the subclass to determine whether it is valid {@link #isAvailableMarkedBeanDefinition(BeanDefinition)},
 * which is directly valid by default.
 *
 * <p>After the above steps, subclasses will be requested to construct a set
 * of information related to {@link BeanDefinition} based on the provided metadata
 * information, which will be passed on to the subsequent bean registration machine
 * {@link BeanDefinitionRegistry} for unified registration.
 * At this point, the dynamic registration process is completed.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
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

    /*** registration machine of the container bean.*/
    private BeanDefinitionRegistry registry;

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
        return Ordered.HIGHEST_PRECEDENCE + 1;
    }

    @Override
    protected void registerBeanDefinitions(AnnotationAttributes importAnnotationAttributes,
                                           @NotNull BeanDefinitionRegistry registry) {
        this.registry = registry;
        String[] scanningPackageNames;
        if (importAnnotationAttributes != null) {
            String scanPathAttributeName = getScanPathAttributeName();
            scanningPackageNames = importAnnotationAttributes.getStringArray(scanPathAttributeName);
            if (ArrayUtils.isEmpty(scanningPackageNames)) {
                scanningPackageNames = new String[]{getConfigClassMetadata().getPackageName()};
                if (log.isDebugEnabled()) {
                    log.debug("According to the attribute name {}, the scan path array value was not obtained.",
                            scanPathAttributeName);
                }
                if (log.isWarnEnabled()) {
                    log.warn("Unable to parse usable path information from import annotation, " +
                            "annotate the package path where the tag class is located is now used.");
                }
            }
        } else {
            scanningPackageNames = new String[]{getConfigClassMetadata().getPackageName()};
            if (log.isWarnEnabled()) {
                log.warn("Import annotation no provider, annotate the package path where the tag class is " +
                        "located is now used.");
            }
        }
        ClassPathScanningCandidateComponentProvider scanningCandidateProvider = getScanningCandidateProvider();
        for (String packageName : scanningPackageNames) {
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
     * @return Returns a {@link ClassPathScanningCandidateComponentProvider} that can
     * have the ability to search for independent and non interface classes under the
     * defined package, specifying a filter {@link TypeFilter}.
     */
    @NotNull
    protected abstract ClassPathScanningCandidateComponentProvider getScanningCandidateProvider();

    /**
     * Returns whether the {@link BeanDefinition} provided by the scan is
     * available for subclass conditions.
     *
     * @param markedBeanDefinition the {@link BeanDefinition} provided by the scan.
     * @return whether the {@link BeanDefinition} provided by the scan is
     * available for subclass conditions.
     */
    protected boolean isAvailableMarkedBeanDefinition(BeanDefinition markedBeanDefinition) {
        return true;
    }

    /**
     * Returns holder for a BeanDefinition with name and aliases.
     *
     * @param markedBeanDefinition Specify the {@link BeanDefinition} of the filter type tag.
     * @return Holder for a BeanDefinition with name and aliases.
     */
    protected abstract BeanDefinitionHolder createBeanDefinitionHolder(T markedBeanDefinition);

    /**
     * @return Run the environment object and leave it to subclasses to
     * obtain environment variables.
     */
    protected Environment getEnvironment() {
        return this.environment;
    }

    /**
     * @return Return the resource loader and hand it over to the subclass for use.
     */
    protected ResourceLoader getResourceLoader() {
        return resourceLoader;
    }

    /**
     * @return Return the registration machine of the container bean.
     */
    protected BeanDefinitionRegistry getBeanDefinitionRegistry() {
        return registry;
    }

    /**
     * Determine whether the given attribute is in the form of an el expression.
     *
     * @param property Attribute name.
     * @return If {@code true} is determined as an el expression , {@code false} otherwise.
     */
    protected boolean is$PropertyGet(String property) {
        if (StringUtils.isNotBlank(property)) {
            return property.startsWith(PLACEHOLDER_PREFIX) && property.endsWith(PLACEHOLDER_SUFFIX);
        }
        return false;
    }
}
